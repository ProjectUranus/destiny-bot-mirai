package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.command.destinyBrigadierCommands
import cn.ac.origind.destinybot.image.getImage
import com.mojang.brigadier.CommandDispatcher
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import org.junit.Test

class TestBot {
    @Test
    fun testWeeklyReport(): Unit = runBlocking {
        getImage("https:${getLatestWeeklyReportURL()}", false)
    }

    fun testBrigadier(): Unit = runBlocking {
        val dispatcher = CommandDispatcher<MessageEvent>()
        destinyBrigadierCommands(dispatcher)
    }
}
