package net.origind.destinybot.core

import com.electronwill.nightconfig.core.file.FileConfig
import com.electronwill.nightconfig.toml.TomlFormat
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.utils.BotConfiguration
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.plugin.Plugin
import net.origind.destinybot.core.command.*
import net.origind.destinybot.features.DataStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.nio.file.Paths
import java.util.*

object DestinyBot : Closeable {
    val bot: Bot
    val logger: Logger
    val config: FileConfig
    var plugins: List<Plugin> = emptyList()

    init {
        logger = LoggerFactory.getLogger("DestinyBot")

        config = FileConfig.builder(Paths.get("config.toml"), TomlFormat.instance()).concurrent().autosave().build()

        config.load()

        bot = BotFactory.newBot(config.get("account.qq"), config.get<String>("account.password")) {
            fileBasedDeviceInfo()
            protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE
        }
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
        DataStore.init()
        bot.login()
        logger.info("Logged in")
        bot.subscribeMessages()
        registerCommands()
        CommandManager.init()
        logger.info("Fetched Little Light Wishlist")

        loadPlugins()
        logger.info("Finished loading")
    }

    suspend fun start() {
        CommandManager.buildCache()
        logger.info("注册的命令: ${CommandManager.commands.joinToString { it.name }}")
        bot.join()
    }

    override fun close() {
        bot.close()
    }

    private fun registerCommands() {
        // Base Commands
        CommandManager.register(HelpCommand)
        CommandManager.register(ConfigCommand)
        CommandManager.register(RankingCommand)
        CommandManager.register(ReloadCommand)
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
    }

    @ExperimentalStdlibApi
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        init()
        start()
        close()
    }
}
