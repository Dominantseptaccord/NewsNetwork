package com.example.news.domain.usecase.settingsUseCase

import com.example.news.data.mapper.toRefreshConfig
import com.example.news.domain.model.RefreshConfig
import com.example.news.domain.repository.Repository
import com.example.news.domain.repository.SettingRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class RefreshSettingsUseCase @Inject constructor(
    val repository: Repository,
    val settingRepository: SettingRepository
) {
    suspend operator fun invoke() {
        return settingRepository.settingsObserver().map {
            it.toRefreshConfig()
        }.distinctUntilChanged()
            .onEach {
                repository.refreshBackground(it)
            }.collect()
    }
}