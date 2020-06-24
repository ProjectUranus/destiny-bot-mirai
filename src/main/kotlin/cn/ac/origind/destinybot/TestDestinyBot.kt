package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.image.getImage
import kotlinx.coroutines.runBlocking
import java.awt.Color

val normalColor = Color(255, 255, 255)
val pveColor = Color(87, 145, 190)
val pvpColor = Color(245, 91, 91)
val godColor = Color(227, 202, 87)

fun main(args: Array<String>) {
    runBlocking {
        println(getImage("https:${getLatestWeeklyReportURL()}"))
    }
}
