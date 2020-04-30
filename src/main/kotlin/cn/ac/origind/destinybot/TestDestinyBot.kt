package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.image.getImage
import kotlinx.coroutines.runBlocking
import java.awt.Color
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

val normalColor = Color(255, 255, 255)
val pveColor = Color(87, 145, 190)
val pvpColor = Color(245, 91, 91)
val godColor = Color(227, 202, 87)

fun main(args: Array<String>) {
    runBlocking {
        val image = BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)
        val icon = getImage("/common/destiny2_content/icons/0619b0257fec929e5b6eeb5890c99c53.png")
        val outsidePx = 2.0
        with(image.createGraphics()) {
            color = Color(178, 207, 227)
            fill(Ellipse2D.Double(0.0, 0.0, 128.0, 128.0))
            color = pvpColor
            fill(Ellipse2D.Double(outsidePx, outsidePx, 128.0 - outsidePx * 2, 128.0 - outsidePx * 2))
            drawImage(icon, 16, 16, null)
            dispose()
        }
        ImageIO.write(image, "png", File("temp.png"))
    }
}