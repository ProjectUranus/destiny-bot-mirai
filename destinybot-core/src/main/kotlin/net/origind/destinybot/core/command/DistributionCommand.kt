package net.origind.destinybot.core.command

import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.MemberPermission
import net.origind.destinybot.api.command.*
import java.nio.charset.StandardCharsets

object DistributionCommand: AbstractCommand("分配头衔") {
    init {
        description = "头衔大分配"
        arguments += ArgumentContext("rank", StringArgument, false, "要设置的头衔，%1来替换index")
        arguments += ArgumentContext("shuffle", BooleanArgument, false, "是否 shuffle")

        permission = "admin.ranking.set"
    }


    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val specialTitle = argument.getArgument<String>("rank")
        val shuffle = if (argument.hasArgument("shuffle")) argument.getArgument("shuffle") else false

        if (executor is MiraiUserCommandExecutor && executor.user is Member) {
            val group = executor.user.group
            if (group.botPermission != MemberPermission.OWNER) {
                executor.sendMessage("机器人不是群主。")
                return
            }

            val iterator = if (shuffle) group.members.shuffled().withIndex() else group.members.withIndex()

            for ((i, member) in iterator) {
                if (member !in group) {
                    executor.sendMessage("要设置的成员不在群中")
                    return
                }
                val formatted = specialTitle.format(i)
                if (formatted.toByteArray(StandardCharsets.UTF_8).size > 18) {
                    executor.sendMessage("请注意在 UTF-8 编码中大于 18 字节的头衔会被裁断。")
                }
                member.specialTitle = formatted
            }
            executor.sendMessage("设置成功")
        }
    }
}
