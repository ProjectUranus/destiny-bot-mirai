package cn.ac.origind.command

open class CommandSpec(val name: String) {
    val subcommands = mutableListOf<CommandSpec>()
    val subcommandMap = mutableMapOf<String, CommandSpec>()
    var permission: String = "destinybot.$name"

    val arguments = mutableListOf<ArgumentContext<*>>()
    val container by lazy { ArgumentContainer(arguments) }
    var execute: (ArgumentContainer, CommandExecutor, CommandContext) -> Unit = { _, _, _ -> }

    var description: String? = null

    open fun parse(parser: CommandParser, executor: CommandExecutor, context: CommandContext) {
        if (parser.hasMore()) {
            val sub = parser.take(false)
            if (subcommandMap.containsKey(sub)) {
                parser.take()
                subcommandMap[sub]?.parse(parser, executor, context)
            } else {
                container.parse(parser)
                execute(container, executor, context)
            }
        } else {
            container.parse(parser)
            execute(container, executor, context)
        }
    }

    open fun getHelp() = buildString {
    }


    fun registerSubcommand(command: CommandSpec) {
        subcommands += command
        subcommandMap[command.name] = command
    }

    fun subcommand(name: String, init: CommandSpec.() -> Unit): CommandSpec {
        val spec = CommandSpec(name)
        spec.init()
        registerSubcommand(spec)
        return spec
    }

    fun argument(name: String, type: ArgumentType<*>, description: String? = null, optional: Boolean = false) {
        arguments += ArgumentContext(name, type, optional, description)
    }
}

fun command(name: String, init: CommandSpec.() -> Unit): CommandSpec {
    val spec = CommandSpec(name)
    spec.init()
    CommandManager.register(spec)
    return spec
}
