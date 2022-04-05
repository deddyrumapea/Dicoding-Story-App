package com.romnan.dicodingstory.core.layers.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AppPreferences(
    val loginToken: String
) {
    companion object {
        val defaultValue = AppPreferences(loginToken = "")
    }
}