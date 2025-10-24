package com.example.news.domain.model

data class Settings(
    val language: Language,
    val interval: Interval,
    val isNotificationEnabled: Boolean,
    val isOnlyWifi: Boolean
){
    companion object{
        val DEFAULT_LANGUAGE = Language.ENGLISH
        val DEFAULT_INTERVAL = Interval.MIN_15
        const val DEFAULT_NOTIFICATION = false
        const val DEFAULT_WIFI = false
    }
}

enum class Language(val title: String){
    ENGLISH("English"),KAZAKH("Kazakh"),RUSSIAN("Russian")
}
enum class Interval(val minutes: Int){
    MIN_15(15),
    MIN_30(30),
    HOUR_1(60),
    HOUR_3(180),
    HOUR_6(360),
    HOUR_12(720),
    HOUR_24(1440)
}