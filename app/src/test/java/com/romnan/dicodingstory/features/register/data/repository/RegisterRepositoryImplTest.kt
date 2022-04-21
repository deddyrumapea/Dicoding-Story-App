package com.romnan.dicodingstory.features.register.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.register.data.remote.FakeRegisterApi
import com.romnan.dicodingstory.features.register.domain.model.RegisterResponse
import com.romnan.dicodingstory.util.Faker
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.IOException

class RegisterRepositoryImplTest {
    @Test
    fun `Attempt register, emits loading first`() = runBlocking {
        val registerRepository = RegisterRepositoryImpl(FakeRegisterApi())

        registerRepository.register(
            name = Faker.getName(),
            email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<Unit>()::class.java)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `Successful register, emits Success`() = runBlocking {
        val fakeRegisterApi = FakeRegisterApi(
            registerResponse = RegisterResponse(error = false, message = "User Created")
        )
        val registerRepository = RegisterRepositoryImpl(fakeRegisterApi)

        registerRepository.register(
            name = Faker.getName(),
            email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<Unit>()::class.java)
            assertThat(awaitItem()).isInstanceOf(Resource.Success(Unit)::class.java)
            awaitComplete()
        }
    }

    @Test
    fun `Unsuccessful register, emits error with message from API`() = runBlocking {
        val expectedErrorMessage = Faker.getLorem()
        val fakeRegisterApi = FakeRegisterApi(
            registerResponse = RegisterResponse(error = true, message = expectedErrorMessage)
        )
        val registerRepository = RegisterRepositoryImpl(fakeRegisterApi)

        registerRepository.register(
            name = Faker.getName(),
            email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<Unit>()::class.java)

            awaitItem().let {
                val expectedUiText = UIText.DynamicString(expectedErrorMessage)
                assertThat(it).isInstanceOf(Resource.Error<Unit>(expectedUiText)::class.java)
                assertThat(it.uiText).isEqualTo(expectedUiText)
            }

            awaitComplete()
        }
    }

    @Test
    fun `Register with blank name, emits error with correct message`() = runBlocking {
        val fakeRegisterApi = FakeRegisterApi()
        val registerRepository = RegisterRepositoryImpl(fakeRegisterApi)

        registerRepository.register(
            name = "",
            email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<Unit>()::class.java)

            awaitItem().let {
                val expectedUiText = UIText.StringResource(R.string.em_name_blank)
                assertThat(it).isInstanceOf(Resource.Error<Unit>(expectedUiText)::class.java)
                assertThat(it.uiText).isEqualTo(expectedUiText)
            }

            awaitComplete()
        }
    }

    @Test
    fun `Register with blank email, emits error with correct message`() = runBlocking {
        val fakeRegisterApi = FakeRegisterApi()
        val registerRepository = RegisterRepositoryImpl(fakeRegisterApi)

        registerRepository.register(
            name = Faker.getName(),
            email = "",
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<Unit>()::class.java)

            awaitItem().let {
                val expectedUiText = UIText.StringResource(R.string.em_email_blank)
                assertThat(it).isInstanceOf(Resource.Error<Unit>(expectedUiText)::class.java)
                assertThat(it.uiText).isEqualTo(expectedUiText)
            }

            awaitComplete()
        }
    }

    @Test
    fun `Register with blank password, emits error with correct message`() = runBlocking {
        val fakeRegisterApi = FakeRegisterApi()
        val registerRepository = RegisterRepositoryImpl(fakeRegisterApi)

        registerRepository.register(
            name = Faker.getName(),
            email = Faker.getEmail(),
            password = ""
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<Unit>()::class.java)

            awaitItem().let {
                val expectedUiText = UIText.StringResource(R.string.em_password_blank)
                assertThat(it).isInstanceOf(Resource.Error<Unit>(expectedUiText)::class.java)
                assertThat(it.uiText).isEqualTo(expectedUiText)
            }

            awaitComplete()
        }
    }

    @Test
    fun `Empty response from API, emits error with correct message`() = runBlocking {
        val fakeRegisterApi = FakeRegisterApi()
        val registerRepository = RegisterRepositoryImpl(fakeRegisterApi)

        registerRepository.register(
            name = Faker.getName(),
            email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<Unit>()::class.java)

            awaitItem().let {
                val expectedUiText = UIText.StringResource(R.string.em_unknown)
                assertThat(it).isInstanceOf(Resource.Error<Unit>(expectedUiText)::class.java)
                assertThat(it.uiText).isEqualTo(expectedUiText)
            }

            awaitComplete()
        }
    }

    @Test
    fun `IOException thrown from API, emits error with correct message`() = runBlocking {
        val fakeRegisterApi = FakeRegisterApi(exception = IOException())
        val registerRepository = RegisterRepositoryImpl(fakeRegisterApi)

        registerRepository.register(
            name = Faker.getName(),
            email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<Unit>()::class.java)

            awaitItem().let {
                val expectedUiText = UIText.StringResource(R.string.em_io_exception)
                assertThat(it).isInstanceOf(Resource.Error<Unit>(expectedUiText)::class.java)
                assertThat(it.uiText).isEqualTo(expectedUiText)
            }

            awaitComplete()
        }
    }

    @Test
    fun `Exception thrown from API, emits error with correct message`() = runBlocking {
        val fakeRegisterApi = FakeRegisterApi(exception = Exception())
        val registerRepository = RegisterRepositoryImpl(fakeRegisterApi)

        registerRepository.register(
            name = Faker.getName(), email = Faker.getEmail(),
            password = Faker.getPassword()
        ).test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading<Unit>()::class.java)

            awaitItem().let {
                val expectedUiText = UIText.StringResource(R.string.em_unknown)
                assertThat(it).isInstanceOf(Resource.Error<Unit>(expectedUiText)::class.java)
                assertThat(it.uiText).isEqualTo(expectedUiText)
            }

            awaitComplete()
        }
    }
}