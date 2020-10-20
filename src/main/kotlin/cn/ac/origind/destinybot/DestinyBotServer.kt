package cn.ac.origind.destinybot

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class DestinyBotServer : CoroutineScope {
    override val coroutineContext: CoroutineContext
            = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    init {
        val server = embeddedServer(Netty, port = 8081) {
            routing {
                get("/") {
                    call.respondText("Hello World!", ContentType.Text.Plain)
                }
                get("/auth") {
                    call.respondText(call.request.queryParameters["code"] ?: "no code")
                }
            }
        }
        launch { server.start(true) }
    }
}
