package com.romnan.dicodingstory.core.layers.domain.model

data class Story(
    val id: String?,
    val createdAt: String?,
    val name: String?,
    val photoUrl: String?,
    val description: String?,
    val lat: Double?,
    val lon: Double?,
)