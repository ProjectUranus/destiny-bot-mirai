package net.origind.destinybot.core.command

import net.origind.destinybot.api.command.*
import net.origind.destinybot.core.DestinyBot

object ConfigCommand: AbstractCommand("/config") {
    init {
        permission = "admin.config"
        registerSubcommand(GetCommand)
        registerSubcommand(SetCommand)
        registerSubcommand(DeleteCommand)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        executor.sendMessage("用法: /config get [node]\n/config set (node) (value)\n/config delete (node)")
    }

    object GetCommand : AbstractCommand("get") {
        init {
            permission = "admin.config.get"
            arguments += ArgumentContext("node", StringArgument, true)
        }

        override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
            val node = argument.getArgument("node") ?: ""
            if (DestinyBot.config.contains(node))
                executor.sendMessage(DestinyBot.config.get<Any>(node).toString())
            else
                executor.sendMessage("配置中不存在 $node 节点")
        }

    }

    object SetCommand : AbstractCommand("set") {
        init {
            permission = "admin.config.set"
            arguments += ArgumentContext("node", StringArgument)
            arguments += ArgumentContext("value", StringArgument)
        }

        override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
            val node = argument.getArgument<String>("node")
            val value = argument.getArgument<String>("value")

            if (DestinyBot.config.contains(node)) {
                val current = DestinyBot.config.get<Any?>(node)

                if (current.javaClass != String::class.java) {
                    executor.sendMessage("暂不支持设定 ${current.javaClass.name} 类型的节点")
                    return
                } else {
                    executor.sendMessage("原内容为 $current")
                }
            }
            DestinyBot.config.set<String>(node, value)
            DestinyBot.config.save()
            DestinyBot.reloadConfig()
            executor.sendMessage("已将 $node 设置为 $value")
        }
    }

    object DeleteCommand : AbstractCommand("delete") {
        init {
            permission = "admin.config.remove"
            arguments += ArgumentContext("node", StringArgument)
        }

        override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
            val node = argument.getArgument<String>("node")

            DestinyBot.config.remove<Any?>(node)
            DestinyBot.config.save()
            DestinyBot.reloadConfig()
            executor.sendMessage("已移除 $node")
        }
    }
}
