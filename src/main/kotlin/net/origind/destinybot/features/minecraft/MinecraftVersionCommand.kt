package net.origind.destinybot.features.minecraft

import net.origind.destinybot.api.command.AbstractCustomCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor
import net.origind.destinybot.core.moshi
import java.nio.file.Files
import java.nio.file.Paths
import java.time.*

object MinecraftVersionCommand: AbstractCustomCommand("mc版本") {
    var versionManifest: MinecraftVersionManifest
    var versionMap: Map<String, Version>

    init {
        versionManifest = moshi.adapter(MinecraftVersionManifest::class.java).fromJson(Files.readString(Paths.get("version_manifest.json")))!!
        versionMap = versionManifest.versions?.associate { "/" + it.id!!.replace(".", "") to it } ?: emptyMap()
    }

    private fun buildMinecraftVersionMessage(version: String, builder: StringBuilder) {
        val version = versionMap["/" + version.replace(".", "").replace("/", "")]
        val now = LocalDateTime.now()
        val duration = Duration.between(Instant.parse(version?.releaseTime), Instant.now())
        val period = Period.between(ZonedDateTime.parse(version?.releaseTime).toLocalDate(), now.toLocalDate())

        builder.apply {
            append("Minecraft ${version?.id} 已经发布 ")
            if (period.years > 0) append("${period.years} 年 ")
            if (period.months > 0) append("${period.months} 月 ")
            append("${period.days} 天 ${duration.toHoursPart()} 小时 ${duration.toMinutesPart()} 分钟 ${duration.toSecondsPart()} 秒 ${duration.toMillisPart()} 毫秒了。")
        }
    }

    override suspend fun execute(
        main: String,
        argument: ArgumentContainer,
        executor: CommandExecutor,
        context: CommandContext
    ) {
        if (versionMap.containsKey(main)) {
            val builder = StringBuilder()
            buildMinecraftVersionMessage(main, builder)
            executor.sendMessage(builder.toString())
            return
        }
        when (main) {
            "/release" -> {
                val builder = if (versionManifest.latest?.snapshot == versionManifest.latest?.release) {
                    StringBuilder("最新版本 ")
                }
                else {
                    StringBuilder("最新预览版 ")
                }
                buildMinecraftVersionMessage(
                    versionManifest.latest?.snapshot!!,
                    builder
                )
                executor.sendMessage(builder.toString())
            }
            "/latest" -> {
                val builder = StringBuilder("最新版本")
                buildMinecraftVersionMessage(
                    versionManifest.latest?.release!!,
                    builder
                )
                executor.sendMessage(builder.toString())
            }
        }
    }


}
