package net.origind.destinybot.core.command

import net.origind.destinybot.api.command.*
import net.origind.destinybot.core.DestinyBot

object DeopCommand: AbstractCommand("/deop") {
    init {
        arguments += ArgumentContext("id", QQArgument)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        if (executor is MiraiUserCommandExecutor && DestinyBot.ops.contains(executor.user.id)) {
            val id = argument.getArgument<Long>("id")
            DestinyBot.ops -= id
            DestinyBot.config.set<MutableList<Long>>("bot.ops", DestinyBot.ops)
            DestinyBot.config.save()
            executor.sendMessage("De-Opped $id")
        } else {
            executor.sendMessage("无权执行该命令。")
        }
    }

}
