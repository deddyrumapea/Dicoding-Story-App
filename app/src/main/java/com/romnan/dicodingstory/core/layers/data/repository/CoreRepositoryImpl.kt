package com.romnan.dicodingstory.core.layers.data.repository

import androidx.paging.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.data.paging.StoriesRemoteMediator
import com.romnan.dicodingstory.core.layers.data.retrofit.CoreApi
import com.romnan.dicodingstory.core.layers.data.room.dao.StoryDao
import com.romnan.dicodingstory.core.layers.domain.model.Story
import com.romnan.dicodingstory.core.layers.domain.repository.CoreRepository
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.login.data.model.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class CoreRepositoryImpl(
    private val dao: StoryDao,
    private val api: CoreApi,
    private val prefRepo: PreferencesRepository,
    private val storiesRemoteMediator: StoriesRemoteMediator
) : CoreRepository {
    override fun getAllStories(): Flow<Resource<List<Story>>> = flow {

        emit(Resource.Loading(emptyList()))

        try {
            val loginResult = prefRepo.getAppPreferences().first().loginResult
            val bearerToken = "Bearer ${loginResult.token}"
            val response = api.getAllStories(bearerToken = bearerToken)

            if (response.listStory?.isEmpty() == true) {
                emit(Resource.Error(UIText.StringResource(R.string.em_stories_empty)))
            } else {
                emit(Resource.Success(response.listStory))
            }

        } catch (t: Throwable) {
            val errorUiText: UIText = when (t) {
                is HttpException -> {
                    try {
                        val response = Gson().fromJson<LoginResponse>(
                            t.response()?.errorBody()?.charStream(),
                            object : TypeToken<LoginResponse>() {}.type
                        )
                        UIText.DynamicString(response.message!!)
                    } catch (e: Exception) {
                        UIText.StringResource(R.string.em_unknown)
                    }
                }
                is IOException -> UIText.StringResource(R.string.em_io_exception)
                else -> UIText.StringResource(R.string.em_unknown)
            }
            emit(Resource.Error(errorUiText))
        }
    }

    override fun getStoriesWithLatLong(
        maxPage: Int,
        pageSize: Int
    ): Flow<Resource<List<Story>>> =
        flow {

            emit(Resource.Loading(emptyList()))

            try {
                val loginResult = prefRepo.getAppPreferences().first().loginResult
                val bearerToken = "Bearer ${loginResult.token}"

                val storiesList = mutableListOf<Story>()

                for (page in 0 until maxPage) {
                    val response = api.getPagedStories(
                        bearerToken = bearerToken,
                        page = page,
                        size = pageSize
                    )
                    if (response.listStory == null || response.listStory.isEmpty()) break
                    storiesList.addAll(response.listStory.filter { it.lat != null && it.lon != null })
                }

                if (storiesList.isEmpty()) {
                    emit(Resource.Error(UIText.StringResource(R.string.em_stories_empty)))
                } else {
                    emit(Resource.Success(storiesList))
                }

            } catch (t: Throwable) {
                val errorUiText: UIText = when (t) {
                    is HttpException -> {
                        try {
                            val response = Gson().fromJson<LoginResponse>(
                                t.response()?.errorBody()?.charStream(),
                                object : TypeToken<LoginResponse>() {}.type
                            )
                            UIText.DynamicString(response.message!!)
                        } catch (e: Exception) {
                            UIText.StringResource(R.string.em_unknown)
                        }
                    }
                    is IOException -> UIText.StringResource(R.string.em_io_exception)
                    else -> UIText.StringResource(R.string.em_unknown)
                }
                emit(Resource.Error(errorUiText))
            }
        }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedStories(): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = storiesRemoteMediator,
            pagingSourceFactory = { dao.getAll() }
        ).flow.map { pagingData ->
            pagingData.map { it.toStory() }
        }
    }
}