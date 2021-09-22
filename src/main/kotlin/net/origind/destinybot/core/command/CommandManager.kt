package net.origind.destinybot.core.command

import com.projecturanus.suffixtree.GeneralizedSuffixTree
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import net.origind.destinybot.api.command.Command
import net.origind.destinybot.api.command.CustomCommand

class CommandManager {
    val commands: MutableList<Command> = mutableListOf()
    val customCommands: MutableList<CustomCommand> = mutableListOf()

    // Cache
    private var commandNameCache: Map<String, Command> = emptyMap()
    private var commandIndexCache: Int2ObjectMap<Command> = Int2ObjectMaps.EMPTY_MAP as Int2ObjectMap<Command>
    private var searchTree: GeneralizedSuffixTree = GeneralizedSuffixTree()

    fun registerCommand(command: Command) {
        commands += command
    }

    fun registerCustomCommand(command: CustomCommand) {
        customCommands += command
    }

    fun buildCache() {
        searchTree = GeneralizedSuffixTree()
        commandNameCache = commands.associateBy { it.baseName }
        commandIndexCache = Int2ObjectArrayMap()
        commands.forEachIndexed { index, command ->
            commandIndexCache[index] = command
            searchTree.put(command.baseName, index)
        }
    }
}
