package cn.ac.origind.command

open class ArgumentType<T>(val clazz: Class<T>, val serializer: (T) -> String, val deserializer: (String) -> T)
