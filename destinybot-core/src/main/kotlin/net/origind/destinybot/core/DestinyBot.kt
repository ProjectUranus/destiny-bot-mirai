package net.origind.destinybot.core

import com.electronwill.nightconfig.core.file.FileConfig
import com.electronwill.nightconfig.toml.TomlFormat
import it.unimi.dsi.fastutil.longs.LongArrayList
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.utils.BotConfiguration
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.plugin.Plugin
import net.origind.destinybot.api.timer.TimedTask
import net.origind.destinybot.api.timer.TimerManager
import net.origind.destinybot.core.command.*
import net.origind.destinybot.core.task.checkStreamer
import net.origind.destinybot.core.util.getOrThrow
import net.origind.destinybot.features.DataStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.GraphicsEnvironment
import java.io.Closeable
import java.nio.file.Paths
import java.time.Duration
import java.util.*
import kotlin.system.exitProcess

object DestinyBot : Closeable {
    val bot: Bot
    val logger: Logger
    val config: FileConfig
    var plugins: List<Plugin> = emptyList()
    var ops: LongArrayList = LongArrayList()

    init {
        System.setProperty("java.awt.headless", "true")
        println("Headless mode: " + GraphicsEnvironment.isHeadless())

        logger = LoggerFactory.getLogger("DestinyBot")

        config = FileConfig.builder(Paths.get("config.toml"), TomlFormat.instance()).concurrent().autosave().build()

        config.load()
        ops = LongArrayList(config.getOrElse<List<Number>>("bot.ops", listOf()).map { it.toLong() })

        bot = BotFactory.newBot(
			config.getOrThrow("account.qq") { IllegalArgumentException("配置中未设置账号") },
			config.getOrThrow<String>("account.password") { IllegalArgumentException("配置中未设置密码") }
		) {
            fileBasedDeviceInfo()
            protocol = BotConfiguration.MiraiProtocol.ANDROID_PAD
        }
        TimerManager // init
    }

    fun loadPlugins() {
        val loader = ServiceLoader.load(Plugin::class.java)
        plugins = loader.map { plugin ->
            plugin.init()
            plugin.registerCommand(CommandManager)
            plugin.reloadConfig(config)
            logger.info("Loaded plugin ${plugin.name}")
            plugin
        }
    }

    fun reloadConfig() {
        plugins.forEach {
            it.reloadConfig(config)
        }
    }

    suspend fun init() {
        if (!GraphicsEnvironment.isHeadless()) {
            logger.error("Headless is not set to true, check your settings!");
            exitProcess(-1);
        }
        DataStore.init()
        bot.login()
        logger.info("Logged in")
        bot.subscribeMessages()
        registerCommands()
        registerTasks()
        CommandManager.init()
        logger.info("Fetched Little Light Wishlist")

        loadPlugins()
        logger.info("Finished loading")
    }

    suspend fun start() {
        CommandManager.buildCache()
        logger.info("注册的命令: ${CommandManager.commands.joinToString { it.name }}")
        TimerManager.run()
        bot.join()
    }

    override fun close() {
        bot.close()
    }

    private fun registerCommands() {
        // Base Commands
        CommandManager.register(HelpCommand)

        CommandManager.register(AnnouncementCommand)
        CommandManager.register(ConfigCommand)
        CommandManager.register(GroupListCommand)
        CommandManager.register(RankingCommand)
        CommandManager.register(ReloadCommand)
        CommandManager.register(OpsCommand)
        CommandManager.register(OpCommand)
        CommandManager.register(DeopCommand)
        CommandManager.register(MemberJoinRequestCommand)
        CommandManager.register(KickCommand)
        CommandManager.register(AdminCommand)
    }

    private fun registerTasks() {
        TimerManager.schedule("checkstreamer", TimedTask(::checkStreamer, Duration.ofSeconds(30)))
    }

    private fun Bot.subscribeMessages() {
        eventChannel.subscribeMessages {
            always {
                val context = CommandContext(sender.id, subject.id, it, time.toLong())
                CommandManager.parse(it, MiraiUserCommandExecutor(sender), context)
            }
            case("花园世界").reply("前往罗斯128b，与你的扭曲人伙伴们一起延缓凋零的复苏。")
            case("小行星带").reply("调查新近出现的bart遗迹，查明它的装配线生成概率。")
            case("咱…").reply("咱…")
        }
        eventChannel.subscribe<MemberJoinRequestEvent> {
            MemberJoinRequestCommand.events += it
            group?.sendMessage("$fromNick ($fromId) 申请加群，/jr list后可以用/jr accept，/jr deny或/jr ignore来处理请求。")
            ListeningStatus.LISTENING
        }
    }

    @ExperimentalStdlibApi
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        init()
        start()
        close()
    }
}
