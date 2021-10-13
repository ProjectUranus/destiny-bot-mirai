package net.origind.destinybot.features.apex

import net.origind.destinybot.api.command.AbstractCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor
import net.origind.destinybot.api.util.toLocalizedString
import java.time.Duration

object MapRotationCommand: AbstractCommand("地图轮换") {
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        try {
            val rotation = getMapRotation()
            executor.sendMessage(buildString {
                appendLine("当前大逃杀模式地图：${localizeMapName(rotation.battleRoyale.current.map)}，将在 ${Duration.ofSeconds(rotation.battleRoyale.current.remainingSecs!!).toLocalizedString()} 后切换为 ${localizeMapName(rotation.battleRoyale.next.map)}。")
                append("排名赛地图：${localizeMapName(rotation.ranked.current.map)}，下一张地图为 ${localizeMapName(rotation.ranked.next.map)}")
            })
        } catch (e: Exception) {
            executor.sendMessage("请求时发生了错误；${e.localizedMessage}，请稍后重试。")
        }
    }
}
