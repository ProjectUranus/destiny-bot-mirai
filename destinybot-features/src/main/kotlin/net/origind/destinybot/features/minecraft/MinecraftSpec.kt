package net.origind.destinybot.features.minecraft

import com.electronwill.nightconfig.core.Config

class MinecraftConfig(config: Config) {
	val default: MinecraftServerSpec = config.get<String?>("minecraft.default").let {
		if(it == null) {
			MinecraftServerSpec(null)
		} else {
			val address = MinecraftServerAddressArgument.resolveInetAddress(it)
			MinecraftServerSpec(address.hostString, address.port)
		}
	}
	val servers: Map<String, MinecraftServerSpec> =
		(config.get<Config>("minecraft.servers")?.valueMap() ?: emptyMap<String, Any>())
			.map {
				val address = MinecraftServerAddressArgument.resolveInetAddress(it.value.toString())
				it.key.lowercase() to MinecraftServerSpec(address.hostString, address.port)
			}.toMap()
	val ignoreCase: Boolean = config.getOrElse("minecraft.ignoreCase", true)
	val timeoutMillis: Long = config.getLongOrElse("minecraft.timeout", 3000)
}

data class MinecraftServerSpec(var host: String? = null, var port: Int = 25565)
