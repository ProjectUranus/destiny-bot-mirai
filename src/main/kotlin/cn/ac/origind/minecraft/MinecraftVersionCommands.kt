package cn.ac.origind.minecraft

import cn.ac.origind.destinybot.DestinyBot.config
import cn.ac.origind.destinybot.mapper
import cn.ac.origind.destinybot.reply
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.event.MessageEventSubscribersBuilder
import java.io.File
import java.time.*

data class LatestManifest(var release: String? = null, var snapshot: String? = null)
data class Version(var id: String? = null, var type: String? = null, var url: String? = null, var time: String? = null, var releaseTime: String? = null)

data class MinecraftVersionManifest(var latest: LatestManifest? = null, var versions: List<Version>? = null)

lateinit var versionManifest: MinecraftVersionManifest
lateinit var versionMap: Map<String, Version>

suspend fun initMinecraftVersion() {
    versionManifest = withContext(Dispatchers.IO) { mapper.readValue(File("version_manifest.json"), MinecraftVersionManifest::class.java) }
    versionMap = versionManifest.versions?.map { "/" + it.id!!.replace(".", "") to it }?.toMap() ?: emptyMap()
}

fun MessageEventSubscribersBuilder.minecraftCommands() {
    case("/ping") {
        MinecraftClientLogin.statusAsync(subject)
    }
    startsWith("/ping ") {
        val address = it
        if (config[MinecraftSpec.servers].containsKey(it)) {
            val spec = config[MinecraftSpec.servers][it.removePrefix("/ping ")]
            MinecraftClientLogin.statusAsync(subject, spec!!.host!!, spec.port)
            return@startsWith
        }
        if (address.startsWith("192.") || address.startsWith("127.")) {
            reply("老子用 LOIC 把你妈的内网 ping 了，再往里面塞几个超长握手包让你妈的服务器彻底暴毙")
            return@startsWith
        }
        if (address.contains(':')) {
            try {
                MinecraftClientLogin.statusAsync(subject, address.substringBefore(':'), Integer.parseInt(address.substringAfter(':')))
            } catch (e: NumberFormatException) {
                reply(e.localizedMessage)
            }
        } else {
            MinecraftClientLogin.statusAsync(subject, address)
        }
    }
    case("/release") {
        val version = versionMap["/" + versionManifest.latest?.release?.replace(".", "")]
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
    case("/latest") {
        val version = versionMap["/" + versionManifest.latest?.snapshot?.replace(".", "")]
        val now = LocalDateTime.now()
        val duration = Duration.between(Instant.parse(version?.releaseTime), Instant.now())
        val period = Period.between(ZonedDateTime.parse(version?.releaseTime).toLocalDate(), now.toLocalDate())

        reply(buildString {
            if (versionManifest.latest?.snapshot == versionManifest.latest?.release)
                append("最新版本 Minecraft ${version?.id} 已经发布 ")
            else
                append("最新预览版 Minecraft ${version?.id} 已经发布 ")
            if (period.years > 0) append("${period.years} 年 ")
            if (period.months > 0) append("${period.months} 月 ")
            append("${period.days} 天 ${duration.toHoursPart()} 小时 ${duration.toMinutesPart()} 分钟 ${duration.toSecondsPart()} 秒 ${duration.toMillisPart()} 毫秒了, ")
            append("你怎么还不去玩 Minecraft ${version?.id}?")
        })
    }
    content { versionMap.containsKey(it) }.reply {
        val version = versionMap[it]
        val now = LocalDateTime.now()
        val duration = Duration.between(Instant.parse(version?.releaseTime), Instant.now())
        val period = Period.between(ZonedDateTime.parse(version?.releaseTime).toLocalDate(), now.toLocalDate())

        buildString {
            append("Minecraft ${version?.id} 已经发布 ")
            if (period.years > 0) append("${period.years} 年 ")
            if (period.months > 0) append("${period.months} 月 ")
            append("${period.days} 天 ${duration.toHoursPart()} 小时 ${duration.toMinutesPart()} 分钟 ${duration.toSecondsPart()} 秒 ${duration.toMillisPart()} 毫秒了, ")
            if (period.years > 1) {
                append("你为什么还在玩 Minecraft ${version?.id}?")
            } else {
                append("你怎么还不去玩 Minecraft ${version?.id}?")
            }
        }
    }
}
