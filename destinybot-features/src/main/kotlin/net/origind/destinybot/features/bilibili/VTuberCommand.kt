package net.origind.destinybot.features.bilibili

import net.origind.destinybot.api.command.*

object VTuberCommand : AbstractCommand("查成分") {
    init {
        arguments += ArgumentContext("name", StringArgument)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val name = argument.getArgument<String>("name")
        val id = name.toIntOrNull()
        if (id != null) {

        } else {

        }
    }
}
