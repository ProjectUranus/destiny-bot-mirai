package net.origind.destinybot.api.command

open class ArgumentType<T>(val clazz: Class<T>, val parse: String.() -> T?)
