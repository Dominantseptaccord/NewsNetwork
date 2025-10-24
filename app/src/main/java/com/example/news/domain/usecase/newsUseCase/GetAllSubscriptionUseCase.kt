package com.example.news.domain.usecase.newsUseCase

import com.example.news.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSubscriptionUseCase @Inject constructor(
    val repository: Repository
) {
    operator fun invoke() : Flow<List<String>> {
        return repository.getAllSubscription()
    }
}