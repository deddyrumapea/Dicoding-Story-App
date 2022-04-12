package com.romnan.dicodingstory.features.register.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.SimpleResource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.login.data.model.LoginResponse
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

        when {
            name.isBlank() -> {
                emit(Resource.Error(UIText.StringResource(R.string.em_name_blank)))
                return@flow
            }
            email.isBlank() -> {
                emit(Resource.Error(UIText.StringResource(R.string.em_email_blank)))
                return@flow
            }
            password.isBlank() -> {
                emit(Resource.Error(UIText.StringResource(R.string.em_password_blank)))
                return@flow
            }
        }

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
        } catch (t: Throwable) {
            val errorUiText: UIText = when (t) {
                is HttpException -> {
                    try {
                        val response = Gson().fromJson<LoginResponse>(
                            t.response()?.errorBody()?.charStream(),
                            object : TypeToken<LoginResponse>() {}.type
                        )
                        UIText.DynamicString(response.message!!)
                    } catch (e: Exception) {
                        UIText.StringResource(R.string.em_unknown)
                    }
                }
                is IOException -> UIText.StringResource(R.string.em_io_exception)
                else -> UIText.StringResource(R.string.em_unknown)
            }
            emit(Resource.Error(errorUiText))
        }
    }
}