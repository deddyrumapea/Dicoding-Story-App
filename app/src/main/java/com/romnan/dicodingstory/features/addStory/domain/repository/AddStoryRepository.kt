package com.romnan.dicodingstory.features.addStory.domain.repository

import android.net.Uri
import com.romnan.dicodingstory.core.util.SimpleResource
import com.romnan.dicodingstory.features.addStory.domain.model.NewStory
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AddStoryRepository {
    fun uploadStory(newStory: NewStory): Flow<SimpleResource>
    suspend fun getNewTempJpegUri(): Uri
    suspend fun findJpegByUri(uri: Uri): File
    suspend fun getNewTempJpeg(): File
}