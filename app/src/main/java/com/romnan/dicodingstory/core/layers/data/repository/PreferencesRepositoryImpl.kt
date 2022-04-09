package com.romnan.dicodingstory.core.layers.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.romnan.dicodingstory.core.layers.data.util.AppPreferencesSerializer
import com.romnan.dicodingstory.core.layers.domain.model.AppPreferences
import com.romnan.dicodingstory.core.layers.domain.model.LoginResult
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class PreferencesRepositoryImpl(
    appContext: Context
) : PreferencesRepository {

    private val Context.dataStore by dataStore(
        fileName = APP_PREF_FILE_NAME,
        AppPreferencesSerializer
    )
    private val dataStore: DataStore<AppPreferences> = appContext.dataStore

    override fun getAppPreferences(): Flow<AppPreferences> {
        return dataStore.data
    }

    override suspend fun saveLoginResult(loginResult: LoginResult) {
        dataStore.updateData {
            it.copy(loginResult = loginResult)
        }
    }

    override suspend fun deleteLoginResult() {
        dataStore.updateData {
            it.copy(loginResult = LoginResult.defaultValue)
        }
    }

    companion object {
        private const val APP_PREF_FILE_NAME = "app-preferences.json"
    }
}