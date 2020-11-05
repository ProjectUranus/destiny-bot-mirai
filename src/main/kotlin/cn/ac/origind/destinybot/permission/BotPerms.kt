package cn.ac.origind.destinybot.permission

import cn.ac.origind.destinybot.user.User
import me.lucko.luckperms.common.verbose.event.PermissionCheckEvent
import net.luckperms.api.util.Tristate

object BotPerms {

    fun check(sender: User, node: String): Tristate {
        val user = BotPlugin.userManager.getOrMake(sender.uuid())
        val queryOptions = BotPlugin.contextManager.getQueryOptions(sender)
        return user.cachedData.getPermissionData(queryOptions).checkPermission(
            node,
            PermissionCheckEvent.Origin.PLATFORM_PERMISSION_CHECK
        ).result()
    }
}
