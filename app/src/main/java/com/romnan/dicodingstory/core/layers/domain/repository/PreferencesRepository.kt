package com.romnan.dicodingstory.core.layers.domain.repository

import com.romnan.dicodingstory.core.layers.domain.model.AppPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getAppPreferences(): Flow<AppPreferences>
    suspend fun setLoginToken(newToken: String)
}