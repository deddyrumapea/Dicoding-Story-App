package com.romnan.dicodingstory.core.layers.data.repository

import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.romnan.dicodingstory.core.layers.domain.model.AppPreferences
import com.romnan.dicodingstory.util.Faker
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class PreferencesRepositoryImplTest {

    private lateinit var preferencesRepository: PreferencesRepositoryImpl

    @Before
    fun setUp() {
        preferencesRepository = PreferencesRepositoryImpl(
            appContext = ApplicationProvider.getApplicationContext()
        )
    }

    @Test
    fun getAppPreferencesInitially_appPreferencesDefaultValue() = runBlocking {
        preferencesRepository.getAppPreferences().test {
            assertThat(awaitItem()).isEqualTo(AppPreferences.defaultValue)
            cancelAndConsumeRemainingEvents()
        }

    }

    @Test
    fun saveLoginResult_correctAppPreferences() = runBlocking {
        val expectedLoginResult = Faker.getFilledLoginResult()
        preferencesRepository.saveLoginResult(loginResult = expectedLoginResult)
        preferencesRepository.getAppPreferences().test {
            assertThat(awaitItem()).isEqualTo(AppPreferences(loginResult = expectedLoginResult))
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun deleteLoginResult_appPreferencesIsResetToDefaultValue() = runBlocking {
        preferencesRepository.saveLoginResult(loginResult = Faker.getFilledLoginResult())
        preferencesRepository.deleteLoginResult()
        preferencesRepository.getAppPreferences().test {
            assertThat(awaitItem()).isEqualTo(AppPreferences.defaultValue)
            cancelAndConsumeRemainingEvents()
        }
    }
}