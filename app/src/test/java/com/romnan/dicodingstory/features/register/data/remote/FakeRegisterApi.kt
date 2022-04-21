package com.romnan.dicodingstory.features.register.data.remote

import com.romnan.dicodingstory.features.register.domain.model.RegisterResponse

class FakeRegisterApi(
    private val registerResponse: RegisterResponse? = null,
    private val exception: Exception? = null
) : RegisterApi {
    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): RegisterResponse {
        return registerResponse ?: throw exception!!
    }
}