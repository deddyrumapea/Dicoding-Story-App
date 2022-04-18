package com.romnan.dicodingstory.features.login.di

import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import com.romnan.dicodingstory.features.login.data.remote.LoginApi
import com.romnan.dicodingstory.features.login.data.repository.LoginRepositoryImpl
import com.romnan.dicodingstory.features.login.domain.repository.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LoginModule {

    @Provides
    @Singleton
    fun provideLoginRepository(
        loginApi: LoginApi,
        preferencesRepository: PreferencesRepository
    ): LoginRepository {
        return LoginRepositoryImpl(
            loginApi = loginApi,
            preferencesRepository = preferencesRepository
        )
    }

    @Provides
    @Singleton
    fun provideLoginApi(coreRetrofit: Retrofit): LoginApi {
        return coreRetrofit.create(LoginApi::class.java)
    }
}