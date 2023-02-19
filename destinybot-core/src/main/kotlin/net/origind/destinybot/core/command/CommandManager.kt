package net.origind.destinybot.core.command

import com.projecturanus.suffixtree.GeneralizedSuffixTree
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import kotlinx.coroutines.*
import net.origind.destinybot.api.command.*
import net.origind.destinybot.api.command.CommandManager
import net.origind.destinybot.api.util.joinToString
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

fun checkCommand(command: Command) {
    if (command.name.isBlank()) throw CommandException("命令没有名称")
}

object CommandManager: CoroutineScope, CommandManager {
    override val commands = mutableListOf<Command>()
    override var helpText = ""

    private val commandMap = mutableMapOf<String, Command>()
    private val customCommands = mutableListOf<CustomCommand>()
    private var commandNameCache: Map<String, Command> = emptyMap()
    private var commandIndexCache: Int2ObjectMap<Command> = Int2ObjectMaps.EMPTY_MAP as Int2ObjectMap<Command>
    private var searchTree: GeneralizedSuffixTree = GeneralizedSuffixTree()

    override fun init() =
        launch {
            for (command in commands.toList()) {
                command.init()
            }
            for (customCommand in customCommands.toList()) {
                customCommand.init()
            }
        }



    override fun buildCache() {
        searchTree = GeneralizedSuffixTree()
        commandNameCache = commands.associateBy { it.name }
        commandIndexCache = Int2ObjectArrayMap()
        var index = 0
        commands.forEach { command ->
            index++
            commandIndexCache[index] = command
            searchTree.put(command.name, index)
            command.aliases.forEach {
                index++
                commandIndexCache[index] = command
                searchTree.put(it, index)
            }
            commandNameCache = commandNameCache + command.aliases.associateWith { command }
            helpText = commands.joinToString("\n\n") { it.getHelp() }
        }
    }

    override fun register(command: Command) {
        checkCommand(command)
        if (command is CustomCommand) {
            customCommands += command
        } else {
            commands += command
            commandMap[command.name] = command
        }
    }

    override fun parse(command: String, executor: CommandExecutor, context: CommandContext) {
        val parser = CommandParser(command)
        val main = parser.take()
        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            executor.sendMessage("执行出现错误: " + throwable.joinToString())
        }
        customCommands.forEach {
            launch(handler) { withTimeout(10_000) { it.parse(main, parser, executor, context) } }
        }
        if (commandNameCache.containsKey(main)) {
            launch(handler) { withTimeout(10_000) { commandNameCache[main]?.parse(parser, executor, context) } }
        } else {
//            val top = FuzzySearch.extractTop(main, commandNameCache.keys, 1, 90)
//            if (top.isNotEmpty()) {
//                executor.sendMessage("未找到命令 $main, 您要找的是不是 ${top.first().string}(匹配度 ${top.first().score}%)")
//            }

        }
    }

    override val coroutineContext: CoroutineContext = Executors.newCachedThreadPool().asCoroutineDispatcher()
}
