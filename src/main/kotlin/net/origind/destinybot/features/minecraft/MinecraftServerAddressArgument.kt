package net.origind.destinybot.features.minecraft

import net.origind.destinybot.core.DestinyBot
import net.origind.destinybot.api.command.ArgumentParseException
import net.origind.destinybot.api.command.ArgumentType
import java.net.InetSocketAddress

object MinecraftServerAddressArgument : ArgumentType<InetSocketAddress> {
    override val clazz: Class<InetSocketAddress> = InetSocketAddress::class.java

    override fun parse(literal: String): InetSocketAddress {
        return if (DestinyBot.config[MinecraftSpec.servers].containsKey(literal)) {
            val spec = DestinyBot.config[MinecraftSpec.servers][literal]
            InetSocketAddress(spec!!.host, spec.port)
        } else {
            if (literal.contains(':')) {
                InetSocketAddress(literal.substringBefore(':'), Integer.parseInt(literal.substringAfter(':')))
            } else {
                val address = InetSocketAddress(literal.substringBefore(':'), 25565)
                if (address.isUnresolved)
                    throw ArgumentParseException("未知服务器")
                address
            }
        }
    }
}
