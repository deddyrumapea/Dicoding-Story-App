package com.romnan.dicodingstory.features.login.data.repository

import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.domain.model.LoginResult
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.login.data.remote.LoginApi
import com.romnan.dicodingstory.features.login.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class LoginRepositoryImpl(
    private val loginApi: LoginApi,
    private val prefRepo: PreferencesRepository
) : LoginRepository {

    override fun login(
        email: String,
        password: String
    ): Flow<Resource<LoginResult>> = flow {

        emit(Resource.Loading())

        try {
            val response = loginApi.login(email = email, password = password)
            when {
                response.error != true && response.loginResult != null -> {
                    prefRepo.saveLoginResult(response.loginResult)
                    emit(Resource.Success(response.loginResult))
                }

                response.error == true && response.message != null -> {
                    emit(Resource.Error(UIText.DynamicString(response.message)))
                }

                else -> emit(Resource.Error(UIText.StringResource(R.string.em_unknown)))
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    // TODO: extract this error code
                    if (e.code() == 401) {
                        emit(Resource.Error(UIText.StringResource(R.string.em_401)))
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