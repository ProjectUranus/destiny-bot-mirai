package cn.ac.origind.destinybot

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
        searchUsersProfile("Nanami Arihara").forEach { profile ->
            ImageIO.write(
                profile?.characters?.data?.map { (id, character) ->
                    character
                }?.toImage(), "png", File("temp.png"))
        }
    }
}
