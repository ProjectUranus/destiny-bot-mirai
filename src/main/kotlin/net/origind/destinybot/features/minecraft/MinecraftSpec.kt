package net.origind.destinybot.features.minecraft

import com.uchuhimo.konf.ConfigSpec

object MinecraftSpec : ConfigSpec() {
    val default by required<MinecraftServerSpec>()
    val servers by required<Map<String, MinecraftServerSpec>>()
}

data class MinecraftServerSpec(var host: String? = null, var port: Int = 25565)
