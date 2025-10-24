package com.example.news.domain.usecase.settingsUseCase

import com.example.news.domain.repository.SettingRepository
import javax.inject.Inject

class UpdateWifiAccessUseCase @Inject constructor(
    val settingsRepository: SettingRepository
){
    suspend operator fun invoke(isEnabled: Boolean){
        settingsRepository.updateWifiAccess(isEnabled)
    }
}