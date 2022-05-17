package net.origind.destinybot.features.bilibili

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import net.origind.destinybot.api.command.AbstractCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor

lateinit var bilibiliConfig: BilibiliConfig

object StreamerCommand: AbstractCommand("下饭主播") {
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        executor.sendMessage(coroutineScope {
            buildString {
                var anyOnline = false
                bilibiliConfig.lives
                    .map { async(Dispatchers.IO) { getLiveRoomInfo(it) } }
                    .awaitAll()
                    .asSequence()
                    .filter { it.live_status == 1 }
                    .forEach { roomInfo ->
                        appendLine("你喜爱的主播：" + roomInfo.title + " 正在直播并有${roomInfo.online}人气值！https://live.bilibili.com/${roomInfo.room_id}")
                        anyOnline = true
                    }
                if (!anyOnline) append("你喜爱的主播们都不在直播哦O(∩_∩)O")
            }.trim()
        })
    }
}
