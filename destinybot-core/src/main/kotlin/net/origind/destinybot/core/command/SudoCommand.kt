package net.origind.destinybot.core.command

import kotlinx.coroutines.delay
import net.mamoe.mirai.contact.NormalMember
import net.mamoe.mirai.contact.isOwner
import net.origind.destinybot.api.command.AbstractCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor

object SudoCommand: AbstractCommand("/sudo") {
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        if (executor is MiraiUserCommandExecutor && executor.user is NormalMember  && executor.user.group.botPermission.isOwner()) {
            executor.user.modifyAdmin(true)
            executor.sendMessage("已将您临时设为管理员，五分钟后自动解除。")
            delay(5 * 60 * 1000L)
            executor.sendMessage("已自动解除 ${executor.user.id} 的管理员身份。")
            executor.user.modifyAdmin(false)
        } else {
            executor.sendMessage("无权执行该命令。")
        }
    }

}
