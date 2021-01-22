package cn.ac.origind.destinybot

import com.github.steveice10.mc.auth.service.AuthenticationService
import com.github.steveice10.mc.auth.service.SessionService
import com.github.steveice10.mc.protocol.MinecraftConstants
import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket
import com.github.steveice10.packetlib.Client
import com.github.steveice10.packetlib.event.session.DisconnectedEvent
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent
import com.github.steveice10.packetlib.event.session.SessionAdapter
import com.github.steveice10.packetlib.packet.Packet
import com.github.steveice10.packetlib.tcp.TcpSessionFactory
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Test


class TestMCProtocolLib {
    @Test
    fun testAuth() {
        val authService = AuthenticationService()
        authService.baseUri = "https://skin.youtiao.dev/api/yggdrasil/authserver/".toHttpUrl().toUri()
        authService.username = "lasm_gratel@hotmail.com"
        authService.password = "Lasm_Gratel"
        authService.login()

        // Can also use "new MinecraftProtocol(USERNAME, PASSWORD)"
        // if you don't need a proxy or any other customizations.
        val protocol = MinecraftProtocol(authService)
        println("Successfully authenticated user.")


        val sessionService = SessionService()

        val client = Client("minecraft.youtiao.dev", 1984, protocol, TcpSessionFactory())
        client.session.setFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService)
        client.session.addListener(object : SessionAdapter() {
            override fun packetReceived(event: PacketReceivedEvent) {
                if (event.getPacket<Packet>() is ServerJoinGamePacket) {
                    event.session.send(ClientChatPacket("Hello, this is a test of MCProtocolLib."))
                } else if (event.getPacket<Packet>() is ServerChatPacket) {
                    val message = event.getPacket<ServerChatPacket>().message
                    println("Received Message: $message")
                    event.session.disconnect("Finished")
                }
            }

            override fun disconnected(event: DisconnectedEvent) {
                println("Disconnected: " + event.reason)
                if (event.cause != null) {
                    event.cause.printStackTrace()
                }
            }
        })

        client.session.connect(true)
    }
}
