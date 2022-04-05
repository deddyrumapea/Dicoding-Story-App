package com.romnan.dicodingstory.core.di

import android.content.Context
import com.romnan.dicodingstory.core.layers.data.repository.PreferencesRepositoryImpl
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {
    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context
    ): PreferencesRepository {
        return PreferencesRepositoryImpl(context)
    }
}