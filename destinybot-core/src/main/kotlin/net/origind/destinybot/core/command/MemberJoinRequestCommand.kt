package net.origind.destinybot.core.command

import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.origind.destinybot.api.command.*
import java.util.*

object MemberJoinRequestCommand : AbstractCommand("/jr") {
    val events = LinkedList<MemberJoinRequestEvent>()
    init {
        registerSubcommand(Deny)
        registerSubcommand(Ignore)
        registerSubcommand(Accept)
        registerSubcommand(List)
    }

    object List : AbstractCommand("list") {
        init {
            permission = "op.jr.list"
        }

        override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
            executor.sendMessage(buildString {
                events.forEachIndexed { index, memberJoinRequestEvent ->
                    appendLine("$index. ${memberJoinRequestEvent.fromNick} (${memberJoinRequestEvent.fromId}) 申请加入群 ${memberJoinRequestEvent.groupName} (${memberJoinRequestEvent.groupId}): ${memberJoinRequestEvent.message}")
                }
            })
        }

    }

    object Deny : AbstractCommand("deny") {
        init {
            arguments += ArgumentContext("index", IntArgument)
            arguments += ArgumentContext("reason", StringArgument)
            permission = "op.jr.deny"
        }

        override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
            val event = events.getOrNull(argument.getArgument("index"))
            event?.reject(argument.getArgument("reason"))
            events.removeAt(argument.getArgument("index"))
            executor.sendMessage("已拒绝 ${event?.fromId} 的入群申请。")
        }
    }

    object Ignore : AbstractCommand("accept") {
        init {
            arguments += ArgumentContext("index", IntArgument)
            permission = "op.jr.ignore"
        }

        override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
            val event = events.getOrNull(argument.getArgument("index"))
            event?.ignore()
            events.removeAt(argument.getArgument("index"))
            executor.sendMessage("已忽略 ${event?.fromId} 的入群申请。")
        }
    }

    object Accept : AbstractCommand("accept") {
        init {
            arguments += ArgumentContext("index", IntArgument)
            permission = "op.jr.accept"
        }

        override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
            val event = events.getOrNull(argument.getArgument("index"))
            event?.accept()
            events.removeAt(argument.getArgument("index"))
            executor.sendMessage("已同意 ${event?.fromId} 的入群申请。")
        }
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        executor.sendMessage(getHelp())
    }
}
