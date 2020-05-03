package cn.ac.origind.destinybot.image

import cn.ac.origind.destinybot.response.lightgg.ItemDefinition
import cn.ac.origind.destinybot.response.lightgg.ItemPerks
import cn.ac.origind.destinybot.response.lightgg.PerkType
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Image
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB


val normalColor = Color(255, 255, 255)
val pveColor = Color(87, 145, 190)
val pvpColor = Color(245, 91, 91)
val godColor = Color(227, 202, 87)

val legendaryColor = Color(82, 46, 100)

fun text(text: String) : BufferedImage {
    val image = BufferedImage(300, 30, TYPE_INT_RGB)
    with (image.createGraphics()) {
        font = Font("Segoe UI", Font.PLAIN, 18)
        drawString(text, 0, 0)
        dispose()
    }
    return image
}

suspend fun ItemDefinition.toImage(perks: ItemPerks) : BufferedImage {
    val image = getImage(screenshot!!)
    with (image.createGraphics()) {
        font = Font("Microsoft YaHei UI", Font.BOLD, 66)
        var metrics = getFontMetrics(font)

        // Icon
        drawImage(getImage(displayProperties?.icon!!), 140, 100, null)
        stroke = BasicStroke(2f)
        color = Color.white
        drawRect(140, 100, 96, 96)

        drawString(displayProperties?.name!!, 260, 90 + metrics.ascent)

        font = Font("Microsoft YaHei UI", Font.PLAIN, 30)
        metrics = getFontMetrics(font)
        if (itemTypeAndTierDisplayName?.contains("传说") == true) {
            color = legendaryColor
            drawString(itemTypeAndTierDisplayName!!, 260, 170 + metrics.ascent)
            color = Color.white
        } else drawString(itemTypeAndTierDisplayName!!, 260, 170 + metrics.ascent)
        drawString(displayProperties?.description!!, 140, 240 + metrics.ascent)

        if (perks.onlyCurated) drawString("武器特性", 140, 300 + metrics.ascent)
        else drawString("随机特性", 140, 300 + metrics.ascent)
        stroke = BasicStroke(3f)
        drawLine(138, 345, 866, 345)

        var x = 138
        var y = 355
        if (perks.onlyCurated) {
            for (perk in perks.curated) {
                if (perk?.displayProperties?.name?.contains("框架") == true) {
                    drawImage(getImage(perk.displayProperties?.icon!!).getScaledInstance(80, 80, Image.SCALE_SMOOTH), x, y, null)
                } else
                    drawImage(perkImage(perk.displayProperties?.icon!!, 0).getScaledInstance(80, 80, Image.SCALE_DEFAULT), x, y, null)
                x += 93
                drawLine(x, 355, x, 355 + 255)
                x += 16
            }
        }
        else {
            val barrels = perks.all.filter { it.type == PerkType.BARREL }
            val magazines = perks.all.filter { it.type == PerkType.MAGAZINE }
            val perk1 = perks.all.filter { it.type == PerkType.PERK1 }
            val perk2 = perks.all.filter { it.type == PerkType.PERK2 }

            arrayOf(barrels, magazines, perk1, perk2).forEach {
                for (perk in it) {
                    drawImage(perkImage(perk.displayProperties?.icon!!, perk.perkRecommend).getScaledInstance(80, 80, Image.SCALE_DEFAULT), x, y, null)
                    y += 100
                }
                y = 355
                x += 93
                drawLine(x, 355, x, 355 + 255)
                x += 16
            }
        }

        dispose()
    }
    return image
}

suspend fun perkImage(icon: String, perk: Int): BufferedImage {
    val image = BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)
    val outsidePx = 2.0
    with (image.createGraphics()) {
        color = Color(178, 207, 227)
        fill(Ellipse2D.Double(0.0, 0.0, 128.0, 128.0))
        color = when (perk) {
            0 -> pveColor
            1 -> pvpColor
            2 -> godColor
            else -> Color(0, 0, 0, 0)
        }
        fill(Ellipse2D.Double(outsidePx, outsidePx, 128.0 - outsidePx * 2, 128.0 - outsidePx * 2))
        drawImage(getImage(icon), 16, 16, null)
        dispose()
    }
    return image
}