package com.example.news

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.news.presentation.startup.AppStartupManager
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class NewsApp : Application(), Configuration.Provider{
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var startupManager: AppStartupManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        startupManager.startRefresh()
    }
}