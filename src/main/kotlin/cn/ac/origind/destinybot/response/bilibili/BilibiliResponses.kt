package cn.ac.origind.destinybot.response.bilibili

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Article(val id: Int, val publish_time: Int, val title: String)

@JsonClass(generateAdapter = true)
data class ArticlesData(val articles: List<Article>)

@JsonClass(generateAdapter = true)
data class Articles(val data: ArticlesData, val ttl: Int)
