package com.romnan.dicodingstory.core.layers.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.romnan.dicodingstory.core.layers.data.room.dao.StoryDao
import com.romnan.dicodingstory.core.layers.data.room.dao.StoryRemoteKeysDao
import com.romnan.dicodingstory.core.layers.data.room.entity.StoryEntity
import com.romnan.dicodingstory.core.layers.data.room.entity.StoryRemoteKeysEntity

@Database(
    entities = [StoryEntity::class, StoryRemoteKeysEntity::class],
    version = 2,
    exportSchema = false
)
abstract class CoreDatabase : RoomDatabase() {
    abstract val storyDao: StoryDao
    abstract val storyRemoteKeysDao: StoryRemoteKeysDao

    companion object {
        const val NAME = "db_dicoding_story"
    }
}