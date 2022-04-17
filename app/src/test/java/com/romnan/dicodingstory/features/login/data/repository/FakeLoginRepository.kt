package com.romnan.dicodingstory.features.login.data.repository

import com.romnan.dicodingstory.core.layers.domain.model.LoginResult
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.features.login.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLoginRepository(
    private val loginResource: Resource<LoginResult>? = null
) : LoginRepository {
    override fun login(
        email: String,
        password: String
    ): Flow<Resource<LoginResult>> = flow {
        loginResource?.let { emit(it) }
    }
}