package net.origind.destinybot.features.destiny

import io.ktor.client.features.*
import net.origind.destinybot.api.command.*
import net.origind.destinybot.features.DataStore

object BindAccountCommand : AbstractCommand("绑定") {
    init {
        arguments += ArgumentContext("id", QQArgument)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val id = argument.getArgument<Long>("id")
        if (profileQuerys[context.senderId]?.get(id.toInt() - 1) == null) {

            // 直接绑定 ID
            if (id < 10000000) executor.sendMessage("你输入的命运2 ID是不是稍微短了点？")
            else {
                val destinyMembership = if (id.toString().startsWith("7656")) {
                    getMembershipFromHardLinkedCredential(id.toString())
                } else {
                    getProfile(3, id.toString())?.profile?.data?.userInfo
                }

                if (destinyMembership == null) executor.sendMessage("无法找到该玩家，检查一下？")
                else {
                    DataStore[context.senderId].apply {
                        destinyMembershipId = destinyMembership.membershipId
                        destinyMembershipType = destinyMembership.membershipType
                        destinyDisplayName = destinyMembership.displayName
                    }
                    DataStore.save()
                    executor.sendMessage("绑定 ${destinyMembership.displayName}(${destinyMembership.membershipId}) 到 ${context.senderId} 成功。")
                }
            }
        } else {
            // 绑定搜索序号
            val result = profileQuerys[context.senderId]!!
            val index = id - 1
            if (result.size < index + 1) executor.sendMessage("你的序号太大了点。")
            val destinyMembership = result[index.toInt()]
            try {
                DataStore[context.senderId].apply {
                    destinyMembershipId = destinyMembership.membershipId
                    destinyMembershipType = destinyMembership.membershipType
                    destinyDisplayName = destinyMembership.displayName
                }
                DataStore.save()
                executor.sendMessage("绑定 ${destinyMembership.displayName}(${destinyMembership.membershipId}) 到 ${context.senderId} 成功。")
            } catch (e: ServerResponseException) {
                executor.sendMessage("获取详细信息时失败，请重试。\n${e.localizedMessage}")
            }
        }
    }
}
