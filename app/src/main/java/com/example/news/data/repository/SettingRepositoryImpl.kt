package com.example.news.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.news.data.mapper.toInterval
import com.example.news.domain.model.Interval
import com.example.news.domain.model.Language
import com.example.news.domain.model.Settings
import com.example.news.domain.repository.SettingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SettingRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SettingRepository {
    private val languagePreferred = stringPreferencesKey("language")
    private val intervalPreferred = intPreferencesKey("interval")
    private val notificationEnabledPreferred = booleanPreferencesKey("notification")
    private val wifiEnabled = booleanPreferencesKey("wifi")
    override fun settingsObserver(): Flow<Settings> {
        return context.dataStore.data.map {settings ->
            val prefLanguage = Language.valueOf(settings[languagePreferred] ?: Settings.DEFAULT_LANGUAGE.name)
            val prefInterval = settings[intervalPreferred]?.toInterval() ?: Settings.DEFAULT_INTERVAL
            val prefNotification = settings[notificationEnabledPreferred] ?: Settings.DEFAULT_NOTIFICATION
            val prefWifi = settings[wifiEnabled] ?: Settings.DEFAULT_WIFI
            Settings(
                language = prefLanguage,
                interval = prefInterval,
                isNotificationEnabled = prefNotification,
                isOnlyWifi = prefWifi
            )
        }
    }

    override suspend fun updateLanguage(language: Language) {
        context.dataStore.edit {settings ->
            val preferredLanguage = language.name
            settings[languagePreferred] = preferredLanguage
        }
    }

    override suspend fun updateInterval(interval: Interval) {
        context.dataStore.edit {settings ->
            val preferredInterval = interval.name.toInt()
            settings[intervalPreferred] = preferredInterval
        }
    }

    override suspend fun updateNotifications(isEnabled: Boolean) {
        context.dataStore.edit {settings ->
            val preferredNotification = isEnabled
            settings[notificationEnabledPreferred] = preferredNotification
        }
    }

    override suspend fun updateWifiAccess(isEnabled: Boolean) {
        context.dataStore.edit {settings ->
            val preferredWifi = isEnabled
            settings[wifiEnabled] = preferredWifi
        }
    }

}