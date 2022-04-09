package com.romnan.dicodingstory.features.register.domain.repository

import com.romnan.dicodingstory.core.util.SimpleResource
import kotlinx.coroutines.flow.Flow

interface RegisterRepository {
    fun register(name: String, email: String, password: String): Flow<SimpleResource>
}