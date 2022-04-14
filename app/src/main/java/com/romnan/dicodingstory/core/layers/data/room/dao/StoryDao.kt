package com.romnan.dicodingstory.core.layers.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.romnan.dicodingstory.core.layers.data.room.entity.StoryEntity

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(stories: List<StoryEntity>)

    @Query("SELECT * FROM storyentity")
    fun getAll(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM storyentity")
    suspend fun deleteAll()
}