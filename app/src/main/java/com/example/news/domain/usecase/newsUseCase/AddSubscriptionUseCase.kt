package com.example.news.domain.usecase.newsUseCase

import com.example.news.data.mapper.queryToString
import com.example.news.domain.repository.Repository
import com.example.news.domain.repository.SettingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class AddSubscriptionUseCase @Inject constructor(
    val repository: Repository,
    val settingRepository: SettingRepository
) {
    suspend operator fun invoke(topic: String){
        repository.addSubscription(topic)
        CoroutineScope(coroutineContext).launch {
            val language = settingRepository.settingsObserver().first().language
            repository.updateArticlesForTopic(topic,language)
        }
    }
}