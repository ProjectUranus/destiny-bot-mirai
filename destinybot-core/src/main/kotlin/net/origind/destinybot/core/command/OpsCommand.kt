package net.origind.destinybot.core.command

import net.origind.destinybot.api.command.AbstractCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor
import net.origind.destinybot.core.DestinyBot

object OpsCommand: AbstractCommand("/ops") {
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        executor.sendMessage(DestinyBot.ops.joinToString())
    }
}
