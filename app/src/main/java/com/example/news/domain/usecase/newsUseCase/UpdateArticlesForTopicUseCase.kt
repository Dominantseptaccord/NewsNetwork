package com.example.news.domain.usecase.newsUseCase

import com.example.news.data.repository.SettingRepositoryImpl
import com.example.news.domain.model.Language
import com.example.news.domain.repository.Repository
import com.example.news.domain.repository.SettingRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

class UpdateArticlesForTopicUseCase @Inject constructor(
    val repository: Repository,
    val settingRepository: SettingRepository
) {
    suspend operator fun invoke(topic: String, language: Language) : Boolean{
        return repository.updateArticlesForTopic(topic,settingRepository.settingsObserver().first().language)
    }

}