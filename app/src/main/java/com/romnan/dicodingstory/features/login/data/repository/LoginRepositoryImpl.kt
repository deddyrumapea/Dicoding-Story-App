package com.romnan.dicodingstory.features.login.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.domain.model.LoginResult
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.login.data.model.LoginResponse
import com.romnan.dicodingstory.features.login.data.remote.LoginApi
import com.romnan.dicodingstory.features.login.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class LoginRepositoryImpl(
    private val loginApi: LoginApi,
    private val preferencesRepository: PreferencesRepository
) : LoginRepository {

    override fun login(
        email: String,
        password: String
    ): Flow<Resource<LoginResult>> = flow {

        emit(Resource.Loading())

        when {
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
            val response = loginApi.login(email = email, password = password)

            when {
                response.error != true && response.loginResult != null -> {
                    preferencesRepository.saveLoginResult(response.loginResult)
                    emit(Resource.Success(response.loginResult))
                }

                response.error == true && response.message != null -> {
                    emit(Resource.Error(UIText.DynamicString(response.message)))
                }

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