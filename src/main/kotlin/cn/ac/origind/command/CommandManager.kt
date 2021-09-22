package cn.ac.origind.command

import com.projecturanus.suffixtree.GeneralizedSuffixTree
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps

object CommandManager {
    val commands = mutableListOf<CommandSpec>()

    private val commandMap = mutableMapOf<String, CommandSpec>()
    private var commandNameCache: Map<String, CommandSpec> = emptyMap()
    private var commandIndexCache: Int2ObjectMap<CommandSpec> = Int2ObjectMaps.EMPTY_MAP as Int2ObjectMap<CommandSpec>
    private var searchTree: GeneralizedSuffixTree = GeneralizedSuffixTree()

    fun buildCache() {
        searchTree = GeneralizedSuffixTree()
        commandNameCache = commands.associateBy { it.name }
        commandIndexCache = Int2ObjectArrayMap()
        commands.forEachIndexed { index, command ->
            commandIndexCache[index] = command
            searchTree.put(command.name, index)
        }
    }

    fun register(command: CommandSpec) {
        commands += command
        commandMap[command.name] = command
    }

    fun parse(command: String, executor: CommandExecutor, context: CommandContext) {
        val parser = CommandParser(command)
        val main = parser.take()
        if (commandMap.containsKey(main)) {
            commandMap[main]?.parse(parser, executor, context)
        }
    }
}
