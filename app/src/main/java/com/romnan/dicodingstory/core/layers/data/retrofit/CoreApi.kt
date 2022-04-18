package com.romnan.dicodingstory.core.layers.data.retrofit

import com.romnan.dicodingstory.core.layers.data.retrofit.model.GetStoriesResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CoreApi {
    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") bearerToken: String,
        @Query("location") withLocation: Int = CoreApiParamValues.WITH_LOCATION_FALSE
    ): GetStoriesResponse

    @GET("stories")
    suspend fun getPagedStories(
        @Header("Authorization") bearerToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetStoriesResponse
}