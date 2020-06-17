package com.github.takakuraanri.cardgame.base

import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.MessageDsl
import net.mamoe.mirai.event.MessagePacketSubscribersBuilder
import net.mamoe.mirai.event.MessageSubscribersBuilder
import net.mamoe.mirai.message.MessageEvent

fun List<Card>.isContinuously(): Boolean {
    if (isNullOrEmpty()) return false
    val firstCard = first()
    for (i in 0 until size) {
        if (get(i).amount != i + firstCard.amount)
            return false
    }
    return true
}

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
