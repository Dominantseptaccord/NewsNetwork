package com.example.news.data.mapper

import com.example.news.data.local.ArticleDbModel
import com.example.news.data.remote.NewsResponseDto
import com.example.news.domain.model.Article
import com.example.news.domain.model.Interval
import java.text.SimpleDateFormat
import java.util.Locale


fun NewsResponseDto.toDbModel(topic: String) : List<ArticleDbModel>{
    return articles.map {
        ArticleDbModel(
            title = it.title,
            description = it.description,
            imageUrl = it.urlToImage,
            sourceName = it.source.name,
            publishedAt = it.publishedAt.toTimestamp(),
            url = it.url,
            topic = topic
        )
    }
}
fun String.toTimestamp() : Long{
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    return dateFormatter.parse(this).time ?: System.currentTimeMillis()
}

fun List<ArticleDbModel>.toEntities() : List<Article>{
    return map {
        Article(
            title = it.title,
            description = it.description,
            imageUrl = it.imageUrl,
            sourceName = it.sourceName,
            publishedAt = it.publishedAt,
            url = it.url
        )
    }
}

fun Int.toInterval() : Interval{
    return Interval.entries.first{it.minutes==this}
}