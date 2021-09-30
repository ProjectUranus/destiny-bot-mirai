package net.origind.destinybot.features.destiny

import cn.ac.origind.destinybot.database.getRandomLore
import net.origind.destinybot.api.command.AbstractCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor

object StoriesCommand: AbstractCommand("传奇故事") {
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val lore = getRandomLore()
        executor.sendMessage("传奇故事：" + lore.name + '\n' + lore.lore)
    }
}
