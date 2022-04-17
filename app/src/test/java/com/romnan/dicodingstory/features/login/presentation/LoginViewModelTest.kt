package com.romnan.dicodingstory.features.login.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.romnan.dicodingstory.util.Faker
import com.romnan.dicodingstory.util.MainCoroutineRule
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.util.TestErrorMsg
import com.romnan.dicodingstory.core.layers.data.repository.FakePreferencesRepository
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.login.data.repository.FakeLoginRepository
import com.romnan.dicodingstory.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `Login is still on process, isLoading = true`() {
        val fakeLoginRepository = FakeLoginRepository(loginResource = Resource.Loading())
        val fakePreferencesRepository = FakePreferencesRepository(
            appPreferences = Faker.getAppPreferences(loggedIn = false)
        )
        val loginViewModel = LoginViewModel(fakeLoginRepository, fakePreferencesRepository)
        loginViewModel.onEvent(
            LoginEvent.SendLoginRequest(
                email = Faker.getEmail(),
                password = Faker.getPassword()
            )
        )
        assertThat(loginViewModel.isLoading.getOrAwaitValue()).isTrue()
    }

    @Test
    fun `User is logged in, isLoggedIn = true`() {
        val fakeLoginRepository = FakeLoginRepository()
        val fakePreferencesRepository = FakePreferencesRepository(
            appPreferences = Faker.getAppPreferences(loggedIn = true)
        )

        val loginViewModel = LoginViewModel(fakeLoginRepository, fakePreferencesRepository)

        assertThat(loginViewModel.isLoggedIn.getOrAwaitValue()).isTrue()
    }

    @Test
    fun `User is not logged in, isLoggedIn = false`() {
        val fakeLoginRepository = FakeLoginRepository()
        val fakePreferencesRepository = FakePreferencesRepository(
            appPreferences = Faker.getAppPreferences(loggedIn = false)
        )

        val loginViewModel = LoginViewModel(fakeLoginRepository, fakePreferencesRepository)

        assertThat(loginViewModel.isLoggedIn.getOrAwaitValue()).isFalse()
    }

    @Test
    fun `onEvent unsuccessful login, errorMessage is not null`() {
        val fakeLoginRepository = FakeLoginRepository(
            loginResource = Resource.Error(UIText.StringResource(R.string.em_unknown))
        )
        val fakePreferencesRepository = FakePreferencesRepository(
            appPreferences = Faker.getAppPreferences(loggedIn = false)
        )

        val loginViewModel = LoginViewModel(fakeLoginRepository, fakePreferencesRepository)

        // assert that errorMessage value is never set before sending login request
        try {
            assertThat(loginViewModel.errorMessage.getOrAwaitValue()).isNull()
        } catch (t: Throwable) {
            assertThat(t).isInstanceOf(TimeoutException::class.java)
            assertThat(t.message).isEqualTo(TestErrorMsg.LIVEDATA_VALUE_WAS_NEVER_SET)
        }

        loginViewModel.onEvent(
            LoginEvent.SendLoginRequest(
                email = Faker.getEmail(),
                password = Faker.getPassword()
            )
        )

        assertThat(loginViewModel.errorMessage.getOrAwaitValue()).isNotNull()
    }

    @Test
    fun `onEvent successful login, errorMessage is never set`() {
        val successLoginResult = Faker.getFilledLoginResult()
        val fakeLoginRepository = FakeLoginRepository(
            loginResource = Resource.Success(successLoginResult)
        )
        val fakePreferencesRepository = FakePreferencesRepository(
            appPreferences = Faker.getAppPreferences(loggedIn = false)
        )

        val loginViewModel = LoginViewModel(fakeLoginRepository, fakePreferencesRepository)

        // assert that errorMessage value is never set before sending login request
        try {
            assertThat(loginViewModel.errorMessage.getOrAwaitValue()).isNull()
        } catch (t: Throwable) {
            assertThat(t).isInstanceOf(TimeoutException::class.java)
            assertThat(t.message).isEqualTo(TestErrorMsg.LIVEDATA_VALUE_WAS_NEVER_SET)
        }

        loginViewModel.onEvent(
            LoginEvent.SendLoginRequest(
                email = Faker.getEmail(),
                password = Faker.getPassword()
            )
        )

        // assert that errorMessage value is never set after sending login request
        try {
            assertThat(loginViewModel.errorMessage.getOrAwaitValue()).isNull()
        } catch (t: Throwable) {
            assertThat(t).isInstanceOf(TimeoutException::class.java)
            assertThat(t.message).isEqualTo(TestErrorMsg.LIVEDATA_VALUE_WAS_NEVER_SET)
        }
    }

    @Test
    fun `onEvent successful login, isLoggedIn = true`() {
        val successLoginResult = Faker.getFilledLoginResult()
        val fakeLoginRepository = FakeLoginRepository(
            loginResource = Resource.Success(successLoginResult)
        )
        val fakePreferencesRepository = FakePreferencesRepository(
            appPreferences = Faker.getAppPreferences(loggedIn = false)
        )

        val loginViewModel = LoginViewModel(fakeLoginRepository, fakePreferencesRepository)

        assertThat(loginViewModel.isLoggedIn.getOrAwaitValue()).isFalse()

        loginViewModel.onEvent(
            LoginEvent.SendLoginRequest(
                email = Faker.getEmail(),
                password = Faker.getPassword()
            )
        )

        assertThat(loginViewModel.isLoggedIn.getOrAwaitValue()).isTrue()
    }

    @Test
    fun `onEvent unsuccessful login, isLoggedIn = false`() {
        val fakeLoginRepository = FakeLoginRepository(
            loginResource = Resource.Error(UIText.StringResource(R.string.em_unknown))
        )
        val fakePreferencesRepository = FakePreferencesRepository(
            appPreferences = Faker.getAppPreferences(loggedIn = false)
        )

        val loginViewModel = LoginViewModel(fakeLoginRepository, fakePreferencesRepository)

        assertThat(loginViewModel.isLoggedIn.getOrAwaitValue()).isFalse()

        loginViewModel.onEvent(
            LoginEvent.SendLoginRequest(
                email = Faker.getEmail(),
                password = Faker.getPassword()
            )
        )

        assertThat(loginViewModel.isLoggedIn.getOrAwaitValue()).isFalse()
    }
}