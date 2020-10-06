package cn.ac.origind.pricechallange

import cn.ac.origind.destinybot.getBody
import cn.ac.origind.minecraft.urlRegex
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant

/**
 * 搜索比价产品
 */
suspend fun searchChallengeProduct(product: String): Flow<PriceAtDate> {
    val regex = Regex("<a class=\"shenqingGY\"  href=\"/jdDetail\\.aspx\\?originalUrl=(${urlRegex})\"")
    val html = getBody("http://s.manmanbuy.com/Default.aspx?key=${product}&btnSearch=%CB%D1%CB%F7")
    val url = regex.find(html)?.groupValues?.get(1) ?: ""
    return getHistoricalPrice(url)
}

/**
 * 搜索优惠券
 */
suspend fun searchPromotionProduct(product: String) {
    val response = getBody("http://zhekou.manmanbuy.com/searchnew.aspx?key=$product")
}

suspend fun getHistoricalPrice(link: String) = flow {
    val regex = Regex("flotChart\\.chartNow\\(\'(.+)\'\\)")
    val valueRegex = Regex("\\[\\d+,[-+]?[0-9]*\\.?[0-9]+.,\"([\\w]|[^\\x00-\\xff])*\"],")
    val html = getBody("http://p.zwjhl.com/price.aspx?url=${link}")
    val body = regex.find(html)?.groupValues?.get(1) ?: ""
    valueRegex.findAll(body).forEach {
        val arr = it.value.replace("[", "").replace("]", "").trim().split(",").filterNot { str -> str.isBlank() }
        val date = Instant.ofEpochMilli(arr[0].toLong())
        val price = arr[1].toDouble()
        val text = arr[2]
        emit(PriceAtDate(date, price, text))
    }
}

data class PriceAtDate(val date: Instant, val price: Double, val text: String)
