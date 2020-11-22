package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.response.bilibili.Articles

suspend fun getArticleListIDs(): List<Int> {
    val articles = getJson<Articles>("https://api.bilibili.com/x/article/list/articles?id=175327&jsonp=jsonp", false).data.articles
    return articles.map { it.id }
}

suspend fun getLatestArticle(): String {
    return getBody("https://www.bilibili.com/read/cv${getArticleListIDs().last()}/?from=readlist", false)
}

suspend fun getLatestWeeklyReportURL(): String {
    val regex = Regex("<img data-src=\"(//(\\w+:?\\w*@)?(\\S+)(:[0-9]+)?(/|/([\\w#!:.?+=&%@!\\-/]))?)\" width=\"2598\"")

    return regex.find(getLatestArticle())?.groupValues?.get(1)!!
}
