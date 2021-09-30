package net.origind.destinybot.api.command

object BooleanArgument: ArgumentType<Boolean>(Boolean::class.java, {toBooleanStrictOrNull()})

object StringArgument: ArgumentType<String>(String::class.java, {this})

object IntArgument: ArgumentType<Int>(Int::class.java, {toIntOrNull()})

object LongArgument: ArgumentType<Long>(Long::class.java, {toLongOrNull()})

object DoubleArgument: ArgumentType<Double>(Double::class.java, {toDoubleOrNull()})
