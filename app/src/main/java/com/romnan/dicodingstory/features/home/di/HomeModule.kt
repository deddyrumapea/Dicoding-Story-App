package com.romnan.dicodingstory.features.home.di

import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import com.romnan.dicodingstory.features.home.data.remote.HomeApi
import com.romnan.dicodingstory.features.home.data.repository.HomeRepositoryImpl
import com.romnan.dicodingstory.features.home.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HomeModule {
    @Provides
    @Singleton
    fun provideHomeRepository(
        api: HomeApi,
        prefRepo: PreferencesRepository
    ): HomeRepository {
        return HomeRepositoryImpl(
            api = api,
            prefRepo = prefRepo
        )
    }

    @Provides
    @Singleton
    fun provideHomeApi(coreRetrofit: Retrofit): HomeApi {
        return coreRetrofit.create(HomeApi::class.java)
    }
}