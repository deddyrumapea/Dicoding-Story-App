package com.romnan.dicodingstory.features.login.data.remote

import com.romnan.dicodingstory.features.login.data.model.LoginResponse

class FakeLoginApi(
    private val loginResponse: LoginResponse? = null,
    private val exception: Exception? = null
) : LoginApi {
    override suspend fun login(email: String, password: String): LoginResponse {
        return loginResponse ?: throw exception!!
    }
}