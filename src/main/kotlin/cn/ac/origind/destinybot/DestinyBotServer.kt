package cn.ac.origind.destinybot

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun Application.destinyBotModule() {
    routing {
        get("/plain") {
            call.respondText("Hello World!")
        }
    }
}

object DestinyBotServer {
    init {
        GlobalScope.launch {
            embeddedServer(
                Netty, port = 8080, module = Application::destinyBotModule
            ).apply { start(wait = true) }
        }
    }
}