package com.example.news.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.domain.model.Interval
import com.example.news.domain.model.Language
import com.example.news.domain.model.Settings
import com.example.news.domain.usecase.settingsUseCase.GetSettingsUseCase
import com.example.news.domain.usecase.settingsUseCase.UpdateIntervalUseCase
import com.example.news.domain.usecase.settingsUseCase.UpdateLanguageUseCase
import com.example.news.domain.usecase.settingsUseCase.UpdateNotificationUseCase
import com.example.news.domain.usecase.settingsUseCase.UpdateWifiAccessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val getSettingsUseCase: GetSettingsUseCase,
    val updateIntervalUseCase: UpdateIntervalUseCase,
    val updateLanguageUseCase: UpdateLanguageUseCase,
    val updateNotificationUseCase: UpdateNotificationUseCase,
    val updateWifiAccessUseCase: UpdateWifiAccessUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<SettingsState>(SettingsState.InitialSettings)
    val state = _state.asStateFlow()

    fun processCommand(settingCommand: SettingCommand) {
        when (settingCommand) {
            is SettingCommand.IntervalSelect -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is SettingsState.Setting) {
                            updateIntervalUseCase(settingCommand.interval)
                            previousState.copy(setting = previousState.setting.copy(interval = settingCommand.interval))
                        } else {
                            previousState
                        }
                    }
                }
            }

            is SettingCommand.LanguageSelect -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is SettingsState.Setting) {
                            updateLanguageUseCase(settingCommand.language)
                            previousState.copy(setting = previousState.setting.copy(language = settingCommand.language))
                        } else {
                            previousState
                        }
                    }
                }
            }

            SettingCommand.ToggleNotification -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is SettingsState.Setting) {
                            val selected = !previousState.setting.isNotificationEnabled
                            updateNotificationUseCase(selected)
                            previousState.copy(
                                setting = previousState.setting.copy(
                                    isNotificationEnabled = selected
                                )
                            )
                        } else {
                            previousState
                        }
                    }
                }
            }

            SettingCommand.ToggleWifi -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is SettingsState.Setting) {
                            val selected = !previousState.setting.isOnlyWifi
                            updateWifiAccessUseCase(selected)
                            previousState.copy()
                        } else {
                            previousState
                        }
                    }
                }
            }
        }
    }
}
sealed interface SettingCommand{
    data class LanguageSelect(val language: Language) : SettingCommand
    data class IntervalSelect(val interval: Interval) : SettingCommand
    data object ToggleNotification : SettingCommand
    data object ToggleWifi : SettingCommand
}

sealed interface SettingsState{
    data object InitialSettings : SettingsState

    data class Setting(
        val setting: Settings,
    ) : SettingsState

}