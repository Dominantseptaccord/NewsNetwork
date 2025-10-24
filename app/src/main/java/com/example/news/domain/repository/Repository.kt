package com.example.news.domain.repository

import com.example.news.domain.model.Article
import com.example.news.domain.model.Language
import com.example.news.domain.model.RefreshConfig
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getAllSubscription() : Flow<List<String>>

    suspend fun addSubscription(topic: String)

    suspend fun updateArticlesForTopic(topic: String, language: Language) : Boolean

    suspend fun deleteSubscription(topic: String)

    fun getArticlesByTopic(topics: List<String>) : Flow<List<Article>>

    suspend fun clearAllArticles(topics: List<String>)

    suspend fun updateSubscriptionFromArticle(language: Language) : List<String>
    suspend fun getArticleByTopics(topics: List<String>) : Flow<List<Article>>

    suspend fun refreshBackground(refreshConfig: RefreshConfig)
}