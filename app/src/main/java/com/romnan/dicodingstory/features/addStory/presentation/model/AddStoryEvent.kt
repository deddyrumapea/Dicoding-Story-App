package com.romnan.dicodingstory.features.addStory.presentation.model

import android.location.Location
import android.net.Uri

sealed class AddStoryEvent {
    object LaunchCamera : AddStoryEvent()
    object ImageCaptured : AddStoryEvent()
    data class ImageSelected(val selectedJpegUri: Uri) : AddStoryEvent()
    data class UploadImage(val description: String) : AddStoryEvent()
    data class AddLocation(val location: Location) : AddStoryEvent()
}