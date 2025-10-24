package com.example.news.domain.usecase.newsUseCase

import com.example.news.domain.repository.Repository
import jakarta.inject.Inject

class DeleteSubscriptionUseCase @Inject constructor(
    val repository: Repository
){
    suspend operator fun invoke(topic: String){
        repository.deleteSubscription(topic)
    }
}