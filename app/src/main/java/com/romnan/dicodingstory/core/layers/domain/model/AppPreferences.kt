package com.romnan.dicodingstory.core.layers.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AppPreferences(
    val loginResult: LoginResult
) {
    companion object {
        val defaultValue = AppPreferences(
            loginResult = LoginResult.defaultValue
        )
    }
}