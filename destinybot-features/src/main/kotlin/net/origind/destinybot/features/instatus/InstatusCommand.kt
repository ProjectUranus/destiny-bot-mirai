package net.origind.destinybot.features.instatus

import net.origind.destinybot.api.command.AbstractCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor

object InstatusCommand : AbstractCommand("/instatus") {
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        if (InstatusAPI.apiKey == null) {
            executor.sendMessage("未指定 Instatus API Key。")
            return
        }

    }

}
