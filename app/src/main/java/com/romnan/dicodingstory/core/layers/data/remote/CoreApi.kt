package com.romnan.dicodingstory.core.layers.data.remote

import com.romnan.dicodingstory.core.layers.data.model.GetAllStoriesDto
import retrofit2.http.GET
import retrofit2.http.Header

interface CoreApi {

    //TODO: add page and size as parameters
    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") bearerToken: String
    ): GetAllStoriesDto
}