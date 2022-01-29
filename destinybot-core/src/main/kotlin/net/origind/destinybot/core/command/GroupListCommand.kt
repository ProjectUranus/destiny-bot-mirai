package net.origind.destinybot.core.command

import com.squareup.moshi.Types
import net.mamoe.mirai.contact.Member
import net.origind.destinybot.api.command.*
import net.origind.destinybot.core.util.MemberData
import net.origind.destinybot.core.util.toGZIPCompressedBase64Encoded
import net.origind.destinybot.features.moshi

object GroupListCommand : AbstractCommand("/名单") {
    override val aliases: List<String> = listOf("/辛德勒的名单", "/拉清单", "/别看你今天闹得欢")

    init {
        arguments += ArgumentContext("isLong", BooleanArgument, true, "输出长格式")
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        if (executor is MiraiUserCommandExecutor && executor.user is Member) {
            val group = executor.user.group
            if (argument.hasArgument("isLong") && argument.getArgument("isLong")) {
                executor.sendMessage("本消息经过 GZIP 压缩并通过 Base64 编码，可以在 https://www.txtwizard.net/compression 解码。")
                executor.sendMessage(
                    moshi.adapter<List<MemberData>>(Types.newParameterizedType(List::class.java, MemberData::class.java))
                        .toJson(group.members.map(::MemberData).toList()).toGZIPCompressedBase64Encoded()
                )
            } else {
                executor.sendMessage("[${group.members.map { it.id }.joinToString()}]")
            }
        } else {
            executor.sendMessage("请在群聊中调用。")
        }
    }
}
