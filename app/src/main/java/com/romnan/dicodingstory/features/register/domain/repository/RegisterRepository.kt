package com.romnan.dicodingstory.features.register.domain.repository

import com.bumptech.glide.load.engine.Resource
import com.romnan.dicodingstory.core.util.SimpleResource
import com.romnan.dicodingstory.features.register.domain.model.RegisterResult
import kotlinx.coroutines.flow.Flow

interface RegisterRepository {
    fun register(name: String, email: String, password: String): Flow<SimpleResource>
}