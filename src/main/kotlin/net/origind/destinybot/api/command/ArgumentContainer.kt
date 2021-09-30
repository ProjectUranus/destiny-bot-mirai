package net.origind.destinybot.api.command

class ArgumentContainer(arguments: List<ArgumentContext<*>>) {
    val argumentContextMap: Map<String, ArgumentContext<*>>
    val requiredArgumentsToParse: Int
    private val argumentMap = mutableMapOf<String, Any>()

    val deque: ArrayDeque<ArgumentContext<*>>

    init {
        if (!arguments.toSet().containsAll(arguments))
            throw IllegalArgumentException("Argument name is not unique: " + arguments.intersect(arguments.toSet()).joinToString(", ") { it.name })

        argumentContextMap = arguments.associateBy { it.name }
        deque = ArrayDeque(arguments)
        requiredArgumentsToParse = arguments.count { !it.optional }
    }

    fun parse(parser: CommandParser) {
        var parsedRequiredArguments = 0
        var internal: String? = null
        while (deque.isNotEmpty() && (parser.hasMore() || internal != null)) {
            val arg = deque.first()
            if (!arg.optional) {
                if (internal != null) {
                    argumentMap[arg.name] =
                        arg.type.parse(internal) ?: throw ArgumentParseException("无法解析 ${arg.name} 参数")
                    internal = null
                } else {
                    argumentMap[arg.name] =
                        arg.type.parse(parser.take()) ?: throw ArgumentParseException("无法解析 ${arg.name} 参数")
                }
                parsedRequiredArguments++
            } else {
                if (internal != null) {
                    val parsed = arg.type.parse(internal)
                    if (parsed != null) {
                        argumentMap[arg.name] = parsed
                        internal = null
                    }
                } else {
                    val str = parser.take()
                    val parsed = arg.type.parse(str)
                    if (parsed != null) {
                        argumentMap[arg.name] = parsed
                    } else {
                        internal = str
                    }
                }
            }
            deque.removeFirst()
        }
        if (parsedRequiredArguments != requiredArgumentsToParse) {
            throw ArgumentParseException("命令参数不足，需要: $requiredArgumentsToParse, 实际: $parsedRequiredArguments")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getArgument(name: String): T = argumentMap[name] as T
}