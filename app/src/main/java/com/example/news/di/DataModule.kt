package com.example.news.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.news.data.local.Database
import com.example.news.data.local.NewsDao
import com.example.news.data.remote.NewsApiService
import com.example.news.data.repository.NewsRepositoryImpl
import com.example.news.data.repository.SettingRepositoryImpl
import com.example.news.domain.repository.Repository
import com.example.news.domain.repository.SettingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Singleton
    @Binds
    fun provideRepImpl(repositoryImpl: NewsRepositoryImpl) : Repository

    @Singleton
    @Binds
    fun provideSettingsRepositoryImpl(settingsRepository: SettingRepositoryImpl) : SettingRepository


    companion object{
        @Singleton
        @Provides
        fun provideDao(database: Database) : NewsDao{
            return database.newsDao()
        }

        @Singleton
        @Provides
        fun provideWorkManagerGetInstance(
            @ApplicationContext context: Context
        ) : WorkManager{
            return WorkManager.getInstance(context)
        }

        @Singleton
        @Provides
        fun database(@ApplicationContext context: Context) : Database{
            return Room.databaseBuilder(
                context = context,
                klass = Database::class.java,
                name = "news_db"
            ).build()
        }

        @Singleton
        @Provides
        fun provideJson() : Json{
            return Json{
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }
        @Singleton
        @Provides
        fun provideConverterFactory(
            json: Json
        ) : Converter.Factory {
            return json.asConverterFactory("application/json".toMediaType())
        }

        @Singleton
        @Provides
        fun provideRetrofit(convert: Converter.Factory) : Retrofit{
            val baseUrl = "https://newsapi.org/"
            return Retrofit.Builder()
                .addConverterFactory(convert)
                .baseUrl(baseUrl)
                .build() }
        @Singleton
        @Provides
        fun provideApiService(
            retrofit: Retrofit
        ) : NewsApiService{
            return retrofit.create()
        }
    }

}