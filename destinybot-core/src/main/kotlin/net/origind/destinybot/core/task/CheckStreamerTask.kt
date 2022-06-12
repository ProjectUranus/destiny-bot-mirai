package net.origind.destinybot.core.task

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import net.origind.destinybot.core.DestinyBot
import net.origind.destinybot.features.bilibili.LiveRoomInfo
import net.origind.destinybot.features.bilibili.bilibiliConfig
import net.origind.destinybot.features.bilibili.getLiveRoomInfo
import net.origind.destinybot.features.bilibili.getUserInfo

private val onlineStreamers = mutableSetOf<LiveRoomInfo>()

suspend fun checkStreamer() {
    var anyOnline = false

    val str = coroutineScope {
        buildString {
            val infos = bilibiliConfig.lives
                .map { async(Dispatchers.IO) { getLiveRoomInfo(it) } }
                .awaitAll()
                .toSet()

            val offline = infos
                .asSequence()
                .filter { it.live_status == 0 }
                .filter { onlineStreamers.any { info2 -> it.room_id == info2.room_id } }
                .toSet()
            offline.forEach { roomInfo ->
                appendLine("主播 " + roomInfo.title + " 下播了...")
                anyOnline = true
            }
            onlineStreamers.removeIf { offline.any { info -> it.room_id == info.room_id } }

            infos.asSequence()
                .filter { it.live_status == 1 }
                .filter { onlineStreamers.none { info -> it.room_id == info.room_id } }
                .forEach { roomInfo ->
                    appendLine("你喜爱的主播 " + getUserInfo(roomInfo.uid)?.name() + " 正在直播: ${roomInfo.title}！https://live.bilibili.com/${roomInfo.room_id}")
                    anyOnline = true
                    onlineStreamers += roomInfo
                }
        }.trim()
    }
    if (!anyOnline) return
    for (id in bilibiliConfig.replyStreamersTo) {
        DestinyBot.bot.getGroup(id)?.sendMessage(str)
    }
}
