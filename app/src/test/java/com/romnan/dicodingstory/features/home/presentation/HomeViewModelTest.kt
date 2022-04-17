package com.romnan.dicodingstory.features.home.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.google.common.truth.Truth.assertThat
import com.romnan.dicodingstory.util.Faker
import com.romnan.dicodingstory.util.MainCoroutineRule
import com.romnan.dicodingstory.core.layers.data.repository.FakeCoreRepository
import com.romnan.dicodingstory.core.layers.data.repository.FakePreferencesRepository
import com.romnan.dicodingstory.core.layers.domain.model.AppPreferences
import com.romnan.dicodingstory.features.home.presentation.model.HomeEvent
import com.romnan.dicodingstory.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `getStoriesList, storiesList is not null`() = runTest {
        val fakePreferencesRepository = FakePreferencesRepository()
        val fakeCoreRepository = FakeCoreRepository(
            pagedStories = PagingData.from(Faker.getStoriesList())
        )
        val homeViewModel = HomeViewModel(fakeCoreRepository, fakePreferencesRepository)

        assertThat(homeViewModel.storiesList.getOrAwaitValue()).isNotNull()
    }

    @Test
    fun `User is logged in, isLoggedIn = true`() {
        val fakePreferencesRepository = FakePreferencesRepository(
            appPreferences = Faker.getAppPreferences(loggedIn = true)
        )
        val fakeCoreRepository = FakeCoreRepository()
        val homeViewModel = HomeViewModel(fakeCoreRepository, fakePreferencesRepository)

        assertThat(homeViewModel.isLoggedIn.getOrAwaitValue()).isTrue()
    }

    @Test
    fun `User is not logged in, isLoggedIn = false`() {
        val fakePreferencesRepository = FakePreferencesRepository(
            appPreferences = Faker.getAppPreferences(loggedIn = false)
        )
        val fakeCoreRepository = FakeCoreRepository()
        val homeViewModel = HomeViewModel(fakeCoreRepository, fakePreferencesRepository)

        assertThat(homeViewModel.isLoggedIn.getOrAwaitValue()).isFalse()
    }

    @Test
    fun `onEvent Logout, reset AppPreferences to default`() {
        val fakePreferencesRepository = FakePreferencesRepository(
            appPreferences = Faker.getAppPreferences(loggedIn = true)
        )
        val fakeCoreRepository = FakeCoreRepository()
        val homeViewModel = HomeViewModel(fakeCoreRepository, fakePreferencesRepository)

        homeViewModel.onEvent(HomeEvent.Logout)

        assertThat(fakePreferencesRepository.appPreferences).isEqualTo(AppPreferences.defaultValue)
    }
}