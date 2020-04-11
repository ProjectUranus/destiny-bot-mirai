package com.github.takakuraanri.cardgame.base

import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.MessageDsl
import net.mamoe.mirai.event.MessagePacketSubscribersBuilder
import net.mamoe.mirai.event.MessageSubscribersBuilder
import net.mamoe.mirai.message.MessagePacket

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
fun MessagePacketSubscribersBuilder.caseAny(
    vararg equals: String,
    ignoreCase: Boolean = false,
    trim: Boolean = true
): MessageSubscribersBuilder<MessagePacket<*, *>, Listener<MessagePacket<*, *>>, Unit, Unit>.ListeningFilter {
    val equalsSequence = equals.asSequence()
    return if (trim) {
        val toCheck = equalsSequence.map { it.trim() }
        content { text -> toCheck.any { it.equals(text, ignoreCase) } }
    } else {
        content { text -> equalsSequence.any { it.equals(text, ignoreCase) } }
    }
}

@MessageDsl
fun MessagePacketSubscribersBuilder.containsAny(
    vararg contains: String,
    ignoreCase: Boolean = false,
    trim: Boolean = true
): MessageSubscribersBuilder<MessagePacket<*, *>, Listener<MessagePacket<*, *>>, Unit, Unit>.ListeningFilter {
    val containsSequence = contains.asSequence()
    return if (trim) {
        val toCheck = containsSequence.map { it.trim() }
        content { text -> toCheck.any { text.contains(it, ignoreCase) } }
    } else {
        content { text -> containsSequence.any { text.contains(it, ignoreCase) } }
    }
}
