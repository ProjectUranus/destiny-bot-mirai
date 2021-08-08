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

fun MessageEventSubscribersBuilder.minecraftCommands() {
    case("/ping") {
        MinecraftClientLogin.statusAsync(subject, config[MinecraftSpec.default].host!!, config[MinecraftSpec.default].port)
    }
    startsWith("/ping ") {
        val address = it
        if (config[MinecraftSpec.servers].containsKey(it)) {
            val spec = config[MinecraftSpec.servers][it]
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
            MinecraftClientLogin.statusAsync(subject, address, 25565)
        }
    }
    case("/release") {
        val builder = StringBuilder("最新版本")
        buildMinecraftVersionMessage(versionManifest.latest?.release!!, builder)
        reply(builder.toString())
    }
    case("/latest") {
        val builder = if (versionManifest.latest?.snapshot == versionManifest.latest?.release) {
            StringBuilder("最新版本 ")
        }
        else {
            StringBuilder("最新预览版 ")
        }
        buildMinecraftVersionMessage(versionManifest.latest?.snapshot!!, builder)
        reply(builder.toString())
    }
    content { versionMap.containsKey(it) }.reply {
        val builder = StringBuilder()
        buildMinecraftVersionMessage(it, builder)
        builder.toString()
    }
}
