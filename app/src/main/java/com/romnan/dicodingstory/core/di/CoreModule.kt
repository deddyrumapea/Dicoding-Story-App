package com.romnan.dicodingstory.core.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.romnan.dicodingstory.core.layers.data.paging.StoriesPagingSource
import com.romnan.dicodingstory.core.layers.data.paging.StoriesRemoteMediator
import com.romnan.dicodingstory.core.layers.data.repository.CoreRepositoryImpl
import com.romnan.dicodingstory.core.layers.data.repository.PreferencesRepositoryImpl
import com.romnan.dicodingstory.core.layers.data.retrofit.CoreApi
import com.romnan.dicodingstory.core.layers.data.room.CoreDatabase
import com.romnan.dicodingstory.core.layers.domain.repository.CoreRepository
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Provides
    @Singleton
    fun provideCoreRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCoreRepository(
        api: CoreApi,
        coreDatabase: CoreDatabase,
        prefRepo: PreferencesRepository,
        storiesRemoteMediator: StoriesRemoteMediator
    ): CoreRepository {
        return CoreRepositoryImpl(
            dao = coreDatabase.storyDao,
            api = api,
            prefRepo = prefRepo,
            storiesRemoteMediator = storiesRemoteMediator
        )
    }

    @Provides
    @Singleton
    fun provideStoriesRemoteMediator(
        coreDatabase: CoreDatabase,
        coreApi: CoreApi,
        prefRepo: PreferencesRepository
    ): StoriesRemoteMediator {
        return StoriesRemoteMediator(
            database = coreDatabase,
            api = coreApi,
            prefRepo = prefRepo
        )
    }

    @Provides
    @Singleton
    fun provideStoriesPagingSource(
        coreApi: CoreApi,
        prefRepo: PreferencesRepository
    ): StoriesPagingSource {
        return StoriesPagingSource(api = coreApi, prefRepo = prefRepo)
    }

    @Provides
    @Singleton
    fun provideHomeApi(coreRetrofit: Retrofit): CoreApi {
        return coreRetrofit.create(CoreApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): CoreDatabase {
        return Room
            .databaseBuilder(
                app,
                CoreDatabase::class.java,
                CoreDatabase.NAME
            )
            .fallbackToDestructiveMigration()
            .build()
    }
}