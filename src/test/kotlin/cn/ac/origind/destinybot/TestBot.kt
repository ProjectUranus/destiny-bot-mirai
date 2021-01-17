package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.image.getImage
import kotlinx.coroutines.runBlocking
import org.junit.Test

class TestBot {
    @Test
    fun testWeeklyReport(): Unit = runBlocking {
        getImage("https:${getLatestWeeklyReportURL()}", false)
    }

    fun testBrigadier(): Unit = runBlocking {
    }
}
