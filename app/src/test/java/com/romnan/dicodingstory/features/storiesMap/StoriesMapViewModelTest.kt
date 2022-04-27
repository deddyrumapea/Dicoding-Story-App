package com.romnan.dicodingstory.features.storiesMap

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.data.repository.FakeCoreRepository
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.util.Faker
import com.romnan.dicodingstory.util.MainCoroutineRule
import com.romnan.dicodingstory.util.TestErrorMsg
import com.romnan.dicodingstory.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class StoriesMapViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `storiesList is still being loaded, isLoading = true`() {
        val fakeCoreRepository = FakeCoreRepository(
            storiesWithLatLong = Resource.Loading()
        )

        val storiesMapViewModel = StoriesMapViewModel(fakeCoreRepository)

        assertThat(storiesMapViewModel.isLoading.getOrAwaitValue()).isTrue()
    }

    @Test
    fun `Successfully getStoriesList, correct stories list`() {
        val expectedStoriesList = Faker.getStoriesList()
        val fakeCoreRepository = FakeCoreRepository(
            storiesWithLatLong = Resource.Success(expectedStoriesList)
        )

        val storiesMapViewModel = StoriesMapViewModel(fakeCoreRepository)

        assertThat(storiesMapViewModel.storiesList.getOrAwaitValue()).isEqualTo(expectedStoriesList)
    }

    @Test
    fun `Successfully getStoriesList, errorMessage was never set`() {
        val fakeCoreRepository = FakeCoreRepository(
            storiesWithLatLong = Resource.Success(Faker.getStoriesList())
        )

        val storiesMapViewModel = StoriesMapViewModel(fakeCoreRepository)

        val exception = assertThrows(TimeoutException::class.java) {
            storiesMapViewModel.errorMessage.getOrAwaitValue()
        }
        assertThat(exception.message).isEqualTo(TestErrorMsg.LIVEDATA_VALUE_WAS_NEVER_SET)
    }

    @Test
    fun `Unsuccessfully getStoriesList, storiesList was never set`() {
        val fakeCoreRepository = FakeCoreRepository(
            storiesWithLatLong = Resource.Error(UIText.StringResource(R.string.em_unknown))
        )

        val storiesMapViewModel = StoriesMapViewModel(fakeCoreRepository)

        val exception = assertThrows(TimeoutException::class.java) {
            storiesMapViewModel.storiesList.getOrAwaitValue()
        }
        assertThat(exception.message).isEqualTo(TestErrorMsg.LIVEDATA_VALUE_WAS_NEVER_SET)
    }

    @Test
    fun `Unsuccessfully getStoriesList, errorMessage is not null`() {
        val fakeCoreRepository = FakeCoreRepository(
            storiesWithLatLong = Resource.Error(UIText.StringResource(R.string.em_unknown))
        )

        val storiesMapViewModel = StoriesMapViewModel(fakeCoreRepository)

        assertThat(storiesMapViewModel.errorMessage.getOrAwaitValue()).isNotNull()
    }
}