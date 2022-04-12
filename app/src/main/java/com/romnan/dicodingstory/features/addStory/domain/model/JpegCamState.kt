package com.romnan.dicodingstory.features.addStory.domain.model

import android.net.Uri

sealed class JpegCamState {
    object Closed : JpegCamState()
    object Opening : JpegCamState()
    data class Opened(val tempJpegUri: Uri) : JpegCamState()
}