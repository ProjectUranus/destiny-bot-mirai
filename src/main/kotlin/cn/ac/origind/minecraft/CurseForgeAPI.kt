package cn.ac.origind.minecraft

import cn.ac.origind.destinybot.DestinyBot
import cn.ac.origind.minecraft.response.ImmibisProject
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText

suspend fun searchImmibis(criteria: String) : List<ImmibisProject> {
    val response = DestinyBot.client.get<HttpResponse>("https://fabricate.immibis.com/search?q=$criteria")
    val resultsRegex = Regex("<div id=\"results\" class=\"results\">(.+)<div class=\"versions\">", RegexOption.DOT_MATCHES_ALL)
    val downloadsRegex = Regex("<p title=\"Downloads\">([.\\w]+)</p>")
    val createdRegex = Regex("<p title=\"Created\">([-\\w]+)</p>")
    val lastUpdatedRegex = Regex("<p title=\"Last Updated\">([-\\w]+)</p>")
    val gameVersionRegex = Regex("<p title=\"Version\">([.\\w]+)</p>")
    val categoryRegex = Regex("<div class=\"(\\w+)-badge result-badge\">")
    val authorRegex = Regex("<a class=\"result-author\" href=\"https://www.curseforge.com/members/.+\">(.+)</a></p>")
    val urlRegex = Regex("<a class=\"result-name\" href=\"(https://(\\w+:?\\w*@)?(\\S+)(:[0-9]+)?(/|/([\\w#!:.?+=&%@\\-/]))?)\"><h2>(.+)</h2></a>")

    val html = resultsRegex.find(response.readText())?.groupValues?.get(1)!!
    return html.split("result gray-border rounded-border").drop(1).map { resultText ->
        val downloads = downloadsRegex.find(resultText)?.groupValues?.get(1)!!
        val createdTime = createdRegex.find(resultText)?.groupValues?.get(1)!!
        val lastUpdatedTime = lastUpdatedRegex.find(resultText)?.groupValues?.get(1)!!
        val gameVersion = gameVersionRegex.find(resultText)?.groupValues?.get(1)!!
        val url = urlRegex.find(resultText)?.groupValues?.get(1)!!
        val name = urlRegex.find(resultText)?.groupValues?.last()!!
        val categories = categoryRegex.findAll(resultText).map { it.groupValues[1] }.toList()
        val author = authorRegex.find(resultText)?.groupValues?.get(1)!!
        ImmibisProject(name, "Not implemented", author, downloads, createdTime, lastUpdatedTime, gameVersion, url, categories)
    }
}
