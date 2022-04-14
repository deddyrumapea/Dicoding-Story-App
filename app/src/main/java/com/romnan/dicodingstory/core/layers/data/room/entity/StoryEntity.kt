package com.romnan.dicodingstory.core.layers.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.romnan.dicodingstory.core.layers.domain.model.Story

@Entity
data class StoryEntity(
    @PrimaryKey val id: String,
    val createdAt: String,
    val name: String,
    val photoUrl: String,
    val description: String,
    val lat: Double,
    val lon: Double
) {
    constructor(story: Story) : this(
        id = story.id ?: "",
        createdAt = story.createdAt ?: "",
        name = story.name ?: "",
        photoUrl = story.photoUrl ?: "",
        description = story.description ?: "",
        lat = story.lat ?: 0.0,
        lon = story.lon ?: 0.0
    )

    fun toStory() = Story(
        id = id,
        createdAt = createdAt,
        name = name,
        photoUrl = photoUrl,
        description = description,
        lat = lat,
        lon = lon
    )
}