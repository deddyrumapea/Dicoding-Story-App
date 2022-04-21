package com.romnan.dicodingstory.features.login.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.data.repository.FakePreferencesRepository
import com.romnan.dicodingstory.core.layers.domain.model.AppPreferences
import com.romnan.dicodingstory.core.layers.domain.model.LoginResult
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.login.data.model.LoginResponse
import com.romnan.dicodingstory.features.login.data.remote.FakeLoginApi
import com.romnan.dicodingstory.util.Faker
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.IOException

class LoginRepositoryImplTest {

    @Test
    fun `Attempt login, emits loading first`() = runBlocking {
        val fakeLoginApi = FakeLoginApi()
        val fakePreferencesRepository = FakePreferencesRepository()
        val loginRepository = LoginRepositoryImpl(fakeLoginApi, fakePreferencesRepository)

        loginRepository.login(
            email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<LoginResult>()::class.java)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `Successful login, emits login result as received from API`() = runBlocking {
        val expectedLoginResult = Faker.getFilledLoginResult()
        val fakeLoginApi = FakeLoginApi(
            loginResponse = LoginResponse(
                error = false,
                loginResult = expectedLoginResult,
                message = "success"
            )
        )
        val fakePreferencesRepository = FakePreferencesRepository()
        val loginRepository = LoginRepositoryImpl(fakeLoginApi, fakePreferencesRepository)

        loginRepository.login(
            email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<LoginResult>()::class.java)

            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Success(expectedLoginResult)::class.java)
                assertThat(it.data).isEqualTo(expectedLoginResult)
            }

            awaitComplete()
        }
    }

    @Test
    fun `Successful login, login result is saved in AppPreferences`() = runBlocking {
        val expectedLoginResult = Faker.getFilledLoginResult()
        val fakeLoginApi = FakeLoginApi(
            loginResponse = LoginResponse(
                error = false,
                loginResult = expectedLoginResult,
                message = "success"
            )
        )
        val fakePreferencesRepository = FakePreferencesRepository(
            appPreferences = AppPreferences.defaultValue
        )
        val loginRepository = LoginRepositoryImpl(fakeLoginApi, fakePreferencesRepository)

        loginRepository.login(
            email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            awaitItem() // Loading
            awaitItem() // Success
            awaitComplete()
            assertThat(fakePreferencesRepository.appPreferences).isEqualTo(
                AppPreferences(expectedLoginResult)
            )
        }
    }

    @Test
    fun `Unsuccessful login, emits error with message from API`() = runBlocking {
        val expectedErrorMessage = Faker.getLorem()
        val fakeLoginApi = FakeLoginApi(
            loginResponse = LoginResponse(
                error = true,
                message = expectedErrorMessage
            )
        )
        val fakePreferencesRepository = FakePreferencesRepository()
        val loginRepository = LoginRepositoryImpl(fakeLoginApi, fakePreferencesRepository)

        loginRepository.login(
            email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<LoginResult>()::class.java)

            awaitItem().let {
                val expectedUiText = UIText.DynamicString(expectedErrorMessage)
                assertThat(it).isInstanceOf(Resource.Error<LoginResult>(expectedUiText)::class.java)
                assertThat(it.uiText).isEqualTo(expectedUiText)
            }

            awaitComplete()
        }
    }

    @Test
    fun `Login with blank email, emits error with correct message`() = runBlocking {
        val fakeLoginApi = FakeLoginApi()
        val fakePreferencesRepository = FakePreferencesRepository()
        val loginRepository = LoginRepositoryImpl(fakeLoginApi, fakePreferencesRepository)

        loginRepository.login(
            email = "",
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<LoginResult>()::class.java)

            awaitItem().let {
                val expectedUiText = UIText.StringResource(R.string.em_email_blank)
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(expectedUiText)
            }

            awaitComplete()
        }
    }

    @Test
    fun `Login with blank password, emits error with correct message`() = runBlocking {
        val fakeLoginApi = FakeLoginApi()
        val fakePreferencesRepository = FakePreferencesRepository()
        val loginRepository = LoginRepositoryImpl(fakeLoginApi, fakePreferencesRepository)

        loginRepository.login(
            email = Faker.getEmail(),
            password = ""
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<LoginResult>()::class.java)

            awaitItem().let {
                val expectedUiText = UIText.StringResource(R.string.em_password_blank)
                assertThat(it).isInstanceOf(Resource.Error<LoginResult>(expectedUiText)::class.java)
                assertThat(it.uiText).isEqualTo(expectedUiText)
            }

            awaitComplete()
        }
    }

    @Test
    fun `Empty response from API, emits error with correct message`() = runBlocking {
        val fakeLoginApi = FakeLoginApi()
        val fakePreferencesRepository = FakePreferencesRepository()
        val loginRepository = LoginRepositoryImpl(fakeLoginApi, fakePreferencesRepository)

        loginRepository.login(
            email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<LoginResult>()::class.java)

            awaitItem().let {
                val expectedUiText = UIText.StringResource(R.string.em_unknown)
                assertThat(it).isInstanceOf(Resource.Error<LoginResult>(expectedUiText)::class.java)
                assertThat(it.uiText).isEqualTo(expectedUiText)
            }

            awaitComplete()
        }
    }

    @Test
    fun `IOException thrown from API, emits error with correct message`() = runBlocking {
        val fakeLoginApi = FakeLoginApi(
            loginResponse = null,
            exception = IOException()
        )
        val fakePreferencesRepository = FakePreferencesRepository()
        val loginRepository = LoginRepositoryImpl(fakeLoginApi, fakePreferencesRepository)

        loginRepository.login(
            email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<LoginResult>()::class.java)

            awaitItem().let {
                val expectedUiText = UIText.StringResource(R.string.em_io_exception)
                assertThat(it).isInstanceOf(Resource.Error<LoginResult>(expectedUiText)::class.java)
                assertThat(it.uiText).isEqualTo(expectedUiText)
            }

            awaitComplete()
        }
    }

    @Test
    fun `Exception thrown from API, emits error with correct message`() = runBlocking {
        val fakeLoginApi = FakeLoginApi(
            loginResponse = null,
            exception = Exception()
        )
        val fakePreferencesRepository = FakePreferencesRepository()
        val loginRepository = LoginRepositoryImpl(fakeLoginApi, fakePreferencesRepository)

        loginRepository.login(
            email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<LoginResult>()::class.java)

            awaitItem().let {
                val expectedUiText = UIText.StringResource(R.string.em_unknown)
                assertThat(it).isInstanceOf(Resource.Error<LoginResult>(expectedUiText)::class.java)
                assertThat(it.uiText).isEqualTo(expectedUiText)
            }

            awaitComplete()
        }
    }
}