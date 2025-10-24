package com.example.news.domain.usecase.newsUseCase

import com.example.news.domain.model.Article
import com.example.news.domain.repository.Repository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetArticleByTopicUseCase @Inject constructor(
    val repository: Repository
) {
    operator fun invoke(topics: List<String>) : Flow<List<Article>> {
        return repository.getArticlesByTopic(topics)
    }
}