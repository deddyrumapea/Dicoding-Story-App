package com.romnan.dicodingstory.features.addStory.domain.model

import java.io.File

data class NewStory(
    val description: String,
    val photo: File,
    val lat: Float? = null,
    val lon: Float? = null
)