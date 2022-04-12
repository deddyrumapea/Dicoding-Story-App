package com.romnan.dicodingstory.core.layers.data.model

import com.romnan.dicodingstory.core.layers.domain.model.Story

data class GetAllStoriesDto(
    val error: Boolean?,
    val listStory: List<Story>?,
    val message: String?
)