package net.origind.destinybot.features.destiny

import net.mamoe.mirai.message.data.buildMessageChain
import net.origind.destinybot.api.command.*
import net.origind.destinybot.core.features.bilibili.getLatestWeeklyReportURL
import net.origind.destinybot.core.upload
import net.origind.destinybot.features.destiny.image.getImage

object WeeklyReportCommand: AbstractCommand("周报") {
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        if (executor is UserCommandExecutor) {
            executor.sendMessage(buildMessageChain {
                add(getImage("https:${getLatestWeeklyReportURL()}").upload(executor.user))
            })
        }
    }

}
