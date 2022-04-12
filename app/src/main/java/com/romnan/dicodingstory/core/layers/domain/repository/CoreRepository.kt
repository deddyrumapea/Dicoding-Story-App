package com.romnan.dicodingstory.core.layers.domain.repository

import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.layers.domain.model.Story
import kotlinx.coroutines.flow.Flow

interface CoreRepository {
    fun getAllStories(): Flow<Resource<List<Story>>>
}