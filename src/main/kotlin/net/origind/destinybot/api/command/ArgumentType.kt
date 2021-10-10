package net.origind.destinybot.api.command

interface ArgumentType<T> {
    val clazz: Class<T>

    @Throws(ArgumentParseException::class)
    fun parse(literal: String): T
}
