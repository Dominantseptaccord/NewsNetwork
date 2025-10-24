package com.example.news.domain.model

data class RefreshConfig(
    val language: Language,
    val interval: Interval,
    val isOnlyWifi: Boolean
)