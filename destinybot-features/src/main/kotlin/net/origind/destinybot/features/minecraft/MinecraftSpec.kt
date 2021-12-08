package net.origind.destinybot.features.minecraft

import com.electronwill.nightconfig.core.Config

class MinecraftConfig(config: Config) {
    val default: MinecraftServerSpec = config.get<String?>("minecraft.default").let {
        val address = MinecraftServerAddressArgument.resolveInetAddress(it)
        MinecraftServerSpec(address.hostString, address.port)
    }
    val servers: Map<String, MinecraftServerSpec> = config.get<Config>("minecraft.servers").valueMap().map {
        val address = MinecraftServerAddressArgument.resolveInetAddress(it.value.toString())
        it.key.lowercase() to MinecraftServerSpec(address.hostString, address.port)
    }.toMap()
    val ignoreCase: Boolean = config.getOrElse("minecraft.ignoreCase", true)
}

data class MinecraftServerSpec(var host: String? = null, var port: Int = 25565)
