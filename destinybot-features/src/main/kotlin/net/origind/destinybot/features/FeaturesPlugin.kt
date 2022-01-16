package net.origind.destinybot.features

import com.electronwill.nightconfig.core.Config
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.origind.destinybot.api.command.CommandManager
import net.origind.destinybot.api.plugin.Plugin
import net.origind.destinybot.features.apex.MapRotationCommand
import net.origind.destinybot.features.apex.ProfileCommand
import net.origind.destinybot.features.bilibili.BilibiliConfig
import net.origind.destinybot.features.bilibili.StreamerCommand
import net.origind.destinybot.features.bilibili.bilibiliConfig
import net.origind.destinybot.features.destiny.*
import net.origind.destinybot.features.github.GitHubCommand
import net.origind.destinybot.features.injdk.InjdkCommand
import net.origind.destinybot.features.instatus.InstatusAPI
import net.origind.destinybot.features.minecraft.MinecraftConfig
import net.origind.destinybot.features.minecraft.MinecraftVersionCommand
import net.origind.destinybot.features.minecraft.PingCommand
import net.origind.destinybot.features.minecraft.minecraftConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
val logger: Logger = LoggerFactory.getLogger("DestinyBot Features")

class FeaturesPlugin : Plugin {
    override val name: String = "features"
    override val version: String = "1.0.0"

    override fun init() {
    }

    override fun reloadConfig(config: Config) {
        bilibiliConfig = BilibiliConfig(config)
        minecraftConfig = MinecraftConfig(config)
        PingCommand.reloadConfig(config)
        InstatusAPI.reloadConfig(config)
    }

    override suspend fun reload() {
        MinecraftVersionCommand.reload()
        InjdkCommand.reload()
    }

    override fun registerCommand(manager: CommandManager) {
        // Apex Commands
        manager.register(MapRotationCommand)
        manager.register(ProfileCommand)

        // Bilibili Commands
        manager.register(StreamerCommand)

        // Destiny Commands
        manager.register(ActivityCommand)
        manager.register(BindAccountCommand)
        manager.register(LoreCommand)
        manager.register(MyProfileCommand)
        manager.register(PerkCommand)
        manager.register(PlayerProfileCommand)
        manager.register(QueryLinkedCredentialCommand)
        manager.register(SearchChooseResultCommand)
        manager.register(SearchCommand)
        manager.register(TrackerCommand)
        manager.register(WeeklyReportCommand)

        manager.register(InjdkCommand)

        // GitHub Commands
        manager.register(GitHubCommand)

        // Minecraft Commands
        manager.register(PingCommand)
        manager.register(MinecraftVersionCommand)
    }
}
