package net.origind.destinybot.features.bilibili

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.origind.destinybot.api.command.AbstractCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor

lateinit var bilibiliConfig: BilibiliConfig

object StreamerCommand: AbstractCommand("下饭主播") {
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        executor.sendMessage(buildString {
            var anyOnline = false
            for (id in bilibiliConfig.lives) {
                val roomInfo = withContext(Dispatchers.IO) {
                    getLiveRoomInfo(id)
                }
                if (roomInfo.live_status == 1) {
                    appendLine("你喜爱的主播：" + roomInfo.title + " 正在直播并有${roomInfo.online}人气值！https://live.bilibili.com/$id")
                    anyOnline = true
                }
            }
            if (!anyOnline) append("你喜爱的主播们都不在直播哦O(∩_∩)O")
        }.trim())
    }
}
