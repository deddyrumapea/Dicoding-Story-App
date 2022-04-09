package com.romnan.dicodingstory.features.addStory.presentation.model

import android.graphics.Bitmap
import android.net.Uri

sealed class StoryPreview {
    data class BitmapImage(val bitmap: Bitmap) : StoryPreview()
    data class ImageUri(val uri: Uri) : StoryPreview()
}
