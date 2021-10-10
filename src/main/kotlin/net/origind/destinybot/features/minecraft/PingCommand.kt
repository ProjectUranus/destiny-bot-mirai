package net.origind.destinybot.features.minecraft

import net.origind.destinybot.core.DestinyBot
import net.origind.destinybot.core.joinToString
import com.github.steveice10.mc.protocol.MinecraftConstants
import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.data.SubProtocol
import com.github.steveice10.mc.protocol.data.message.Message
import com.github.steveice10.mc.protocol.data.message.TextMessage
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler
import com.github.steveice10.mc.protocol.data.status.handler.ServerPingTimeHandler
import com.github.steveice10.packetlib.Client
import com.github.steveice10.packetlib.tcp.TcpSessionFactory
import kotlinx.coroutines.delay
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.origind.destinybot.api.command.*
import java.net.InetSocketAddress

object PingCommand: AbstractCommand("/ping") {
    val statusProtocol = MinecraftProtocol(SubProtocol.STATUS)

    override val aliases: List<String>
        get() = listOf("ping")

    init {
        arguments += ArgumentContext("server", MinecraftServerAddressArgument, true)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        if (argument.hasArgument("server")) {
            try {
                status(executor, DestinyBot.config[MinecraftSpec.default].host!!, DestinyBot.config[MinecraftSpec.default].port)
            } catch (e: Exception) {
                if (e is NullPointerException) {
                    executor.sendMessage("未设置默认服务器。")
                    executor.sendMessage(getHelp())
                } else {
                    executor.sendMessage("连接失败: " + e.joinToString())
                }
            }
        } else {
            val server = argument.getArgument<InetSocketAddress>("server")
            try {
                status(executor, server.hostString, server.port)
            } catch (e: Exception) {
                executor.sendMessage("连接失败: " + e.joinToString())
            }
        }
    }

    fun mapMessageToRaw(message: Message): String {
        return if (message is TextMessage) message.text
        else {
            buildString { message.extra.map(::mapMessageToRaw).forEach(::append) }
        }
    }

    suspend fun status(executor: CommandExecutor, host: String, port: Int) {
        val client = Client(host, port, statusProtocol, TcpSessionFactory(null))

        var infoVar: ServerStatusInfo? = null
        var pingTimeVar: Long? = null

        client.session.setFlag(
            MinecraftConstants.SERVER_INFO_HANDLER_KEY,
            ServerInfoHandler { _, i ->
                infoVar = i
            })

        client.session.setFlag(
            MinecraftConstants.SERVER_PING_TIME_HANDLER_KEY,
            ServerPingTimeHandler { _, p ->
                pingTimeVar = p
            })

        client.session.connect(true)

        delay(1000)

        if (infoVar == null || pingTimeVar == null) {
            executor.sendMessage("请求超时，请重试。")
            return
        }

        val info = infoVar!!
        val pingTime = pingTimeVar!!

        val msg = buildString {
            appendLine(mapMessageToRaw(info.description).replace(Regex("§[\\w\\d]"), ""))
            appendLine(
                ", ${
                    info.versionInfo.versionName.replace(
                        Regex("((thermos|cauldron|craftbukkit|mcpc|kcauldron|fml),?)+"),
                        ""
                    )
                }\n"
            )
            appendLine("玩家: ${info.playerInfo.onlinePlayers} / ${info.playerInfo.maxPlayers}\n")
            if (info.playerInfo.onlinePlayers > 0) {
                appendLine(info.playerInfo.players.joinToString(", ") { it.name })
            }
        }.trim()
        if (executor is UserCommandExecutor) {
            executor.sendMessage(buildMessageChain {
                info.iconPng?.let { icon -> add(executor.user.uploadImage(icon.toExternalResource("png"))) }
                add(msg)
            })
        } else {
            executor.sendMessage(msg)
        }
        executor.sendMessage("服务器延迟为 ${pingTime}ms")
    }
}
