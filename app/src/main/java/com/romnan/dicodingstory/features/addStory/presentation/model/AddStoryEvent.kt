package com.romnan.dicodingstory.features.addStory.presentation.model

import android.net.Uri

sealed class AddStoryEvent {
    data class UploadImage(val description: String) : AddStoryEvent()
    object OpenCamera : AddStoryEvent()
    object ImageCaptured : AddStoryEvent()
    data class ImageSelected(val selectedJpegUri: Uri) : AddStoryEvent()
}