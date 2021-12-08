package net.origind.destinybot.features.minecraft

import net.origind.destinybot.api.command.ArgumentParseException
import net.origind.destinybot.api.command.ArgumentType
import java.net.InetSocketAddress

object MinecraftServerAddressArgument : ArgumentType<InetSocketAddress> {
    override val clazz: Class<InetSocketAddress> = InetSocketAddress::class.java

    fun resolveInetAddress(address: String): InetSocketAddress {
        return if (address.contains(':')) {
            InetSocketAddress(address.substringBefore(':'), Integer.parseInt(address.substringAfter(':')))
        } else {
            val socketAddress = InetSocketAddress(address.substringBefore(':'), 25565)
            if (socketAddress.isUnresolved)
                throw ArgumentParseException("未知服务器")
            socketAddress
        }
    }

    fun isServer(address: String): Boolean {
        return address.contains(':') || minecraftConfig.servers.containsKey(address.lowercase())
    }

    override fun parse(literal: String): InetSocketAddress {
        return if (minecraftConfig.servers.containsKey(literal.lowercase())) {
            val spec = minecraftConfig.servers[literal.lowercase()]
            InetSocketAddress(spec!!.host, spec.port)
        } else {
            resolveInetAddress(literal)
        }
    }
}
