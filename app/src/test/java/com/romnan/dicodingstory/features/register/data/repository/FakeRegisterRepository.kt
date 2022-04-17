package com.romnan.dicodingstory.features.register.data.repository

import com.romnan.dicodingstory.core.util.SimpleResource
import com.romnan.dicodingstory.features.register.domain.repository.RegisterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRegisterRepository(
    private val registerResource: SimpleResource? = null
) : RegisterRepository {
    override fun register(
        name: String, email: String, password: String
    ): Flow<SimpleResource> = flow {
        registerResource?.let { emit(it) }
    }
}