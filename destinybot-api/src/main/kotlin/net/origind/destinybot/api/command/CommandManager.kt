package net.origind.destinybot.api.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface CommandManager : CoroutineScope {
    val commands: List<Command>
    val helpText: String

    fun init(): Job

    fun buildCache()

    fun register(command: Command)

    fun parse(command: String, executor: CommandExecutor, context: CommandContext)
}
