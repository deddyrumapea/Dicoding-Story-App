package com.romnan.dicodingstory.core.layers.data.retrofit

import com.romnan.dicodingstory.core.layers.data.retrofit.model.GetStoriesResponse
import com.romnan.dicodingstory.core.layers.domain.model.Story

class FakeCoreApi(
    private val stories: GetStoriesResponse? = null,
    private val pagedStories: GetStoriesResponse? = null
) : CoreApi {

    override suspend fun getStories(bearerToken: String, withLocation: Int): GetStoriesResponse {
        return stories ?: throw NullPointerException()
    }

    override suspend fun getPagedStories(
        bearerToken: String,
        page: Int,
        size: Int
    ): GetStoriesResponse {
        val result = mutableListOf<Story>()

        val startIndex = page * size
        val endIndex = ((page + 1) * size) - 1

        for (i in startIndex..endIndex) {
            pagedStories?.listStory?.get(i)?.let { result.add(it) }
        }

        return pagedStories?.copy(listStory = result) ?: throw NullPointerException()
    }
}