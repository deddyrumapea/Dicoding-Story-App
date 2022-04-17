package com.romnan.dicodingstory.features.addStory.data.repository

import android.net.Uri
import com.romnan.dicodingstory.core.util.SimpleResource
import com.romnan.dicodingstory.features.addStory.domain.model.NewStory
import com.romnan.dicodingstory.features.addStory.domain.repository.AddStoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class FakeAddStoryRepository(
    private val uploadStoryResource: SimpleResource? = null,
    private var newTempJpegUri: Uri? = null,
    private var foundJpegFile: File? = null
) : AddStoryRepository {
    override fun uploadStory(newStory: NewStory): Flow<SimpleResource> = flow {
        uploadStoryResource?.let { emit(it) }
    }

    override suspend fun getNewTempJpegUri(): Uri =
        newTempJpegUri ?: throw NullPointerException()

    override suspend fun findJpegByUri(uri: Uri): File =
        foundJpegFile ?: throw  NullPointerException()
}