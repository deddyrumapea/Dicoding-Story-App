package com.romnan.dicodingstory.features.addStory.data.remote

import com.romnan.dicodingstory.features.addStory.data.model.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AddStoryApi {
    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") bearerToken: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): AddStoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") bearerToken: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ): AddStoryResponse
}