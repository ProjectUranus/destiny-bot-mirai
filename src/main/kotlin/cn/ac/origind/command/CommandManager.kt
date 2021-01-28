package cn.ac.origind.command

object CommandManager {
    val commands = mutableListOf<CommandSpec>()
    val commandMap = mutableMapOf<String, CommandSpec>()

    fun register(command: CommandSpec) {
        commands += command
        commandMap[command.name] = command
    }

    fun parse(command: String, executor: CommandExecutor, context: CommandContext) {
        val parser = CommandParser(command)
        val main = parser.take()
        if (commandMap.containsKey(main)) {
            commandMap[main]?.parse(parser, executor, context)
        }
    }
}
