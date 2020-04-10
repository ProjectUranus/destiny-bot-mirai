package cn.ac.origind.uno

import net.mamoe.mirai.utils.suspendToExternalImage
import java.awt.Font
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import javax.imageio.ImageIO

val cardFont = Font("Microsoft YaHei", Font.PLAIN, 32)
val goldenCardImage = ImageIO.read(Card::class.java.getResourceAsStream("/Cards/GoldenCard.png"))!!
val mainCardImage = ImageIO.read(Card::class.java.getResourceAsStream("/Cards/MainCard.png"))!!

val Card.image get() =
    ImageIO.read(Card::class.java.getResourceAsStream("/Cards/${color}/${value}.png")) ?: goldenCardImage

fun drawUnoCards(cards: List<Card>) : BufferedImage {
    if (cards.isEmpty()) return BufferedImage(1, 1, TYPE_INT_RGB)
    if (cards.size == 1) return cards.first().image

    val count = 50.coerceAtMost(cards.size)
    val images = (if (cards.size > 50) cards.take(50) else cards).map { it.image }
    val first = images.first()
    val eachWidth = (first.width.toDouble() / 5 * 2).toInt()
    val width = first.width + eachWidth * (count - 1)
    val height = first.height
    val image = BufferedImage(width, height + 55, TYPE_INT_RGB)
    val graphics = image.createGraphics()
    graphics.font = cardFont
    graphics.color = white
    graphics.fillRect(0, 0, width, height + 55)

    var x = 0
    var textX = first.width / 2 - 80
    val textY = height + 10
    for (index in 0 until count) {
        val image = images[index]
        val card = cards[index]
        graphics.drawImage(image, x, 0, null)
        graphics.color = card.color.color
        graphics.drawString(card.shortName, textX, textY)
        textX += eachWidth
        x += eachWidth
    }
    graphics.dispose()
    return image
}
