package net.origind.destinybot.features.bilibili

import net.origind.destinybot.api.command.*

class ManageStreamerCommand: AbstractCommand("添加主播") {

    init {
        permission = "admin.config.set"
        arguments += ArgumentContext("value", LongArgument)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val value = argument.getArgument<Long>("value")

        bilibiliConfig.lives.add(value)
        println("已添加")
    }
}
