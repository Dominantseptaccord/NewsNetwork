package com.example.news.data.repository

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import coil3.network.NetworkRequest
import com.example.news.data.background.RefreshBackground
import com.example.news.data.local.ArticleDbModel
import com.example.news.data.local.NewsDao
import com.example.news.data.local.SubscriptionDbModel
import com.example.news.data.mapper.queryToString
import com.example.news.data.mapper.toDbModel
import com.example.news.data.mapper.toEntities
import com.example.news.data.mapper.toRefreshConfig
import com.example.news.data.remote.NewsApiService
import com.example.news.domain.model.Article
import com.example.news.domain.model.Language
import com.example.news.domain.model.RefreshConfig
import com.example.news.domain.repository.Repository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException

class NewsRepositoryImpl @Inject constructor(
    val newsDao: NewsDao,
    val newsApiService: NewsApiService,
    val workManager: WorkManager,
) : Repository {
    override fun getAllSubscription(): Flow<List<String>> {
        return newsDao.getAllSubscription().map { lst ->
            lst.map { subscriptionDbModel ->
                subscriptionDbModel.topic
            }
        }
    }

    override suspend fun addSubscription(topic: String) {
        newsDao.addSubscription(SubscriptionDbModel(topic))
    }

    override suspend fun updateArticlesForTopic(topic: String,language: Language) : Boolean {
        val articles = loadAllArticles(topic,language)
        val ids = newsDao.addArticles(articles)
        Log.d("UpdatedArticlesForTopic", "$articles and $ids")
        return ids.any{ it!=-1L }
    }

    suspend fun loadAllArticles(topic: String, language: Language) : List<ArticleDbModel>{
        return try {
            val response = newsApiService.loadArticles(topic, language.queryToString())
            val tr = response.toDbModel(topic)
            Log.d("Proverka", "$tr")
            tr
        } catch (e: CancellationException) {
            Log.w("NewsRepositoryImpl", "Loading articles cancelled for topic=$topic", e)
            throw e
        } catch (e: Exception) {
            Log.e("NewsRepositoryImpl", "Failed to load articles for topic=$topic, lang=${language.queryToString()}", e)
            emptyList()
        }
    }
    override suspend fun deleteSubscription(topic: String) {
        newsDao.deleteSubscription(SubscriptionDbModel(topic))
    }


    override fun getArticlesByTopic(topics: List<String>): Flow<List<Article>> {
        return newsDao.getArticlesByTopic(topics).map {
            it.toEntities()
        }
    }

    override suspend fun clearAllArticles(topics: List<String>) {
        newsDao.deleteArticlesByTopics(topics)
    }

    override suspend fun updateSubscriptionFromArticle(language: Language) : List<String> {
        val updatedTopics = mutableListOf<String>()
        val subscriptions = getAllSubscription().first()
        coroutineScope {
            subscriptions.forEach {
                val updated = updateArticlesForTopic(it,language)
                Log.d("IDS","$it")
                if(updated){
                    updatedTopics.add(it)
                }
            }
        }
        return updatedTopics
    }

    override suspend fun getArticleByTopics(topics: List<String>): Flow<List<Article>> {
        return newsDao.getArticlesByTopic(topics).map {
            it.toEntities()
        }
    }

    override suspend fun refreshBackground(refreshConfig: RefreshConfig){
        val constraint = Constraints.Builder().setRequiredNetworkType(
            if(refreshConfig.isOnlyWifi){
                NetworkType.UNMETERED
            }
            else{
                NetworkType.CONNECTED
            }
        ).setRequiresBatteryNotLow(true)
            .build()
        val request = PeriodicWorkRequestBuilder<RefreshBackground>(
            repeatInterval = refreshConfig.interval.minutes.toLong(),
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        ).setConstraints(constraint)
            .build()
        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = "refresher",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.REPLACE,
            request = request
        )
    }
}