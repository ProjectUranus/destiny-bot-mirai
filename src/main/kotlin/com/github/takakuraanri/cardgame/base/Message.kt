package com.github.takakuraanri.cardgame.base

import net.mamoe.mirai.message.data.Message

interface MessageSender {
    suspend fun sendMessage(message: Message)
}
