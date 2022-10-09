package net.origind.destinybot.api.command

object BooleanArgument: ArgumentType<Boolean> {
    override val clazz: Class<Boolean> = Boolean::class.java

    override fun parse(literal: String): Boolean = literal.toBooleanStrictOrNull() ?: throw ArgumentParseException("Must be true or false")
}

object StringArgument: ArgumentType<String> {
    override val clazz: Class<String> = String::class.java

    override fun parse(literal: String): String = literal
}

object IntArgument: ArgumentType<Int> {
    override val clazz: Class<Int> = Int::class.java

    override fun parse(literal: String): Int = literal.toIntOrNull() ?: throw ArgumentParseException("Not a valid int")
}

object LongArgument: ArgumentType<Long> {
    override val clazz: Class<Long> = Long::class.java

    override fun parse(literal: String): Long = literal.toLongOrNull() ?: throw ArgumentParseException("Not a valid long")
}

object QQArgument: ArgumentType<Long> {
    override val clazz: Class<Long> = Long::class.java

    override fun parse(literal: String): Long = literal.removePrefix("@").toLongOrNull() ?: throw ArgumentParseException("Not a valid qq number")
}

object DoubleArgument: ArgumentType<Double> {
    override val clazz: Class<Double> = Double::class.java

    override fun parse(literal: String): Double = literal.toDoubleOrNull() ?: throw ArgumentParseException("Not a valid double")
}
