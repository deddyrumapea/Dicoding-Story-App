package com.romnan.dicodingstory.features.addStory.di

import android.content.Context
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import com.romnan.dicodingstory.features.addStory.data.remote.AddStoryApi
import com.romnan.dicodingstory.features.addStory.data.repository.AddStoryRepositoryImpl
import com.romnan.dicodingstory.features.addStory.domain.repository.AddStoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AddStoryModule {

    @Provides
    @Singleton
    fun provideAddStoryRepository(
        addStoryApi: AddStoryApi,
        prefRepo: PreferencesRepository,
        @ApplicationContext context: Context
    ): AddStoryRepository {
        return AddStoryRepositoryImpl(
            addStoryApi = addStoryApi,
            preferencesRepository = prefRepo,
            appContext = context
        )
    }

    @Provides
    @Singleton
    fun provideAddStoryApi(coreRetrofit: Retrofit): AddStoryApi {
        return coreRetrofit.create(AddStoryApi::class.java)
    }
}