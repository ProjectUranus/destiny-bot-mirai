package net.origind.destinybot.features.destiny.image

import java.awt.image.RenderedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun RenderedImage.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    ImageIO.write(this, "png", stream)
    return stream.toByteArray()
}
