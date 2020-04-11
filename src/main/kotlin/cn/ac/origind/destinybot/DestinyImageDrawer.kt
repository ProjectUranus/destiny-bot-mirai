package cn.ac.origind.destinybot

import java.awt.Font
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB

fun text(text: String) : BufferedImage {
    val image = BufferedImage(300, 30, TYPE_INT_RGB)
    with (image.createGraphics()) {
        font = Font("Segoe UI", Font.PLAIN, 18)
        drawString(text, 0, 0)
        dispose()
    }
    return image
}