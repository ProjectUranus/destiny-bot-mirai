package net.origind.destinybot.core

import com.uchuhimo.konf.Config
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.utils.BotConfiguration
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandManager
import net.origind.destinybot.api.command.UserCommandExecutor
import net.origind.destinybot.core.command.HelpCommand
import net.origind.destinybot.core.command.RankingCommand
import net.origind.destinybot.core.config.AccountSpec
import net.origind.destinybot.core.config.AppSpec
import net.origind.destinybot.core.config.DictSpec
import net.origind.destinybot.core.data.DataStore
import net.origind.destinybot.features.apex.MapRotationCommand
import net.origind.destinybot.features.apex.ProfileCommand
import net.origind.destinybot.features.bilibili.BilibiliSpec
import net.origind.destinybot.features.bilibili.StreamerCommand
import net.origind.destinybot.features.destiny.*
import net.origind.destinybot.features.minecraft.MinecraftSpec
import net.origind.destinybot.features.minecraft.MinecraftVersionCommand
import net.origind.destinybot.features.minecraft.PingCommand
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File
import java.util.concurrent.TimeUnit

object DestinyBot : Closeable {
    val bot: Bot
    val logger: Logger
    val config: Config
    val client: OkHttpClient

    init {
        logger = LoggerFactory.getLogger("DestinyBot")

        config = Config {
            addSpec(AccountSpec)
            addSpec(MinecraftSpec)
            addSpec(DictSpec)
            addSpec(AppSpec)
            addSpec(BilibiliSpec)
        }.from.json.watchFile("config.json", delayTime = 15)
        bot = BotFactory.newBot(config[AccountSpec.qq], config[AccountSpec.password]) {
            fileBasedDeviceInfo()
            protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE
        }

        client = OkHttpClient.Builder()
            .cache(Cache(directory = File("web_cache"), maxSize = 10L * 1024L * 1024L))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .followSslRedirects(true)
            .callTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    suspend fun init() {
        DataStore.init()
        bot.login()
        logger.info("Logged in")
        bot.subscribeMessages()
        registerCommands()
        CommandManager.init()

        logger.info("Fetched Little Light Wishlist")
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
        // TODO Module system
        // Base Commands
        CommandManager.register(HelpCommand)
        CommandManager.register(RankingCommand)

        // Apex Commands
        CommandManager.register(MapRotationCommand)
        CommandManager.register(ProfileCommand)

        // Bilibili Commands
        CommandManager.register(StreamerCommand)

        // Destiny Commands
        CommandManager.register(ActivityCommand)
        CommandManager.register(BindAccountCommand)
        CommandManager.register(LoreCommand)
        CommandManager.register(MyProfileCommand)
        CommandManager.register(PerkCommand)
        CommandManager.register(PlayerProfileCommand)
        CommandManager.register(QueryLinkedCredentialCommand)
        CommandManager.register(SearchChooseResultCommand)
        CommandManager.register(SearchCommand)
        CommandManager.register(TrackerCommand)
        CommandManager.register(WeeklyReportCommand)

        // Minecraft Commands
        CommandManager.register(PingCommand)
        CommandManager.register(MinecraftVersionCommand)
    }

    private fun Bot.subscribeMessages() {
        eventChannel.subscribeMessages {
            always {
                val context = CommandContext(sender.id, subject.id, it, time.toLong())
                CommandManager.parse(it, UserCommandExecutor(sender), context)
            }
            case("花园世界") {
                reply("前往罗斯128b，与你的扭曲人伙伴们一起延缓凋零的复苏。")
            }
            case("小行星带") {
                reply("调查新近出现的bart遗迹，查明它的装配线生成概率。")
            }
            case("咱…") {
                reply("咱…")
            }
        }
    }
}
