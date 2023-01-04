package net.origind.destinybot.api.command

class ArgumentContainer(val arguments: List<ArgumentContext<*>>) {
    val argumentContextMap: Map<String, ArgumentContext<*>>
    val requiredArgumentsToParse: Int
    private val argumentMap = mutableMapOf<String, Any>()

    var deque: ArrayDeque<ArgumentContext<*>>

    val helpText: String

    init {
        if (!arguments.toSet().containsAll(arguments))
            throw IllegalArgumentException("Argument name is not unique: " + arguments.intersect(arguments.toSet()).joinToString(", ") { it.name })

        argumentContextMap = arguments.associateBy { it.name }
        deque = ArrayDeque(arguments)
        requiredArgumentsToParse = arguments.count { !it.optional }

        helpText = buildString {
            appendLine(arguments.joinToString(" ") {
                if (it.optional) "[${it.name}]" else "(${it.name})"
            })
            arguments.filter { it.description != null }.forEach {
                appendLine("${it.name}: ${it.description}")
            }
        }.trim()
    }

    fun parse(parser: CommandParser, executor: CommandExecutor, context: CommandContext) {
        argumentMap.clear()
        deque = ArrayDeque(arguments)
        var parsedRequiredArguments = 0
        var internal: String? = null
        while (deque.isNotEmpty() && (parser.hasMore() || internal != null)) {
            val arg = deque.first()
            if (!arg.optional) {
                if (internal != null) {
                    argumentMap[arg.name] =
                        arg.type.parse(internal, executor, context) ?: throw ArgumentParseException("无法解析 ${arg.name} 参数")
                    internal = null
                } else {
                    argumentMap[arg.name] =
                        arg.type.parse(parser.take(), executor, context) ?: throw ArgumentParseException("无法解析 ${arg.name} 参数")
                }
                parsedRequiredArguments++
            } else {
                if (internal != null) {
                    val parsed = arg.type.parse(internal, executor, context)
                    if (parsed != null) {
                        argumentMap[arg.name] = parsed
                        internal = null
                    }
                } else {
                    val str = parser.take()
                    val parsed = arg.type.parse(str, executor, context)
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
            validate()
        }
    }

    fun validate() {
        val invalidArguments = mutableListOf<ArgumentContext<*>>()

        for (argument in arguments) {
            if (!(argumentMap.containsKey(argument.name) || argument.optional)) {
                invalidArguments += argument
            }
        }

        if (invalidArguments.isNotEmpty()) {
            throw ArgumentParseException("缺失必填参数 " + invalidArguments.joinToString { it.name })
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getArgument(name: String): T = argumentMap[name] as T

    fun hasArgument(name: String) = argumentMap.containsKey(name)
}
