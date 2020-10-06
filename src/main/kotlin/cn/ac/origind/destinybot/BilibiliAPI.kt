package cn.ac.origind.destinybot

import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.IOException

suspend fun getArticleListIDs(): List<Int> {
    val regex = Regex("window.articlelistIds = \\[([0-9,]+)]")

    val articleListIDs = regex.find(getBody("https://www.bilibili.com/read/readlist/rl175327"))?.groupValues?.get(1)!!
    return articleListIDs.split(",").map { Integer.parseInt(it) }.sortedDescending()
}

suspend fun getLatestArticle(): String {
    return getBody("https://www.bilibili.com/read/cv${getArticleListIDs().first()}/?from=readlist")
}

suspend fun getLatestWeeklyReportURL(): String {
    val regex = Regex("<img data-src=\"(//(\\w+:?\\w*@)?(\\S+)(:[0-9]+)?(/|/([\\w#!:.?+=&%@!\\-/]))?)\" width=\"2480\"")

    return regex.find(getLatestArticle())?.groupValues?.get(1)!!
}
