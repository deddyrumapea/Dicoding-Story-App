package com.romnan.dicodingstory.features.register.data.repository

import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.SimpleResource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.register.data.remote.RegisterApi
import com.romnan.dicodingstory.features.register.domain.repository.RegisterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class RegisterRepositoryImpl(
    private val registerApi: RegisterApi
) : RegisterRepository {

    override fun register(
        name: String,
        email: String,
        password: String
    ): Flow<SimpleResource> = flow {

        emit(Resource.Loading())

        try {
            val response = registerApi.register(
                name = name,
                email = email,
                password = password
            )

            when {
                response.error != true -> emit(Resource.Success(Unit))
                response.message != null -> emit(
                    Resource.Error(UIText.DynamicString(response.message))
                )
                else -> emit(Resource.Error(UIText.StringResource(R.string.em_unknown)))
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    // TODO: extract this error code
                    if (e.code() == 400) {
                        emit(Resource.Error(UIText.StringResource(R.string.em_400)))
                    } else {
                        emit(Resource.Error(UIText.StringResource(R.string.em_http_exception)))
                    }
                }

                is IOException -> emit(
                    Resource.Error(UIText.StringResource(R.string.em_io_exception))
                )

                else -> emit(Resource.Error(UIText.StringResource(R.string.em_unknown)))
            }
        }
    }
}