package com.romnan.dicodingstory.core.layers.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.romnan.dicodingstory.core.layers.data.room.entity.StoryRemoteKeysEntity

@Dao
interface StoryRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(remoteKeys: List<StoryRemoteKeysEntity>)

    @Query("SELECT * FROM storyremotekeysentity WHERE id = :id")
    suspend fun getById(id: String): StoryRemoteKeysEntity?

    @Query("DELETE FROM storyremotekeysentity")
    suspend fun deleteAll()
}