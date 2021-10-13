package net.origind.destinybot.features.minecraft

import com.electronwill.nightconfig.core.Config

class MinecraftConfig(config: Config) {
    val default: MinecraftServerSpec = config.get<String?>("minecraft.default").let {
        val address = MinecraftServerAddressArgument.resolveInetAddress(it)
        MinecraftServerSpec(address.hostString, address.port)
    }
    val servers: Map<String, MinecraftServerSpec> = config.get<Config>("minecraft.servers").valueMap().mapValues {
        val address = MinecraftServerAddressArgument.resolveInetAddress(it.value.toString())
        MinecraftServerSpec(address.hostString, address.port)
    }
}

data class MinecraftServerSpec(var host: String? = null, var port: Int = 25565)
