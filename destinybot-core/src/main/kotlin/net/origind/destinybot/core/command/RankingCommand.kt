package net.origind.destinybot.core.command

import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.MemberPermission
import net.origind.destinybot.api.command.*
import java.nio.charset.StandardCharsets

object RankingCommand: AbstractCommand("设置头衔") {
    init {
        arguments += ArgumentContext("qq", LongArgument, false, "QQ号")
        arguments += ArgumentContext("rank", StringArgument, false, "要设置的头衔")

        permission = "admin.ranking.set"
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val qq = argument.getArgument<Long>("qq")
        val specialTitle = argument.getArgument<String>("rank")
        if (executor is MiraiUserCommandExecutor && executor.user is Member) {
            val group = executor.user.group
            if (group.botPermission != MemberPermission.OWNER) {
                executor.sendMessage("机器人不是群主。")
                return
            }
            if (specialTitle.toByteArray(StandardCharsets.UTF_8).size > 18) {
                executor.sendMessage("请注意在 UTF-8 编码中大于 18 字节的头衔会被裁断。")
            }
            if (qq !in group) {
                executor.sendMessage("要设置的成员不在群中")
                return
            }
            group[qq]!!.specialTitle = specialTitle
            executor.sendMessage("设置成功")
        }
    }
}
