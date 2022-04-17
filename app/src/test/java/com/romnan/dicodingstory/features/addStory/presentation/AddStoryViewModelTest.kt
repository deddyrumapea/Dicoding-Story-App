package com.romnan.dicodingstory.features.addStory.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import com.google.common.truth.Truth.assertThat
import com.romnan.dicodingstory.util.Faker
import com.romnan.dicodingstory.util.MainCoroutineRule
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.util.TestErrorMsg
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.addStory.data.repository.FakeAddStoryRepository
import com.romnan.dicodingstory.features.addStory.domain.model.JpegCamState
import com.romnan.dicodingstory.features.addStory.presentation.model.AddStoryEvent
import com.romnan.dicodingstory.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.TimeoutException

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AddStoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `Succeed uploading story, isUploaded = true`() {
        val fakeAddStoryRepository = FakeAddStoryRepository(
            uploadStoryResource = Resource.Success(Unit)
        )
        val addStoryViewModel = AddStoryViewModel(fakeAddStoryRepository)
        addStoryViewModel.onEvent(AddStoryEvent.UploadImage(description = Faker.getLorem()))

        assertThat(addStoryViewModel.isUploaded.getOrAwaitValue()).isTrue()
    }

    @Test
    fun `Succeed uploading story, errorMessage was never set`() {
        val fakeAddStoryRepository = FakeAddStoryRepository(
            uploadStoryResource = Resource.Success(Unit)
        )
        val addStoryViewModel = AddStoryViewModel(fakeAddStoryRepository)
        addStoryViewModel.onEvent(AddStoryEvent.UploadImage(description = Faker.getLorem()))

        try {
            assertThat(addStoryViewModel.errorMessage.getOrAwaitValue()).isNull()
        } catch (t: Throwable) {
            assertThat(t).isInstanceOf(TimeoutException::class.java)
            assertThat(t.message).isEqualTo(TestErrorMsg.LIVEDATA_VALUE_WAS_NEVER_SET)
        }
    }

    @Test
    fun `Failed uploading story, isUploaded = false`() {
        val fakeAddStoryRepository = FakeAddStoryRepository(
            uploadStoryResource = Resource.Error(UIText.StringResource(R.string.em_unknown))
        )
        val addStoryViewModel = AddStoryViewModel(fakeAddStoryRepository)
        addStoryViewModel.onEvent(AddStoryEvent.UploadImage(description = Faker.getLorem()))
        assertThat(addStoryViewModel.isUploaded.getOrAwaitValue()).isFalse()
    }

    @Test
    fun `Failed uploading story, errorMessage is not null`() {
        val fakeAddStoryRepository = FakeAddStoryRepository(
            uploadStoryResource = Resource.Error(UIText.StringResource(R.string.em_unknown))
        )
        val addStoryViewModel = AddStoryViewModel(fakeAddStoryRepository)
        addStoryViewModel.onEvent(AddStoryEvent.UploadImage(description = Faker.getLorem()))
        assertThat(addStoryViewModel.errorMessage.getOrAwaitValue()).isNotNull()
    }

    @Test
    fun `onEvent ImageCaptured, photoFile is equal to captured image file`() {
        val expectedFile = Faker.getJpegFile()
        val fakeAddStoryRepository = FakeAddStoryRepository(
            newTempJpegUri = expectedFile.toUri(),
            foundJpegFile = expectedFile
        )
        val addStoryViewModel = AddStoryViewModel(fakeAddStoryRepository)
        addStoryViewModel.onEvent(AddStoryEvent.LaunchCamera)
        addStoryViewModel.onEvent(AddStoryEvent.ImageCaptured)

        assertThat(addStoryViewModel.photoFile.getOrAwaitValue()).isEqualTo(expectedFile)
    }

    @Test
    fun `onEvent ImageCaptured, jpegCamState is Closed`() {
        val expectedFile = Faker.getJpegFile()
        val fakeAddStoryRepository = FakeAddStoryRepository(
            newTempJpegUri = expectedFile.toUri(),
            foundJpegFile = expectedFile
        )
        val addStoryViewModel = AddStoryViewModel(fakeAddStoryRepository)
        addStoryViewModel.onEvent(AddStoryEvent.LaunchCamera)
        addStoryViewModel.onEvent(AddStoryEvent.ImageCaptured)

        assertThat(addStoryViewModel.jpegCamState.getOrAwaitValue()).isEqualTo(JpegCamState.Closed)
    }

    @Test
    fun `onEvent LaunchCamera, jpegCamState is Opened with correct Uri`() {
        val expectedUri = Faker.getUri()
        val fakeAddStoryRepository = FakeAddStoryRepository(
            newTempJpegUri = expectedUri
        )
        val addStoryViewModel = AddStoryViewModel(fakeAddStoryRepository)
        addStoryViewModel.onEvent(AddStoryEvent.LaunchCamera)

        assertThat(addStoryViewModel.jpegCamState.getOrAwaitValue()).isEqualTo(
            JpegCamState.Opened(tempJpegUri = expectedUri)
        )
    }

    @Test
    fun `onEvent ImageSelected, photoFile is the correct file`() {
        val expectedFile = Faker.getJpegFile()
        val fakeAddStoryRepository = FakeAddStoryRepository(foundJpegFile = expectedFile)
        val addStoryViewModel = AddStoryViewModel(fakeAddStoryRepository)

        addStoryViewModel.onEvent(AddStoryEvent.ImageSelected(expectedFile.toUri()))

        assertThat(addStoryViewModel.photoFile.getOrAwaitValue()).isEqualTo(expectedFile)
    }

    @Test
    fun `onEvent AddLocation, location is correct Location`() {
        val expectedLocation = Faker.getLocation()
        val fakeAddStoryRepository = FakeAddStoryRepository()
        val addStoryViewModel = AddStoryViewModel(fakeAddStoryRepository)

        addStoryViewModel.onEvent(AddStoryEvent.AddLocation(expectedLocation))

        assertThat(addStoryViewModel.location.getOrAwaitValue()).isEqualTo(expectedLocation)
    }

    @Test
    fun `onEvent UploadImage then image is still being uploaded, isUploading = true`() {
        val fakeAddStoryRepository = FakeAddStoryRepository(
            uploadStoryResource = Resource.Loading()
        )
        val addStoryViewModel = AddStoryViewModel(fakeAddStoryRepository)
        addStoryViewModel.onEvent(AddStoryEvent.UploadImage(description = Faker.getLorem()))
        assertThat(addStoryViewModel.isUploading.getOrAwaitValue()).isTrue()
    }
}