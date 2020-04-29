package cn.ac.origind.destinybot

import kotlinx.coroutines.runBlocking

object TestDestinyBot {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        println(getItemPerks("715338174").curated.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
    }
}
