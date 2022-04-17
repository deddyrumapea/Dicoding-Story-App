package com.romnan.dicodingstory.core.layers.data.repository

import androidx.paging.PagingData
import com.romnan.dicodingstory.core.layers.domain.model.Story
import com.romnan.dicodingstory.core.layers.domain.repository.CoreRepository
import com.romnan.dicodingstory.core.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCoreRepository(
    private val allStories: Resource<List<Story>>? = null,
    private val pagedStories: PagingData<Story>? = null,
    private val storiesWithLatLong: Resource<List<Story>>? = null
) : CoreRepository {

    override fun getAllStories(): Flow<Resource<List<Story>>> = flow {
        allStories?.let { emit(it) }
    }

    override fun getPagedStories(): Flow<PagingData<Story>> = flow {
        pagedStories?.let { emit(it) }
    }

    override fun getStoriesWithLatLong(
        maxPage: Int,
        pageSize: Int
    ): Flow<Resource<List<Story>>> = flow {
        storiesWithLatLong?.let { emit(it) }
    }
}