package com.example.news.data.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.example.news.R
import com.example.news.presentation.screen.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


class NotificationBackground @Inject constructor(
    @param:ApplicationContext val context: Context
) {
    val intent = Intent(context, MainActivity::class.java)
    val notificationManager = context.getSystemService<NotificationManager>()
    init {
        createChannel()
    }
    fun createChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.new_articles),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }
    fun showNotificationArticles(notificationList: List<String>){
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_PD,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationContent = NotificationCompat.Builder(context,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_articles)
            .setContentTitle(
                context.getString(R.string.new_articles_notification_title)
            )
            .setContentText(
                "Updated ${notificationList.size}\nSubscriptions: ${notificationList.joinToString(separator = ", ")}"
            )
            .setContentIntent(
                pendingIntent
            )
            .setAutoCancel(true)
            .build()
        notificationManager?.notify(NOTIFICATION_ID, notificationContent)
    }
    companion object {
        const val CHANNEL_ID = "articles"
        const val NOTIFICATION_ID = 1
        const val REQUEST_CODE_PD = 1
    }
}
