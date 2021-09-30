package net.origind.destinybot.features.apex

import net.origind.destinybot.api.command.*

object ProfileCommand: AbstractCommand("apex开盒") {
    init {
        arguments += ArgumentContext("player", StringArgument)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val name = argument.getArgument<String>("player")
        try {
            val player = searchApexPlayer(name)
            if (player.Error != null) {
                executor.sendMessage("查询不到 $name，当前只能查询 Origin 平台上的名称")
                return
            }
            executor.sendMessage(buildString {
                appendLine("玩家：${player.global.name}")
                if (player.realtime.currentState != "offline") {
                    append("，当前在线")
                }
                appendLine("ID：${player.global.uid}")
                appendLine("等级：${player.global.level}，升级进度为 ${player.global.toNextLevelPercent}%")
                appendLine("段位：${localizeRankName(player.global.rank.rankName)} ${player.global.rank.rankDiv}")
                if (player.global.battlepass.level != "-1") {
                    appendLine("通行证等级：${player.global.battlepass.level}")
                }
                append("总击杀：${player.total.kills?.value ?: 0}")
            })
        } catch (e: Exception) {
            executor.sendMessage("请求时发生了错误；${e.localizedMessage}，请稍后重试。")
        }
    }

}
