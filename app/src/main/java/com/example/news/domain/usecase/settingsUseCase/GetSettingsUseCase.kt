package com.example.news.domain.usecase.settingsUseCase

import com.example.news.domain.repository.SettingRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    val settingRepository: SettingRepository
){
    operator fun invoke() = settingRepository.settingsObserver()
}