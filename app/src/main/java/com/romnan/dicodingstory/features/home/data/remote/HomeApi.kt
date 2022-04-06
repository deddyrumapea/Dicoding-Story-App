package com.romnan.dicodingstory.features.home.data.remote

import com.romnan.dicodingstory.features.home.data.model.GetAllStoriesDto
import retrofit2.http.GET
import retrofit2.http.Header

interface HomeApi {

    //TODO: add page and size as parameters
    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") bearerToken: String
    ): GetAllStoriesDto
}