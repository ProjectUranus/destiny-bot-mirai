package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.image.getImage
import kotlinx.coroutines.runBlocking
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

val normalColor = Color(255, 255, 255)
val pveColor = Color(87, 145, 190)
val pvpColor = Color(245, 91, 91)
val godColor = Color(227, 202, 87)

fun main(args: Array<String>) {
    runBlocking {
        val x = 1120
        val y = 407
        val image = BufferedImage(474, 460, BufferedImage.TYPE_INT_ARGB)
        val icon = getImage("/common/destiny2_content/icons/ecafe3e611c54e78656b85b77c8ee2f7.jpg")
        val outsidePx = 2.0
        with(image.createGraphics()) {
            font = Font("Microsoft YaHei UI", Font.BOLD, 24)
            var metrics = getFontMetrics(font)

            drawImage(icon, 0, 0, null)

            drawString("Titan", 1210 - x, 423 - y + metrics.ascent)
            font = Font("Microsoft YaHei UI", Font.BOLD, 19)
            metrics = getFontMetrics(font)
            color = Color.GRAY
            drawString("Exo Male", 1210 - x, 460 - y + metrics.ascent)

            dispose()
        }
        ImageIO.write(image, "png", File("temp.png"))
    }
}