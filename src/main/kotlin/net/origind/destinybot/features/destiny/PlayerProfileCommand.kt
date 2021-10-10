package net.origind.destinybot.features.destiny

import net.origind.destinybot.features.destiny.image.toImage
import net.origind.destinybot.core.upload
import io.ktor.client.features.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.message.data.buildMessageChain
import net.origind.destinybot.api.command.*

object PlayerProfileCommand : AbstractCommand("/j") {
    init {
        arguments += ArgumentContext("steamid", StringArgument)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val id = argument.getArgument<String>("steamid")
        val query = getMembershipFromHardLinkedCredential(id)
        if (query == null) {
            executor.sendMessage("没有找到用户，请检查你的输入。")
            return
        }
        replyProfile(query.membershipType, query.membershipId, executor)
    }

    suspend fun replyProfile(membershipType: Int, membershipId: String, executor: CommandExecutor) {
        try {
            executor.sendMessage(buildString {
                appendLine("Tracker: https://destinytracker.com/destiny-2/profile/steam/${membershipId}/overview")
                appendLine("Braytech: https://braytech.org/3/${membershipId}")
                append("Raid 报告: https://raid.report/pc/${membershipId}")
            })
            val profile = withContext(Dispatchers.IO) {
                getProfile(3, membershipId)
            }
            if (profile == null)
                executor.sendMessage("获取详细信息时失败，请重试。")
            val userProfile = profile?.profile?.data?.userInfo
            val description = buildString {
                appendLine("玩家: ${userProfile?.displayName}")
                appendLine("ID: ${userProfile?.membershipId}")
            }
            if (executor is UserCommandExecutor)
                executor.sendMessage(buildMessageChain {
                    add(description)
                    add(profile?.characters?.data?.map { (id, character) ->
                        character
                    }?.toImage()?.upload(executor.user)!!)
                })
            else
                executor.sendMessage(description)
        } catch (e: ServerResponseException) {
            executor.sendMessage("获取详细信息时失败，请重试。\n${e.localizedMessage}")
        }
    }
}