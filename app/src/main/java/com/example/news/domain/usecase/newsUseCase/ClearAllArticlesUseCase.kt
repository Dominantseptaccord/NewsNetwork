package com.example.news.domain.usecase.newsUseCase

import com.example.news.domain.model.Article
import com.example.news.domain.repository.Repository
import jakarta.inject.Inject

class ClearAllArticlesUseCase @Inject constructor(
    val repository: Repository
) {
    suspend operator fun invoke(topics: List<String>) {
        repository.clearAllArticles(topics)
    }
}