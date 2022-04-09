package com.romnan.dicodingstory.features.addStory.domain.repository

import com.romnan.dicodingstory.core.util.SimpleResource
import com.romnan.dicodingstory.features.addStory.domain.model.NewStory
import kotlinx.coroutines.flow.Flow

interface AddStoryRepository {
    fun uploadStory(newStory: NewStory): Flow<SimpleResource>
}