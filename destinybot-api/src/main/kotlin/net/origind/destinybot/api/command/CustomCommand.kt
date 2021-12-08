package net.origind.destinybot.api.command

interface CustomCommand: Command {
    suspend fun parse(main: String, parser: CommandParser, executor: CommandExecutor, context: CommandContext)
}
