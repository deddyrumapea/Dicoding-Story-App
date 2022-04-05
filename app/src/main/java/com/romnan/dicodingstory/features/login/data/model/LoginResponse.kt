package com.romnan.dicodingstory.features.login.data.model

import com.romnan.dicodingstory.core.layers.domain.model.LoginResult

data class LoginResponse(
    val error: Boolean? = null,
    val loginResult: LoginResult? = null,
    val message: String? = null
)