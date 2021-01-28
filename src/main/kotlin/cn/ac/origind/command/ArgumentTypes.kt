package cn.ac.origind.command

object BooleanArgument: ArgumentType<Boolean>(Boolean::class.java, {take().toBoolean()})

object StringArgument: ArgumentType<String>(String::class.java, {take()})

object IntArgument: ArgumentType<Int>(Int::class.java, {take().toInt()})

object LongArgument: ArgumentType<Long>(Long::class.java, {take().toLong()})

object DoubleArgument: ArgumentType<Double>(Double::class.java, {take().toDouble()})
