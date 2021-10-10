package net.origind.destinybot.api.command

class CommandSpec: Command {
    override var name: String = ""

    val subcommandMap = mutableMapOf<String, CommandSpec>()
    override var permission: String = "destinybot.$name"

    override val arguments = mutableListOf<ArgumentContext<*>>()

    val container by lazy { ArgumentContainer(arguments) }
    var executor: (ArgumentContainer, CommandExecutor, CommandContext) -> Unit = { _, _, _ -> }

    override val aliases: List<String> = emptyList()
    override val examples: List<String> = emptyList()

    override var description: String? = null

    override val argumentContainer: ArgumentContainer by lazy { ArgumentContainer(arguments) }

    override fun getSubcommand(name: String): Command? =
        subcommandMap[name]

    override fun getSubcommands(): Collection<Command> =
        subcommandMap.values

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        executor(argument, executor, context)
    }

    fun registerSubcommand(command: CommandSpec) {
        subcommandMap[command.name] = command
    }

    fun subcommand(init1: CommandSpec.() -> Unit): CommandSpec {
        val spec = CommandSpec()
        spec.init1()
        registerSubcommand(spec)
        return spec
    }

    fun argument(name: String, type: ArgumentType<*>, description: String? = null, optional: Boolean = false) {
        arguments += ArgumentContext(name, type, optional, description)
    }
}

fun command(init1: CommandSpec.() -> Unit): CommandSpec {
    val spec = CommandSpec()
    spec.init1()

    CommandManager.register(spec)
    return spec
}
