package net.origind.destinybot.api.command

abstract class AbstractCustomCommand(name: String) : AbstractCommand(name), CustomCommand {
    override suspend fun parse(
        main: String,
        parser: CommandParser,
        executor: CommandExecutor,
        context: CommandContext
    ) {
        try {
            if (parser.hasMore()) {
                val sub = parser.take(false)
                if (hasSubcommand(sub)) {
                    parser.take()
                    getSubcommand(sub)?.parse(parser, executor, context)
                } else {
                    argumentContainer.parse(parser, executor, context)
                    execute(main, argumentContainer, executor, context)
                }
            } else {
                argumentContainer.parse(parser, executor, context)
                execute(main, argumentContainer, executor, context)
            }
        } catch (e: ArgumentParseException) {
            executor.sendMessage("命令参数解析错误: ${e.localizedMessage}")
        }
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {}

    abstract suspend fun execute(main: String, argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext)
}
