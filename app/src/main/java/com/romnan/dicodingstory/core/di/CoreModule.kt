package com.romnan.dicodingstory.core.di

import android.content.Context
import com.romnan.dicodingstory.core.layers.data.paging.StoriesPagingSource
import com.romnan.dicodingstory.core.layers.data.remote.CoreApi
import com.romnan.dicodingstory.core.layers.data.repository.CoreRepositoryImpl
import com.romnan.dicodingstory.core.layers.data.repository.PreferencesRepositoryImpl
import com.romnan.dicodingstory.core.layers.domain.repository.CoreRepository
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
        // TODO: remove logging interceptor
        val interceptor = HttpLoggingInterceptor()
            .apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideCoreRepository(
        api: CoreApi,
        prefRepo: PreferencesRepository,
        storiesPagingSource: StoriesPagingSource
    ): CoreRepository {
        return CoreRepositoryImpl(
            api = api,
            prefRepo = prefRepo,
            storiesPagingSource = storiesPagingSource
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
}