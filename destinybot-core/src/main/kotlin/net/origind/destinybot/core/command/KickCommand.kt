package net.origind.destinybot.core.command

import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.getMemberOrFail
import net.origind.destinybot.api.command.*

object KickCommand : AbstractCommand("/kick") {
    init {
        permission = "op.kick"
        arguments += ArgumentContext("id", LongArgument)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        if (executor is MiraiUserCommandExecutor && executor.user is Member) {
            val id: Long = argument.getArgument("id")
            executor.user.group.getMemberOrFail(id).kick("")
            executor.sendMessage("已将 $id 移出本群")
        }
    }
}
