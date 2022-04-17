package com.romnan.dicodingstory.features.register.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.romnan.dicodingstory.util.Faker
import com.romnan.dicodingstory.util.MainCoroutineRule
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.util.TestErrorMsg
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.register.data.repository.FakeRegisterRepository
import com.romnan.dicodingstory.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `Registration is still loading, isLoading = true`() {
        val fakeRegisterRepository = FakeRegisterRepository(
            registerResource = Resource.Loading()
        )
        val registerViewModel = RegisterViewModel(fakeRegisterRepository)

        registerViewModel.onEvent(
            RegisterEvent.SendRegisterRequest(
                name = Faker.getName(),
                email = Faker.getEmail(),
                password = Faker.getPassword()
            )
        )

        assertThat(registerViewModel.isLoading.getOrAwaitValue()).isTrue()
    }

    @Test
    fun `Successful registration, isRegistered = true`() {
        val fakeRegisterRepository = FakeRegisterRepository(
            registerResource = Resource.Success(Unit)
        )
        val registerViewModel = RegisterViewModel(fakeRegisterRepository)

        // assert that isRegistered value is never set before sending register request
        try {
            assertThat(registerViewModel.isRegistered.getOrAwaitValue()).isNull()
        } catch (t: Throwable) {
            assertThat(t).isInstanceOf(TimeoutException::class.java)
            assertThat(t.message).isEqualTo(TestErrorMsg.LIVEDATA_VALUE_WAS_NEVER_SET)
        }

        registerViewModel.onEvent(
            RegisterEvent.SendRegisterRequest(
                "John Doe",
                "johndoe@gmail.com",
                "johnn0hj"
            )
        )

        assertThat(registerViewModel.isRegistered.getOrAwaitValue()).isTrue()
    }

    @Test
    fun `Unsuccessful registration, isRegistered = false`() {
        val fakeRegisterRepository = FakeRegisterRepository(
            registerResource = Resource.Error(UIText.StringResource(R.string.em_unknown))
        )
        val registerViewModel = RegisterViewModel(fakeRegisterRepository)

        // assert that isRegistered value is never set before sending register request
        try {
            assertThat(registerViewModel.isRegistered.getOrAwaitValue()).isNull()
        } catch (t: Throwable) {
            assertThat(t).isInstanceOf(TimeoutException::class.java)
            assertThat(t.message).isEqualTo(TestErrorMsg.LIVEDATA_VALUE_WAS_NEVER_SET)
        }

        registerViewModel.onEvent(
            RegisterEvent.SendRegisterRequest(
                "John Doe",
                "johndoe@gmail.com",
                "johnn0hj"
            )
        )

        assertThat(registerViewModel.isRegistered.getOrAwaitValue()).isFalse()
    }

    @Test
    fun `Successful registration, errorMessage was never set`() {
        val fakeRegisterRepository = FakeRegisterRepository(
            registerResource = Resource.Success(Unit)
        )
        val registerViewModel = RegisterViewModel(fakeRegisterRepository)

        registerViewModel.onEvent(
            RegisterEvent.SendRegisterRequest(
                "John Doe",
                "johndoe@gmail.com",
                "johnn0hj"
            )
        )

        try {
            assertThat(registerViewModel.errorMessage.getOrAwaitValue()).isNull()
        } catch (t: Throwable) {
            assertThat(t).isInstanceOf(TimeoutException::class.java)
            assertThat(t.message).isEqualTo(TestErrorMsg.LIVEDATA_VALUE_WAS_NEVER_SET)
        }
    }

    @Test
    fun `Unsuccessful registration, errorMessage is not null`() {
        val fakeRegisterRepository = FakeRegisterRepository(
            registerResource = Resource.Error(UIText.StringResource(R.string.em_unknown))
        )
        val registerViewModel = RegisterViewModel(fakeRegisterRepository)

        // assert that errorMessage value is never set before sending register request
        try {
            assertThat(registerViewModel.errorMessage.getOrAwaitValue()).isNull()
        } catch (t: Throwable) {
            assertThat(t).isInstanceOf(TimeoutException::class.java)
            assertThat(t.message).isEqualTo(TestErrorMsg.LIVEDATA_VALUE_WAS_NEVER_SET)
        }

        registerViewModel.onEvent(
            RegisterEvent.SendRegisterRequest(
                "John Doe",
                "johndoe@gmail.com",
                "johnn0hj"
            )
        )

        assertThat(registerViewModel.errorMessage.getOrAwaitValue()).isNotNull()
    }
}