package net.origind.destinybot.features.bilibili

import net.origind.destinybot.api.command.*

object VTuberCommand : AbstractCommand("查成分") {
    init {
        arguments += ArgumentContext("name", StringArgument)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val name = argument.getArgument<String>("name").trim()
        val id = name.toLongOrNull()
        val info = if (id != null) {
            val i = searchUser(name).firstOrNull()
            if (i?.mid != 0L)
                i
            else
                getUserInfo(id)
        } else {
            searchUser(name).firstOrNull()
        } ?: BilibiliUser(0, "")

        if (info.mid == 0L) {
            executor.sendMessage("获取用户信息失败，请检查你的搜索关键词或mid。")
        } else {
            executor.sendMessage("${info.name()} 关注的 VUP 有：" + sameFollow(bilibiliConfig.cookie, info?.mid ?: 0).joinToString { it.name() })
        }

    }
}
