package com.romnan.dicodingstory.features.home.domain.repository

import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.features.home.domain.model.Story
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getAllStories(): Flow<Resource<List<Story>>>
}