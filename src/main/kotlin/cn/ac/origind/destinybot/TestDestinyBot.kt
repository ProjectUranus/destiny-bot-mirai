package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.command.CommandManager
import cn.ac.origind.destinybot.command.destinyBrigadierCommands
import com.mojang.brigadier.CommandDispatcher
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.message.MessageEvent
import java.awt.Color

val normalColor = Color(255, 255, 255)
val pveColor = Color(87, 145, 190)
val pvpColor = Color(245, 91, 91)
val godColor = Color(227, 202, 87)

fun main(args: Array<String>) {
    runBlocking {
        val dispatcher: CommandDispatcher<MessageEvent> = CommandDispatcher()
        destinyBrigadierCommands(CommandManager.dispatcher)
    }
}
