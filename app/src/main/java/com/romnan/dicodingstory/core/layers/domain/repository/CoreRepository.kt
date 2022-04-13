package com.romnan.dicodingstory.core.layers.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.romnan.dicodingstory.core.layers.domain.model.Story
import com.romnan.dicodingstory.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface CoreRepository {
    fun getAllStories(): Flow<Resource<List<Story>>>
    fun getPagedStories(): Flow<PagingData<Story>>
}