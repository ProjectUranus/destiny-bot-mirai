package net.origind.destinybot.core.command

import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.getMemberOrFail
import net.origind.destinybot.api.command.*

object AdminCommand : AbstractCommand("/admin") {
    init {
        permission = "op.admin"
        registerSubcommand(Enable)
        registerSubcommand(Disable)
    }

    object Enable : AbstractCommand("enable") {
        init {
            permission = "op.admin.enable"
            arguments += ArgumentContext("id", LongArgument)
        }

        override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
            val id: Long = argument.getArgument("id")
            ((executor as MiraiUserCommandExecutor).user as Member).group.getMemberOrFail(id).modifyAdmin(true)
            executor.sendMessage("已将 $id 设为管理员")
        }
    }

    object Disable : AbstractCommand("disable") {
        init {
            permission = "op.admin.disable"
            arguments += ArgumentContext("id", LongArgument)
        }

        override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
            val id: Long = argument.getArgument("id")
            ((executor as MiraiUserCommandExecutor).user as Member).group.getMemberOrFail(argument.getArgument("id")).modifyAdmin(false)
            executor.sendMessage("已移除 $id 的管理员")
        }
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        executor.sendMessage(getHelp())
    }
}
