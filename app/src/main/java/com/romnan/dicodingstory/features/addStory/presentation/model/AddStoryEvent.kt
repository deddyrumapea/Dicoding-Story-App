package com.romnan.dicodingstory.features.addStory.presentation.model

import com.romnan.dicodingstory.features.addStory.domain.model.NewStory

sealed class AddStoryEvent {
    data class UploadImage(val newStory: NewStory) : AddStoryEvent()
}