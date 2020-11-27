package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.DestinyBot.logger
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
        fetchWishlist()
        ImageIO.write(Database.getItemDefinition("153979397").toImage(getItemPerks("153979397")), "png", File("output.png"))
        logger.info("Success")
    //        println(getItemPerks("3089417789"))
    }
}
