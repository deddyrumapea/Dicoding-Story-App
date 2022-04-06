package com.romnan.dicodingstory.features.home.data.model

import com.romnan.dicodingstory.features.home.domain.model.Story

data class GetAllStoriesDto(
    val error: Boolean?,
    val listStory: List<Story>?,
    val message: String?
)