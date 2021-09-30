package net.origind.destinybot.features.destiny

import cn.ac.origind.destinybot.features.bilibili.getLatestWeeklyReportURL
import cn.ac.origind.destinybot.image.getImage
import cn.ac.origind.destinybot.upload
import net.mamoe.mirai.message.data.buildMessageChain
import net.origind.destinybot.api.command.*

object WeeklyReportCommand: AbstractCommand("周报") {
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        if (executor is UserCommandExecutor) {
            executor.sendMessage(buildMessageChain {
                add(getImage("https:${getLatestWeeklyReportURL()}", false).upload(executor.user))
            })
        }
    }

}
