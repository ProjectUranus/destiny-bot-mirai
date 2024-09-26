package net.origind.destinybot.api.command

interface ArgumentType<T> {
    val clazz: Class<T>

    @Deprecated("Use the parse with contexts")
    @Throws(ArgumentParseException::class)
    fun parse(literal: String): T

    @Throws(ArgumentParseException::class)
    fun parse(literal: String, executor: CommandExecutor, context: CommandContext) = parse(literal)
}
