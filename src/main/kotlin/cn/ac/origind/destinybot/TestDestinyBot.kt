package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.image.getImage
import cn.ac.origind.minecraft.MinecraftClientLogin
import com.github.steveice10.mc.protocol.MinecraftConstants
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler
import com.github.steveice10.mc.protocol.data.status.handler.ServerPingTimeHandler
import com.github.steveice10.packetlib.Client
import com.github.steveice10.packetlib.tcp.TcpSessionFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.toExternalImage
import net.mamoe.mirai.utils.upload
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.File
import java.net.Proxy
import javax.imageio.ImageIO

val normalColor = Color(255, 255, 255)
val pveColor = Color(87, 145, 190)
val pvpColor = Color(245, 91, 91)
val godColor = Color(227, 202, 87)

fun main(args: Array<String>) {
    runBlocking {
        val client = Client("home.misaka.chat", 25565, MinecraftClientLogin.statusProtocol, TcpSessionFactory(null))
        client.session.setFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY)
        client.session.setFlag(
            MinecraftConstants.SERVER_INFO_HANDLER_KEY,
            ServerInfoHandler { session, info ->
                println(buildString {
                    appendln(info.description.fullText.replace(Regex("§[\\w\\d]"), ""))
                    appendln(", ${info.versionInfo.versionName}\n")
                    appendln("玩家: ${info.playerInfo.onlinePlayers} / ${info.playerInfo.maxPlayers}\n")
                        if (info.playerInfo.onlinePlayers > 0) {
                            appendln(info.playerInfo.players.joinToString(", ") { it.name })
                        }
                    }
                    )
                }
        )

        client.session.setFlag(
            MinecraftConstants.SERVER_PING_TIME_HANDLER_KEY,
            ServerPingTimeHandler { session, pingTime ->
                println("服务器延迟为 ${pingTime}ms") })

        client.session.connect()

        delay(5000)
    }
}
