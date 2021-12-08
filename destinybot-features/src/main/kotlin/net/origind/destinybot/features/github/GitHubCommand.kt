package net.origind.destinybot.features.github

import net.origind.destinybot.api.command.AbstractCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor

object GitHubCommand : AbstractCommand("/github") {
    init {
        registerSubcommand(GitHubCommitCommand)
    }
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        executor.sendMessage(getHelp())
    }
}
