package com.romnan.dicodingstory.core.layers.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
) {
    companion object {
        val defaultValue = LoginResult(
            userId = "",
            name = "",
            token = ""
        )
    }
}