package cn.ac.origind.apex

import cn.ac.origind.destinybot.reply
import cn.ac.origind.destinybot.toLocalizedString
import net.mamoe.mirai.event.MessageEventSubscribersBuilder
import java.time.Duration

fun MessageEventSubscribersBuilder.apexCommands() {
    case("地图轮换") {
        try {
            val rotation = getMapRotation()
            reply(buildString {
                appendLine("当前大逃杀模式地图：${localizeMapName(rotation.battleRoyale.current.map)}，将在 ${Duration.ofSeconds(rotation.battleRoyale.current.remainingSecs!!).toLocalizedString()} 后切换为 ${localizeMapName(rotation.battleRoyale.next.map)}。")
                append("排名赛地图：${localizeMapName(rotation.ranked.current.map)}，下一张地图为 ${localizeMapName(rotation.ranked.next.map)}")
            })
        } catch (e: Exception) {
            reply("请求时发生了错误；${e.localizedMessage}，请稍后重试。")
        }
    }
    startsWith("apex开盒 ") {
        val name = it
        try {
            val player = searchApexPlayer(name)
            if (player.Error != null) {
                reply("查询不到 $name，当前只能查询 Origin 平台上的名称")
                return@startsWith
            }
            reply(buildString {
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
            reply("请求时发生了错误；${e.localizedMessage}，请稍后重试。")
        }
    }
}
