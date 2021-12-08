package net.origind.destinybot.features.destiny

import net.origind.destinybot.api.command.*
import net.origind.destinybot.features.DataStore

object MyProfileCommand: AbstractCommand("我的信息") {
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val user = DataStore[context.senderId]
        if (executor !is UserCommandExecutor) return // TODO 统一的executor类型判断

        if (user.destinyMembershipId.isEmpty()) {
            if (executor.groupContains(3320645904)) // CY BOT
                executor.sendMessage("你还没有绑定账号! 请搜索一个玩家并绑定之。")
        } else {
            PlayerProfileCommand.replyProfile(user.destinyMembershipType, user.destinyMembershipId, executor)
        }
    }

}
