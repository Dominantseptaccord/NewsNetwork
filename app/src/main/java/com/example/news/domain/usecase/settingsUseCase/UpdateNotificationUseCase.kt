package com.example.news.domain.usecase.settingsUseCase

import com.example.news.domain.repository.SettingRepository
import javax.inject.Inject

class UpdateNotificationUseCase @Inject constructor(
    val settingsRepository: SettingRepository
){
    suspend operator fun invoke(isEnabled: Boolean){
        settingsRepository.updateNotifications(isEnabled)
    }
}