package com.romnan.dicodingstory.core.layers.data.repository

import com.romnan.dicodingstory.core.layers.domain.model.AppPreferences
import com.romnan.dicodingstory.core.layers.domain.model.LoginResult
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePreferencesRepository(
    var appPreferences: AppPreferences? = null
) : PreferencesRepository {
    override fun getAppPreferences(): Flow<AppPreferences> = flow {
        appPreferences?.let { emit(it) }
    }

    override suspend fun saveLoginResult(loginResult: LoginResult) {
        appPreferences = appPreferences?.copy(
            loginResult = loginResult
        )
    }

    override suspend fun deleteLoginResult() {
        appPreferences = appPreferences?.copy(
            loginResult = LoginResult.defaultValue
        )
    }
}