package com.example.news.presentation.startup

import com.example.news.domain.usecase.settingsUseCase.RefreshSettingsUseCase
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class AppStartupManager @Inject constructor(
    private val refreshSettingsUseCase: RefreshSettingsUseCase
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    fun startRefresh(){
        scope.launch {
            refreshSettingsUseCase()
        }
    }
}