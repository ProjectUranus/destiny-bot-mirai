package net.origind.destinybot.features.destiny.image

import net.origind.destinybot.features.destiny.response.CharacterComponent
import net.origind.destinybot.features.destiny.response.lightgg.ItemDefinition
import net.origind.destinybot.features.destiny.response.lightgg.ItemPerks
import net.origind.destinybot.features.destiny.response.lightgg.ItemTier
import net.origind.destinybot.features.destiny.response.lightgg.PerkType
import java.awt.*
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB

val races = arrayOf("人类", "觉醒者", "EXO", "未知")
val classes = arrayOf("泰坦", "猎人", "术士", "未知")
val genders = arrayOf("男", "女", "未知")

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

suspend fun List<CharacterComponent>.toImage() : BufferedImage {
    val image = BufferedImage(474, size * 104, BufferedImage.TYPE_INT_ARGB)
    var y = 0
    for (character in this) {
        val icon = getImage(character.emblemBackgroundPath)
        with(image.createGraphics()) {
            font = Font("Microsoft YaHei UI", Font.BOLD, 24)
            var metrics = getFontMetrics(font)

            drawImage(icon, 0, y, null)

            drawString(classes[character.classType], 88, 16 + y + metrics.ascent)

            color = Color.YELLOW
            drawString(character.light.toString(), 390, 14 + y + metrics.ascent)
            font = Font("Microsoft YaHei UI", Font.BOLD, 19)
            metrics = getFontMetrics(font)
            color = Color.GRAY
            drawString("${genders[character.genderType]} ${races[character.raceType]}", 88, 54 + y + metrics.ascent)

            y += 108

            dispose()
        }
    }
    return image
}

suspend fun ItemDefinition.toImage(perks: ItemPerks) : BufferedImage {
    val image = getImage(screenshot!!)
    with (image.createGraphics()) {
        setRenderingHints(RenderingHints(mapOf(
            RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.KEY_ALPHA_INTERPOLATION to RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY,
            RenderingHints.KEY_COLOR_RENDERING to RenderingHints.VALUE_COLOR_RENDER_QUALITY,
            RenderingHints.KEY_RENDERING to RenderingHints.VALUE_RENDER_QUALITY,
            RenderingHints.KEY_TEXT_ANTIALIASING to RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        )))

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
        drawString(displayProperties?.description!!, 140, 230 + metrics.ascent)

        if (perks.onlyCurated) drawString("武器特性", 140, 300 + metrics.ascent)
        else drawString("随机特性", 140, 300 + metrics.ascent)
        stroke = BasicStroke(3f)
        drawLine(138, 345, 866, 345)

        var x = 138
        var y = 355
        if (perks.onlyCurated) {
            for (i in perks.curated.indices) {
                val perk = perks.curated[i]
                if (tier == ItemTier.EXOTIC && i == 0) {
                    drawImage(getImage(perk.url!!).getScaledInstance(80, 80, Image.SCALE_SMOOTH), x, y, null)
                } else if (perk.displayProperties?.name?.contains("Frame") == true) {
                    drawImage(getImage(perk.url!!).getScaledInstance(80, 80, Image.SCALE_SMOOTH), x, y, null)
                } else
                    drawImage(perkImage(perk.url!!, 0).getScaledInstance(80, 80, Image.SCALE_SMOOTH), x, y, null)
                x += 93
                if (i < perks.curated.indices.last)
                    drawLine(x, 355, x, 355 + 80)
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
                    drawImage(perkImage(perk.url!!, perk.perkRecommend).getScaledInstance(80, 80, Image.SCALE_SMOOTH), x, y, null)
                    y += 100
                }
                x += 93
                drawLine(x, 355, x, y)
                x += 16
                y = 355
            }
        }

        dispose()
    }
    return image
}

suspend fun perkImage(icon: String, perk: Int): BufferedImage {
    val image = BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)
    val outsidePx = 3.5
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
