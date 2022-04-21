package com.romnan.dicodingstory.features.addStory.data.remote

import com.romnan.dicodingstory.features.addStory.data.model.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeAddStoryApi(
    private val addStoryResponse: AddStoryResponse? = null,
    private val exception: Exception? = null
) : AddStoryApi {
    override suspend fun uploadStory(
        bearerToken: String,
        photo: MultipartBody.Part,
        description: RequestBody
    ): AddStoryResponse {
        return addStoryResponse ?: throw exception!!
    }

    override suspend fun uploadStory(
        bearerToken: String,
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ): AddStoryResponse {
        return addStoryResponse ?: throw exception!!
    }

}