package com.example.news.domain.usecase.settingsUseCase

import com.example.news.domain.model.Language
import com.example.news.domain.repository.SettingRepository
import javax.inject.Inject

class UpdateLanguageUseCase @Inject constructor(
    val settingsRepository: SettingRepository
){
    suspend operator fun invoke(language: Language){
        settingsRepository.updateLanguage(language)
    }
}