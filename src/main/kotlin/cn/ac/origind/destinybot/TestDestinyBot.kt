package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.database.searchItemDefinitions
import cn.ac.origind.destinybot.image.toImage
import kotlinx.coroutines.runBlocking
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

val normalColor = Color(255, 255, 255)
val pveColor = Color(87, 145, 190)
val pvpColor = Color(245, 91, 91)
val godColor = Color(227, 202, 87)

fun main(args: Array<String>) {
    runBlocking {
        searchItemDefinitions("紧急求生包").forEach { item ->
            ImageIO.write(item.toImage(getItemPerks(item)), "png", File("temp.png"))
        }
    }
}
