package net.origind.destinybot.features.minecraft

import com.electronwill.nightconfig.core.Config
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
import com.projecturanus.suffixtree.GeneralizedSuffixTree
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
import kotlinx.coroutines.*
import net.origind.destinybot.api.command.*
import kotlin.coroutines.suspendCoroutine

lateinit var minecraftConfig: MinecraftConfig

object PingCommand: AbstractCommand("/ping") {
    var searchTree = GeneralizedSuffixTree()
    val searchTreeResultMap = Int2ObjectAVLTreeMap<String>()
    val statusProtocol = MinecraftProtocol(SubProtocol.STATUS)

    override val aliases: List<String>
        get() = listOf("ping")

    init {
        arguments += ArgumentContext("server", StringArgument, true)
    }

    fun reloadConfig(config: Config) {
        searchTreeResultMap.clear()
        searchTree = GeneralizedSuffixTree()
        minecraftConfig.servers.keys.forEachIndexed { index, s ->
            searchTree.put(s, index)
            searchTreeResultMap[index] = s
        }
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        try {
            if (argument.hasArgument("server")) {
                val server = argument.getArgument<String>("server")
                if (MinecraftServerAddressArgument.isServer(server)) {
                    val server = MinecraftServerAddressArgument.parse(server)
                    println("正在测试到服务器 $server 的延迟")
                    try {
                        status(executor, server.hostString, server.port)
                    } catch (e: Exception) {
                        executor.sendMessage("连接失败: " + e.localizedMessage)
                    }
                } else {
                    val results = searchTree.search(server)
                    if (results.isEmpty()) {
                        executor.sendMessage("未找到服务器 $server")
                    } else {
                        executor.sendMessage("未找到服务器 $server，类似的服务器有 ${results.joinToString { searchTreeResultMap[it] }}")
                    }
                }
            } else {
                try {
                    status(executor, minecraftConfig.default.host!!, minecraftConfig.default.port)
                } catch (e: Exception) {
                    if (e is NullPointerException) {
                        executor.sendMessage("未设置默认服务器。")
                        executor.sendMessage(getHelp())
                    } else {
                        executor.sendMessage("连接失败: " + e.localizedMessage)
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            executor.sendMessage("请求超时，请重试。")
        }
    }

    private fun mapMessageToRaw(message: Message): String {
        return if (message is TextMessage) message.text
        else {
            buildString { message.extra.map(::mapMessageToRaw).forEach(::append) }
        }
    }

    suspend fun status(executor: CommandExecutor, host: String, port: Int) = coroutineScope {
        val client = Client(host, port, statusProtocol, TcpSessionFactory(null))

        val infoAsync = async(Dispatchers.IO) {
            suspendCoroutine<ServerStatusInfo> {
                client.session.setFlag(
                    MinecraftConstants.SERVER_INFO_HANDLER_KEY,
                    ServerInfoHandler { _, i ->
                        it.resumeWith(Result.success(i))
                    })
            }
        }

        val pingTimeAsync = async(Dispatchers.IO) {
            suspendCoroutine<Long> {
                client.session.setFlag(
                    MinecraftConstants.SERVER_PING_TIME_HANDLER_KEY,
                    ServerPingTimeHandler { _, p ->
                        it.resumeWith(Result.success(p))
                    })
            }
        }

        client.session.connect(true)

        val (info, pingTime) = withTimeout(1000) { infoAsync.await() to pingTimeAsync.await() }

        val msg = buildString {
            appendLine("服务器延迟为 ${pingTime}ms")
            append(mapMessageToRaw(info.description).replace(Regex("§[\\w\\d]"), ""))
            appendLine(
                ", ${
                    info.versionInfo.versionName.replace(
                        Regex("((thermos|cauldron|craftbukkit|mcpc|kcauldron|fml),?)+"),
                        ""
                    )
                }"
            )
            appendLine("玩家: ${info.playerInfo.onlinePlayers} / ${info.playerInfo.maxPlayers}")
            if (info.playerInfo.onlinePlayers > 0) {
                appendLine(info.playerInfo.players.joinToString(", ") { it.name })
            }
        }.trim()

        if (executor is UserCommandExecutor) {
            info.iconPng?.let { executor.sendImage(it) }
        }
        executor.sendMessage(msg)
    }
}
