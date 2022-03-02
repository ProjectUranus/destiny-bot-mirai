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

@JsonClass(generateAdapter = true)
data class BilibiliResponse(var code: Int = 0, var message: String = "", var ttl: Int = 0)

@JsonClass(generateAdapter = true)
data class BilibiliDataResponse<T>(var code: Int = 0, var message: String = "", var ttl: Int = 0, var data: T)

@JsonClass(generateAdapter = true)
data class BilibiliUserInfoResponse(var code: Int = 0, var message: String = "", var ttl: Int = 0, var data: BilibiliUser? = null)

@JsonClass(generateAdapter = true)
data class BilibiliQueryResponse<T>(var numResults: Int = 0, var numPages: Int = 0, var page: Int = 0, var result: List<T> = emptyList())

@JsonClass(generateAdapter = true)
data class BilibiliUserDataResponse(var code: Int = 0, var message: String = "", var ttl: Int = 0, var data: BilibiliUserQueryResponse? = null)

@JsonClass(generateAdapter = true)
data class BilibiliUserQueryResponse(var numResults: Int = 0, var numPages: Int = 0, var page: Int = 0, var result: List<BilibiliUser> = emptyList())

@JsonClass(generateAdapter = true)
data class BilibiliSameFollowResponse(var code: Int = 0, var message: String = "", var ttl: Int = 0, var data: BilibiliSameFollowDataResponse? = null)

@JsonClass(generateAdapter = true)
data class BilibiliSameFollowDataResponse(var total: Int = 0, var list: List<BilibiliUser> = emptyList())

@JsonClass(generateAdapter = true)
data class BilibiliUser(var mid: Long, var uname: String? = null, var name: String? = null) {
    fun name() = uname ?: name ?: ""
}