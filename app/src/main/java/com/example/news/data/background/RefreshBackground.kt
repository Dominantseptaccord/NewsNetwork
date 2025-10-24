package com.example.news.data.background

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.news.data.mapper.queryToString
import com.example.news.domain.usecase.newsUseCase.UpdateArticlesFromSubscription
import com.example.news.domain.usecase.settingsUseCase.GetSettingsUseCase
import com.example.news.domain.usecase.settingsUseCase.RefreshSettingsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class RefreshBackground @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val updateSubscribeArticlesFromSubscription: UpdateArticlesFromSubscription,
    private val getSettingsUseCase: GetSettingsUseCase,
    val notificationBackground: NotificationBackground?,
) : CoroutineWorker(context,workerParams) {
    override suspend fun doWork(): Result {
        Log.d("RefreshWorker","Start")
        val isNotificationEnabled = getSettingsUseCase().first().isNotificationEnabled
        Log.d("RefreshWorker","$isNotificationEnabled")
        val topics = updateSubscribeArticlesFromSubscription()
        Log.d("RefreshWorker","$topics")
        if(topics.isNotEmpty()){
            Log.d("RefreshWorker", "$topics")
            notificationBackground?.showNotificationArticles(topics)
        }
        Log.d("RefreshWorker","Finish")
        return Result.success()
    }
}