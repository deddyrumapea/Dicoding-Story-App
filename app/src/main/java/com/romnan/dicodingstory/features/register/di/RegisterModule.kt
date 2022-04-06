package com.romnan.dicodingstory.features.register.di

import com.romnan.dicodingstory.features.register.data.remote.RegisterApi
import com.romnan.dicodingstory.features.register.data.repository.RegisterRepositoryImpl
import com.romnan.dicodingstory.features.register.domain.repository.RegisterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RegisterModule {

    @Provides
    @Singleton
    fun provideRegisterRepository(api: RegisterApi): RegisterRepository {
        return RegisterRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideRegisterApi(coreRetrofit: Retrofit): RegisterApi {
        return coreRetrofit.create(RegisterApi::class.java)
    }
}