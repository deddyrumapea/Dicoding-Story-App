package com.romnan.dicodingstory.core.layers.domain.repository

import androidx.paging.PagingData
import com.romnan.dicodingstory.core.layers.domain.model.Story
import com.romnan.dicodingstory.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface CoreRepository {
    fun getStories(): Flow<Resource<List<Story>>>
    fun getPagedStories(): Flow<PagingData<Story>>
    fun getStoriesWithLocation(): Flow<Resource<List<Story>>>
}