package com.romnan.dicodingstory.features.addStory.data.repository

import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.data.repository.FakePreferencesRepository
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.addStory.data.model.AddStoryResponse
import com.romnan.dicodingstory.features.addStory.data.remote.AddStoryApi
import com.romnan.dicodingstory.features.addStory.data.remote.FakeAddStoryApi
import com.romnan.dicodingstory.util.Faker
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.security.DigestInputStream
import java.security.MessageDigest

@SmallTest
class AddStoryRepositoryImplTest {

    private fun createAddStoryRepositoryImpl(
        addStoryApi: AddStoryApi = FakeAddStoryApi()
    ): AddStoryRepositoryImpl = AddStoryRepositoryImpl(
        addStoryApi = addStoryApi,
        appContext = ApplicationProvider.getApplicationContext(),
        preferencesRepository = FakePreferencesRepository(
            appPreferences = Faker.getAppPreferences(loggedIn = true)
        )
    )

    @Test
    fun uploadStory_emitsLoadingFirst() = runBlocking {
        val fakeAddStoryApi = FakeAddStoryApi()
        val addStoryRepository = createAddStoryRepositoryImpl(fakeAddStoryApi)
        val newStory = Faker.getNewStory()

        addStoryRepository.uploadStory(newStory).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun successUploadStory_emitsSuccess() = runBlocking {
        val fakeAddStoryApi = FakeAddStoryApi(
            addStoryResponse = AddStoryResponse(
                error = false,
                message = "success"
            )
        )

        val addStoryRepository = createAddStoryRepositoryImpl(fakeAddStoryApi)

        val newStory = Faker.getNewStory()

        addStoryRepository.uploadStory(newStory).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            assertThat(awaitItem()).isInstanceOf(Resource.Success::class.java)
            awaitComplete()
        }
    }

    @Test
    fun errorUploadStoryWithMessageFromApi_emitsErrorWithCorrectMessage() = runBlocking {
        val expectedMessage = Faker.getLorem()
        val fakeAddStoryApi = FakeAddStoryApi(
            addStoryResponse = AddStoryResponse(
                error = true,
                message = expectedMessage
            )
        )
        val addStoryRepository = createAddStoryRepositoryImpl(fakeAddStoryApi)
        val newStory = Faker.getNewStory()

        addStoryRepository.uploadStory(newStory).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.DynamicString(expectedMessage))
            }
            awaitComplete()
        }
    }

    @Test
    fun errorUploadStoryWithoutMessageFromApi_emitsErrorWithCorrectMessage() = runBlocking {
        val fakeAddStoryApi = FakeAddStoryApi(
            addStoryResponse = AddStoryResponse(
                error = true,
                message = null
            )
        )
        val addStoryRepository = createAddStoryRepositoryImpl(fakeAddStoryApi)
        val newStory = Faker.getNewStory()

        addStoryRepository.uploadStory(newStory).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.StringResource(R.string.em_unknown))
            }
            awaitComplete()
        }
    }

    @Test
    fun uploadStoryWithNullPhotoFile_emitsErrorWithCorrectMessage() = runBlocking {
        val fakeAddStoryApi = FakeAddStoryApi()
        val addStoryRepository = createAddStoryRepositoryImpl(fakeAddStoryApi)
        val newStory = Faker.getNewStory().copy(photo = null)

        addStoryRepository.uploadStory(newStory).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.StringResource(R.string.em_photo_null))
            }
            awaitComplete()
        }
    }

    @Test
    fun uploadStoryWithBlankDescription_emitsErrorWithCorrectMessage() = runBlocking {
        val fakeAddStoryApi = FakeAddStoryApi()
        val addStoryRepository = createAddStoryRepositoryImpl(fakeAddStoryApi)
        val newStory = Faker.getNewStory().copy(description = "")

        addStoryRepository.uploadStory(newStory).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.StringResource(R.string.em_description_blank))
            }
            awaitComplete()
        }
    }

    @Test
    fun ioExceptionFromApi_emitsErrorWithCorrectMessage() = runBlocking {
        val fakeAddStoryApi = FakeAddStoryApi(exception = IOException())
        val addStoryRepository = createAddStoryRepositoryImpl(fakeAddStoryApi)
        val newStory = Faker.getNewStory()

        addStoryRepository.uploadStory(newStory).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.StringResource(R.string.em_io_exception))
            }
            awaitComplete()
        }
    }

    @Test
    fun exceptionFromApi_emitsErrorWithCorrectMessage() = runBlocking {
        val fakeAddStoryApi = FakeAddStoryApi(exception = Exception())
        val addStoryRepository = createAddStoryRepositoryImpl(fakeAddStoryApi)
        val newStory = Faker.getNewStory(withLocation = false)

        addStoryRepository.uploadStory(newStory).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.StringResource(R.string.em_unknown))
            }
            awaitComplete()
        }
    }

    @Test
    fun getNewTempJpegUri_FreshUriEachTime() = runBlocking {
        val addStoryRepository = createAddStoryRepositoryImpl()
        val uri1 = addStoryRepository.getNewTempJpegUri()
        val uri2 = addStoryRepository.getNewTempJpegUri()
        assertThat(uri1).isNotEqualTo(uri2)
    }

    @Test
    fun getNewTempJpegUri_JpegFileUri() = runBlocking {
        val addStoryRepository = createAddStoryRepositoryImpl()
        val uri = addStoryRepository.getNewTempJpegUri()
        val file = uri.path?.let { File(it) }

        assertThat(file).isNotNull()
        assertThat(file?.extension).isEqualTo("jpeg")
    }

    @Test
    fun findJpegByUriWithValidUri_JpegFile() = runBlocking {
        val addStoryRepository = createAddStoryRepositoryImpl()
        val uri = addStoryRepository.getNewTempJpegUri()

        val jpegFile = addStoryRepository.findJpegByUri(uri)

        assertThat(jpegFile).isNotNull()
        assertThat(jpegFile?.isFile).isTrue()
        assertThat(jpegFile?.extension).isEqualTo("jpeg")
    }

    private fun getMd5Checksum(file: File): ByteArray {
        val messageDigest = MessageDigest.getInstance("MD5")
        Files.newInputStream(file.toPath()).use { `is` ->
            DigestInputStream(`is`, messageDigest).use {}
        }
        return messageDigest.digest()
    }

    @Test
    fun findJpegByUriWithInvalidUri_Null() = runBlocking {
        val addStoryRepository = createAddStoryRepositoryImpl()

        val jpegFile = addStoryRepository.findJpegByUri(Faker.getUri())

        assertThat(jpegFile).isNull()
    }
}