package cn.ac.origind.minecraft

import cn.ac.origind.destinybot.lightggGson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.event.MessagePacketSubscribersBuilder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.time.*

data class LatestManifest(var release: String? = null, var snapshot: String? = null)
data class Version(var id: String? = null, var type: String? = null, var url: String? = null, var time: String? = null, var releaseTime: String? = null)

data class MinecraftVersionManifest(var latest: LatestManifest? = null, var versions: List<Version>? = null)


lateinit var versionManifest: MinecraftVersionManifest
lateinit var versionMap: Map<String, Version>

suspend fun initMinecraftVersion() {
    versionManifest = lightggGson.fromJson(withContext(Dispatchers.IO) { Files.readString(Paths.get("version_manifest.json"), StandardCharsets.UTF_8) }, MinecraftVersionManifest::class.java)
    versionMap = versionManifest.versions?.map { "/" + it.id!!.replace(".", "") to it }?.toMap() ?: emptyMap()
}

fun MessagePacketSubscribersBuilder.minecraftCommands() {
    case("/release") {
        val version = versionMap["/" + versionManifest.latest?.release?.replace(".", "")]
        val now = LocalDateTime.now()
        val duration = Duration.between(Instant.parse(version?.releaseTime), Instant.now())
        val period = Period.between(ZonedDateTime.parse(version?.releaseTime).toLocalDate(), now.toLocalDate())

        reply(buildString {
            append("最新预览版 Minecraft ${version?.id} 已经发布 ")
            if (period.years > 0) append("${period.years} 年 ")
            if (period.months > 0) append("${period.months} 月 ")
            append("${period.days} 天 ${duration.toHoursPart()} 小时 ${duration.toMinutesPart()} 分钟 ${duration.toSecondsPart()} 秒 ${duration.toMillisPart()} 毫秒了, ")
            append("你怎么还不去玩 Minecraft ${version?.id}?")
        })
    }
    case("/latest") {
        val version = versionMap["/" + versionManifest.latest?.snapshot?.replace(".", "")]
        val now = LocalDateTime.now()
        val duration = Duration.between(Instant.parse(version?.releaseTime), Instant.now())
        val period = Period.between(ZonedDateTime.parse(version?.releaseTime).toLocalDate(), now.toLocalDate())

        reply(buildString {
            append("最新版本 Minecraft ${version?.id} 已经发布 ")
            if (period.years > 0) append("${period.years} 年 ")
            if (period.months > 0) append("${period.months} 月 ")
            append("${period.days} 天 ${duration.toHoursPart()} 小时 ${duration.toMinutesPart()} 分钟 ${duration.toSecondsPart()} 秒 ${duration.toMillisPart()} 毫秒了, ")
            append("你怎么还不去玩 Minecraft ${version?.id}?")
        })
    }
    content({ versionMap.containsKey(it) }) {
        val version = versionMap[it]
        val now = LocalDateTime.now()
        val duration = Duration.between(Instant.parse(version?.releaseTime), Instant.now())
        val period = Period.between(ZonedDateTime.parse(version?.releaseTime).toLocalDate(), now.toLocalDate())

        reply(buildString {
            append("Minecraft ${version?.id} 已经发布 ")
            if (period.years > 0) append("${period.years} 年 ")
            if (period.months > 0) append("${period.months} 月 ")
            append("${period.days} 天 ${duration.toHoursPart()} 小时 ${duration.toMinutesPart()} 分钟 ${duration.toSecondsPart()} 秒 ${duration.toMillisPart()} 毫秒了, ")
            if (period.years > 1) {
                append("你为什么还在玩 Minecraft ${version?.id}?")
            } else {
                append("你怎么还不去玩 Minecraft ${version?.id}?")
            }
        })
    }
}