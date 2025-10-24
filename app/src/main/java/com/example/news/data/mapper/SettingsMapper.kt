package com.example.news.data.mapper

import com.example.news.domain.model.Interval
import com.example.news.domain.model.Language
import com.example.news.domain.model.RefreshConfig
import com.example.news.domain.model.Settings
import kotlin.enums.enumEntries

fun Settings.toRefreshConfig() : RefreshConfig{
    return RefreshConfig(
        language = language,
        interval = interval,
        isOnlyWifi = isOnlyWifi
    )
}

fun Language.queryToString() : String{
    return when(this){
        Language.ENGLISH -> "en"
        Language.KAZAKH -> "ar"
        Language.RUSSIAN -> "ru"
    }
}

fun List<Language>.languageToListString() : List<String> {
    return map {
        it.title
    }
}
fun List<Interval>.intervalToListString() : List<String> {
    return map {
        it.toString()
    }
}
