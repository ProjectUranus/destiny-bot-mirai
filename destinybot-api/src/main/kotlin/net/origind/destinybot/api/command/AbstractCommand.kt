package net.origind.destinybot.api.command

abstract class AbstractCommand(final override val name: String): Command {
    val subcommandMap = mutableMapOf<String, Command>()

    override var permission: String = "destinybot.$name"

    override val arguments = mutableListOf<ArgumentContext<*>>()

    val container by lazy { ArgumentContainer(arguments) }

    override val aliases: List<String> = emptyList()
    override val examples: List<String> = emptyList()

    override var description: String? = null

    override val argumentContainer: ArgumentContainer by lazy { ArgumentContainer(arguments) }

    override fun getSubcommand(name: String): Command? =
        subcommandMap[name]

    override fun getSubcommands(): Collection<Command> =
        subcommandMap.values

    fun registerSubcommand(command: Command) {
        subcommandMap[command.name] = command
    }
}
