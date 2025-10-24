package com.example.news.domain.usecase.settingsUseCase

import com.example.news.domain.model.Interval
import com.example.news.domain.repository.SettingRepository
import javax.inject.Inject

class UpdateIntervalUseCase @Inject constructor(
    val settingsRepository: SettingRepository
){
    suspend operator fun invoke(interval: Interval){
        settingsRepository.updateInterval(interval)
    }
}