package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.DestinyBot.config
import cn.ac.origind.destinybot.DestinyBot.logger
import cn.ac.origind.destinybot.config.AppSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mamoe.mirai.event.MessageDsl
import net.mamoe.mirai.event.MessageSubscribersBuilder
import net.mamoe.mirai.getFriendOrNull
import net.mamoe.mirai.message.MessageEvent
import okhttp3.Request

val DEBUG : Boolean get() = config[AppSpec.debug]

@MessageDsl
fun <M : MessageEvent, Ret, R : RR, RR> MessageSubscribersBuilder<M, Ret, R, RR>.caseAny(
    vararg equals: String,
    ignoreCase: Boolean = false,
    trim: Boolean = true
): MessageSubscribersBuilder<M, Ret, R, RR>.ListeningFilter {
    val equalsSequence = equals.asSequence()
    return if (trim) {
        content { text -> equalsSequence.any { it.equals(text.trim(), ignoreCase) } }
    } else {
        content { text -> equalsSequence.any { it.equals(text, ignoreCase) } }
    }
}

@MessageDsl
fun <M : MessageEvent, Ret, R : RR, RR> MessageSubscribersBuilder<M, Ret, R, RR>.caseAny(
    equals: Collection<String>,
    ignoreCase: Boolean = false,
    trim: Boolean = true
): MessageSubscribersBuilder<M, Ret, R, RR>.ListeningFilter {
    return if (trim) {
        if (ignoreCase)
            content { text -> equals.any { it.equals(text.trim(), ignoreCase) } }
        else
            content { text -> equals.contains(text.trim()) }
    } else {
        if (ignoreCase)
            content { text -> equals.any { it.equals(text, ignoreCase) } }
        else
            content { text -> equals.contains(text) }
    }
}

@MessageDsl
fun <M : MessageEvent, Ret, R : RR, RR> MessageSubscribersBuilder<M, Ret, R, RR>.containsAny(
    vararg contains: String,
    ignoreCase: Boolean = false,
    trim: Boolean = true
): MessageSubscribersBuilder<M, Ret, R, RR>.ListeningFilter {
    val containsSequence = contains.asSequence()
    return if (trim) {
        val toCheck = containsSequence.map { it.trim() }
        content { text -> toCheck.any { text.contains(it, ignoreCase) } }
    } else {
        content { text -> containsSequence.any { text.contains(it, ignoreCase) } }
    }
}

private val loggingFriend get() = DestinyBot.bot.getFriendOrNull(1276571946)

fun groupLog(message: String) {
    if (DEBUG)
        loggingFriend?.launch {
            loggingFriend?.sendMessage(message)
        }
}


/**
 * @throws RuntimeException
 */
inline fun <reified T> T?.orLogThrow(msg: String, e: Throwable? = null) : T {
    if (this != null) return this
    else {
        val exception = e ?: NullPointerException()
        logger.error(msg, exception)
        throw RuntimeException(exception)
    }
}

suspend inline fun getBody(url: String, proxy: Boolean = true, crossinline init: Request.Builder.() -> Unit = {}) = withContext(Dispatchers.IO) {
    val request = Request.Builder().apply {
        url(url)
        init()
    }.build()
    val call = (if(proxy)client else rawClient).newCall(request)
    val response = call.execute()
    groupLog("请求 $url (proxy = $proxy) 耗时：" + (response.receivedResponseAtMillis - response.sentRequestAtMillis) + "ms")
    response.body?.string() ?: ""
}

suspend inline fun <reified T> getJson(url: String, proxy: Boolean = true, crossinline init: Request.Builder.() -> Unit = {}): T = withContext(Dispatchers.IO) {
    val request = Request.Builder().apply {
        url(url)
        init()
    }.build()
    val call = (if(proxy)client else rawClient).newCall(request)
    val response = call.execute()
    groupLog("请求 $url (proxy = $proxy) 耗时：" + (response.receivedResponseAtMillis - response.sentRequestAtMillis) + "ms")
    val json = response.body?.string()!!
    moshi.adapter(T::class.java).fromJson(json)!!
}
