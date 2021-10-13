package net.origind.destinybot.features.destiny

import net.origind.destinybot.api.command.*
import net.origind.destinybot.features.bilibili.getLatestWeeklyReportURL
import net.origind.destinybot.features.destiny.image.getImage
import net.origind.destinybot.features.destiny.image.toByteArray

object WeeklyReportCommand: AbstractCommand("周报") {
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        if (executor is UserCommandExecutor) {
            executor.sendImage(getImage("https:${getLatestWeeklyReportURL()}").toByteArray())
        }
    }
}
