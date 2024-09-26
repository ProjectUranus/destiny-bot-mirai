package net.origind.destinybot.api.command

interface Command {
    /**
     * 命令的名称
     */
    val name: String

    /**
     * 命令权限
     */
    val permission: String

    /**
     * 命令描述
     * TODO i18n
     */
    val description: String?

    /**
     * 启用默认帮助（/xxx help)
     */
    val helpEnabled: Boolean
        get() = true

    val arguments: List<ArgumentContext<*>>

    val argumentContainer: ArgumentContainer
        get() = ArgumentContainer(arguments)

    /**
     * 别名
     */
    val aliases: List<String> get() = emptyList()

    /**
     * 用例
     */
    val examples: List<String> get() = emptyList()

    /**
     * 用来代替 Lazy
     */
    suspend fun init() {}

    /**
     * 获取一个子命令
     */
    fun getSubcommand(name: String): Command?

    fun getSubcommands(): Collection<Command>

    /**
     * 是否存在子命令
     * 若为空则永远为真
     */
    fun hasSubcommand(name: String): Boolean =
        name.isEmpty()

    /**
     * 解析命令并执行
     */
    suspend fun parse(parser: CommandParser, executor: CommandExecutor, context: CommandContext) {
        try {
            if (parser.hasMore()) {
                val sub = parser.take(false)
                if ((sub == "help" || sub == "?") && helpEnabled) {
                    executor.sendMessage(getHelp())
                } else if (hasSubcommand(sub)) {
                    parser.take()
                    getSubcommand(sub)?.parse(parser, executor, context)
                } else {
                    argumentContainer.parse(parser, executor, context)
                    if (executor.hasPermission(permission))
                        execute(argumentContainer, executor, context)
                    else
                        executor.sendMessage("你无权执行该命令")
                }
            } else {
                argumentContainer.parse(parser, executor, context)
                if (executor.hasPermission(permission))
                    execute(argumentContainer, executor, context)
                else
                    executor.sendMessage("你无权执行该命令")
            }
        } catch (e: ArgumentParseException) {
            executor.sendMessage("命令参数解析错误: ${e.localizedMessage}")
        }
    }

    /**
     * 执行命令
     * @param argument 解析过的参数
     * @param executor 命令执行者
     * @param context 命令上下文
     */
    suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext)

    /**
     * 帮助
     */
    fun getHelp(): String = buildString {
        append(name)
        if (description != null)
            append(" - ").appendLine(description)
        else
            appendLine()

        if (aliases.isNotEmpty())
            appendLine("别名: [${aliases.joinToString()}]")

        getSubcommands().forEach { command ->
            appendLine("子命令 ${command.name}:")
            appendLine(command.getHelp())
        }

        if (arguments.isNotEmpty()) {
            appendLine("参数: $name " + argumentContainer.helpText)
        } else {
            appendLine("该命令没有任何参数。")
        }

        examples.forEach(this::appendLine)
    }.trim()
}
