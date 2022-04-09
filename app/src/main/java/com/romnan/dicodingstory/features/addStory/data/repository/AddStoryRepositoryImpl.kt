package com.romnan.dicodingstory.features.addStory.data.repository

import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.SimpleResource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.addStory.data.remote.AddStoryApi
import com.romnan.dicodingstory.features.addStory.domain.model.NewStory
import com.romnan.dicodingstory.features.addStory.domain.repository.AddStoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

class AddStoryRepositoryImpl(
    private val api: AddStoryApi,
    private val prefRepo: PreferencesRepository
) : AddStoryRepository {

    override fun uploadStory(newStory: NewStory): Flow<SimpleResource> = flow {
        emit(Resource.Loading())

        try {
            val loginResult = prefRepo.getAppPreferences().first().loginResult
            val bearerToken = "Bearer ${loginResult.token}"

            val rbDescription = newStory.description.toRequestBody("text/plain".toMediaType())
            val rbPhoto = newStory.photo.asRequestBody("image/jpeg".toMediaTypeOrNull())

            val photoMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                name = "photo",
                filename = newStory.photo.name,
                body = rbPhoto
            )

            val response = api.uploadStory(
                bearerToken = bearerToken,
                photo = photoMultipart,
                description = rbDescription
            )
            when {
                response.error != true -> emit(Resource.Success(Unit))
                response.message != null -> emit(
                    Resource.Error(UIText.DynamicString(response.message))
                )
                else -> emit(Resource.Error(UIText.StringResource(R.string.em_unknown)))
            }

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