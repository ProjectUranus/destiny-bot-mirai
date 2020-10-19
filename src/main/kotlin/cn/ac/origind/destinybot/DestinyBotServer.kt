package cn.ac.origind.destinybot

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DestinyBotServer {
    init {
        val server = embeddedServer(Netty, port = 8081) {
            routing {
                get("/") {
                    call.respondText("Hello World!", ContentType.Text.Plain)
                }
                get("/demo") {
                    call.respondText("HELLO WORLD!")
                }
            }
        }
        GlobalScope.launch { server.start(true) }
    }
}
