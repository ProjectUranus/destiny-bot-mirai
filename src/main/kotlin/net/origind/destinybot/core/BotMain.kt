package net.origind.destinybot.core

import kotlinx.coroutines.runBlocking

@ExperimentalStdlibApi
fun main(args: Array<String>) = runBlocking {
    val bot = DestinyBot
    bot.init()
    bot.start()
    bot.close()
}
