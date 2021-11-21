package net.origind.destinybot.features.bilibili

import net.origind.destinybot.api.command.*

class ManageStreamerCommand: AbstractCommand("添加主播") {

    init {
        permission = "admin.config.set"
        arguments += ArgumentContext("value", IntArgument)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val node = argument.getArgument<String>("node")
        val value = argument.getArgument<Int>("value")

        bilibiliConfig.lives
    }
}
