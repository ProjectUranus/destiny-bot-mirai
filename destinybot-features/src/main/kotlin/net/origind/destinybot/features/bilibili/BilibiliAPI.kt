package net.origind.destinybot.features.bilibili

import net.origind.destinybot.features.getBodyAsync
import net.origind.destinybot.features.getJson
import okhttp3.FormBody

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

suspend fun searchUser(keyword: String): List<BilibiliUser> =
    getJson<BilibiliUserDataResponse>("http://api.bilibili.com/x/web-interface/search/type?search_type=bili_user&keyword=$keyword&page=1")
        .data?.result ?: emptyList()

suspend fun follow(csrf: String, cookie: String, fid: String): BilibiliResponse =
    getJson("https://api.bilibili.com/x/relation/modify") {
        header("Cookie", cookie)
        post(FormBody.Builder()
            .addEncoded("fid", fid)
            .addEncoded("act", "1")
            .addEncoded("re_src", "11")
            .addEncoded("csrf", csrf)
            .build())
    }

suspend fun sameFollow(cookie: String, vmid: Long) =
    getJson<BilibiliSameFollowResponse>("https://api.bilibili.com/x/relation/same/followings?ps=3000&vmid=$vmid") {
        header("Cookie", cookie)
    }.data?.list ?: emptyList()

suspend fun getUserInfo(mid: Long) =
    getJson<BilibiliUserInfoResponse>("https://api.bilibili.com/x/space/acc/info?mid=$mid").data