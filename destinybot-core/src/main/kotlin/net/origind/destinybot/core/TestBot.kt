package net.origind.destinybot.core

import com.electronwill.nightconfig.core.file.FileConfig
import com.electronwill.nightconfig.toml.TomlFormat
import net.origind.destinybot.features.minecraft.MinecraftConfig
import java.nio.file.Paths

fun main() {
    val config = FileConfig.of(Paths.get("config.toml").toAbsolutePath(), TomlFormat.instance())
    config.load()
    println(Paths.get("config.toml").toAbsolutePath())
    println(MinecraftConfig(config))
}
