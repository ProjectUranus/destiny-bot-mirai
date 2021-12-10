package net.origind.destinybot.features.bilibili

import net.origind.destinybot.features.getBodyAsync
import net.origind.destinybot.features.getJson

suspend fun getArticleListIDs(): List<Int> {
    val articles = getJson<Articles>("https://api.bilibili.com/x/article/list/articles?id=175327&jsonp=jsonp").data.articles
    return articles.map { it.id }
}

suspend fun getLatestArticle(): String {
    return getBodyAsync("https://www.bilibili.com/read/cv${getArticleListIDs().last()}/?from=readlist").await()
}

suspend fun getLatestWeeklyReportURL(): String {
    val regex = Regex("<img data-src=\"(//(\\w+:?\\w*@)?(\\S+)(:[0-9]+)?(/|/([\\w#!:.?+=&%@\\-/]))?)\" width=\"\\d\\d\\d\\d\"")

    return regex.find(getLatestArticle())?.groupValues?.lastOrNull { it.isNotBlank() }!!
}

suspend fun getLiveRoomInfo(id: Long): LiveRoomInfo {
    return getJson<LiveResponse>("https://api.live.bilibili.com/room/v1/Room/get_info?id=$id").data
}
