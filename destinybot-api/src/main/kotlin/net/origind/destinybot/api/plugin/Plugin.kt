package net.origind.destinybot.api.plugin

import com.electronwill.nightconfig.core.Config
import net.origind.destinybot.api.command.CommandManager

interface Plugin {
    val name: String

    fun init()

    fun reloadConfig(config: Config)

    fun registerCommand(manager: CommandManager)
}