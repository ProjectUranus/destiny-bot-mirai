package cn.ac.origind.command

object BooleanArgument: ArgumentType<Boolean>(Boolean::class.java, {it.toString()}, {it.toBoolean()})

object StringArgument: ArgumentType<String>(String::class.java, {it}, {it})

object DoubleArgument: ArgumentType<Double>(Double::class.java, {it.toString()}, {it.toDouble()})
