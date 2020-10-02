package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.DestinyBot.logger
import net.mamoe.mirai.event.MessageDsl
import net.mamoe.mirai.event.MessageSubscribersBuilder
import net.mamoe.mirai.message.MessageEvent

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