package cn.ac.origind.minecraft

import cn.ac.origind.destinybot.DestinyBot.config
import cn.ac.origind.destinybot.exception.joinToString
import com.github.steveice10.mc.protocol.MinecraftConstants
import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.data.SubProtocol
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler
import com.github.steveice10.mc.protocol.data.status.handler.ServerPingTimeHandler
import com.github.steveice10.packetlib.Client
import com.github.steveice10.packetlib.tcp.TcpSessionFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.sendMessage
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.upload
import java.net.Proxy


object MinecraftClientLogin {
    val statusProtocol = MinecraftProtocol(SubProtocol.STATUS)

    suspend fun statusAsync(contact: Contact, host: String = config[MinecraftSpec.default].host!!, port: Int = config[MinecraftSpec.default].port) = withContext(Dispatchers.Default) {
        try {
            status(contact, host, port)
        } catch (e: Exception) {
            contact.sendMessage("连接失败: " + e.joinToString())
        }
    }

    fun status(contact: Contact, host: String, port: Int) {
        val client = Client(host, port, statusProtocol, TcpSessionFactory(null))
        client.session.setFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY)
        client.session.setFlag(MinecraftConstants.SERVER_INFO_HANDLER_KEY,
            ServerInfoHandler { session, info ->
                contact.launch {
                    contact.sendMessage(
                        buildMessageChain {
                            info.icon?.let { icon -> add(icon.upload(contact)) }
                            add(info.description.fullText.replace(Regex("§[\\w\\d]"), ""))
                            add(", ${info.versionInfo.versionName}\n")
                            add("玩家: ${info.playerInfo.onlinePlayers} / ${info.playerInfo.maxPlayers}\n")
                            if (info.playerInfo.onlinePlayers > 0) {
                                add(info.playerInfo.players.joinToString(", ") { it.name })
                            }
                        }
                    )
                }
            })

        client.session.setFlag(MinecraftConstants.SERVER_PING_TIME_HANDLER_KEY,
            ServerPingTimeHandler { session, pingTime ->
                contact.launch { contact.sendMessage("服务器延迟为 ${pingTime}ms") } })

        client.session.connect()
    }
}