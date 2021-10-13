package net.origind.destinybot.features.bilibili

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Article(val id: Int, val publish_time: Int, val title: String)

@JsonClass(generateAdapter = true)
data class ArticlesData(val articles: List<Article>)

@JsonClass(generateAdapter = true)
data class Articles(val data: ArticlesData, val ttl: Int)

@JsonClass(generateAdapter = true)
data class LiveRoomInfo(val uid: Long, val room_id: Int, val title: String, val online: Int, val live_status: Int)

@JsonClass(generateAdapter = true)
data class LiveResponse(val code: Int, val data: LiveRoomInfo)
