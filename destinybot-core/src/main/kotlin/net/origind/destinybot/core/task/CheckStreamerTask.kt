package net.origind.destinybot.core.task

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import net.origind.destinybot.core.DestinyBot
import net.origind.destinybot.features.bilibili.bilibiliConfig
import net.origind.destinybot.features.bilibili.getLiveRoomInfo

suspend fun checkStreamer() {
    var anyOnline = false

    val str = coroutineScope {
        buildString {
            bilibiliConfig.lives
                .map { async(Dispatchers.IO) { getLiveRoomInfo(it) } }
                .awaitAll()
                .asSequence()
                .filter { it.live_status == 1 }
                .forEach { roomInfo ->
                    appendLine("你喜爱的主播：" + roomInfo.title + " 正在直播并有${roomInfo.online}人气值！https://live.bilibili.com/${roomInfo.room_id}")
                    anyOnline = true
                }
        }.trim()
    }
    if (!anyOnline) return
    for (id in bilibiliConfig.replyStreamersTo) {
        DestinyBot.bot.getGroup(id)?.sendMessage(str)
    }
}
