package net.origind.destinybot.core.command

import net.origind.destinybot.api.command.*
import net.origind.destinybot.core.DestinyBot

object OpCommand: AbstractCommand("/op") {
    init {
        arguments += ArgumentContext("id", LongArgument)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        if (executor is MiraiUserCommandExecutor && executor.user.id in DestinyBot.ops) {
            DestinyBot.ops += argument.getArgument<Long>("id")
            DestinyBot.config.set<MutableList<Long>>("bot.ops", DestinyBot.ops)
            DestinyBot.config.save()
            executor.sendMessage("Opped ${executor.user.id}")
        } else {
            executor.sendMessage("无权执行该命令。")
        }
    }

}
