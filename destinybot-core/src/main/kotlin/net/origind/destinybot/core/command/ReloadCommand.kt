package net.origind.destinybot.core.command

import net.origind.destinybot.api.command.AbstractCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor
import net.origind.destinybot.core.DestinyBot

object ReloadCommand: AbstractCommand("/reload") {
    init {
        permission = "admin.reload"
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        DestinyBot.config.load()
        DestinyBot.reloadConfig()
        for (plugin in DestinyBot.plugins) {
            plugin.reload()
        }
        executor.sendMessage("机器人已重载。")
    }
}
