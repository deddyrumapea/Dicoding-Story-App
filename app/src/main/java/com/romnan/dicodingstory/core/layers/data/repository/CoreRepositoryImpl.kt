package com.romnan.dicodingstory.core.layers.data.repository

import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.core.layers.data.remote.CoreApi
import com.romnan.dicodingstory.core.layers.domain.model.Story
import com.romnan.dicodingstory.core.layers.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class CoreRepositoryImpl(
    private val api: CoreApi,
    private val prefRepo: PreferencesRepository
) : CoreRepository {
    override fun getAllStories(): Flow<Resource<List<Story>>> = flow {
        emit(Resource.Loading(emptyList()))
        try {
            val loginResult = prefRepo.getAppPreferences().first().loginResult
            val bearerToken = "Bearer ${loginResult.token}"
            val response = api.getAllStories(bearerToken = bearerToken)
            emit(Resource.Success(response.listStory))
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    emit(Resource.Error(UIText.StringResource(R.string.em_http_exception)))
                }
                is IOException -> emit(
                    Resource.Error(UIText.StringResource(R.string.em_io_exception))
                )
                else -> emit(Resource.Error(UIText.StringResource(R.string.em_unknown)))
            }
        }
    }
}