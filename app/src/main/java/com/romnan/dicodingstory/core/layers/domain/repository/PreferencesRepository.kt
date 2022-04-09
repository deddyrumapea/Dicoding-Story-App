package com.romnan.dicodingstory.core.layers.domain.repository

import com.romnan.dicodingstory.core.layers.domain.model.AppPreferences
import com.romnan.dicodingstory.core.layers.domain.model.LoginResult
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getAppPreferences(): Flow<AppPreferences>
    suspend fun saveLoginResult(loginResult: LoginResult)
    suspend fun deleteLoginResult()
}