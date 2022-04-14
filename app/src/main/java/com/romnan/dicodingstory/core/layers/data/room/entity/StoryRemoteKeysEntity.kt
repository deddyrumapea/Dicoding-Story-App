package com.romnan.dicodingstory.core.layers.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StoryRemoteKeysEntity(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)