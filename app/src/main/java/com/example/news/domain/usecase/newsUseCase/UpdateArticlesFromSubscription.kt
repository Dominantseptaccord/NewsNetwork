package com.example.news.domain.usecase.newsUseCase

import com.example.news.data.repository.SettingRepositoryImpl
import com.example.news.domain.model.Language
import com.example.news.domain.repository.Repository
import com.example.news.domain.repository.SettingRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

class UpdateArticlesFromSubscription @Inject constructor(
    val repository: Repository,
    val settingRepository: SettingRepository

){
    suspend operator fun invoke() : List<String>{
        return repository.updateSubscriptionFromArticle(settingRepository.settingsObserver().first().language)
    }

}