package com.example.news.domain.repository

import com.example.news.domain.model.Interval
import com.example.news.domain.model.Language
import com.example.news.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    fun settingsObserver() : Flow<Settings>

    suspend fun updateLanguage(language: Language)
    suspend fun updateInterval(interval: Interval)
    suspend fun updateNotifications(isEnabled: Boolean)
    suspend fun updateWifiAccess(isEnabled: Boolean)
}