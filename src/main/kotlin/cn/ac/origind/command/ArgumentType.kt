package cn.ac.origind.command

open class ArgumentType<T>(val clazz: Class<T>, val parse: CommandParser.() -> T)
