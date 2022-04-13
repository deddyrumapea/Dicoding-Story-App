package com.romnan.dicodingstory.core.layers.data.remote

import com.romnan.dicodingstory.core.layers.data.model.GetAllStoriesDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CoreApi {
    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") bearerToken: String
    ): GetAllStoriesDto

    @GET("stories")
    suspend fun getPagedStories(
        @Header("Authorization") bearerToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetAllStoriesDto
}