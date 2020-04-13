package cn.ac.origind.uno

import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import javax.imageio.ImageIO
import kotlin.math.sqrt


val cardFont = Font("Microsoft YaHei", Font.PLAIN, 32)
val goldenCardImage = ImageIO.read(Card::class.java.getResourceAsStream("/Cards/GoldenCard.png"))!!
val mainCardImage = ImageIO.read(Card::class.java.getResourceAsStream("/Cards/MainCard.png"))!!

val Card.image get() =
    ImageIO.read(Card::class.java.getResourceAsStream("/Cards/${color}/${value}.png")) ?: goldenCardImage

fun drawUnoCards(cards: List<Card>) : BufferedImage {
    if (cards.isEmpty()) return BufferedImage(1, 1, TYPE_INT_ARGB)
    if (cards.size == 1) return cards.first().image

    val count = 50.coerceAtMost(cards.size)
    val images = (if (cards.size > 50) cards.take(50) else cards).map { it.image }
    val first = images.first()
    val eachWidth = (first.width.toDouble() / 5 * 2).toInt()
    val width = first.width + eachWidth * (count - 1)
    val height = first.height
    val image = BufferedImage(width, height + 55, TYPE_INT_ARGB)
    val graphics = image.createGraphics()
    graphics.font = cardFont
    graphics.color = white
    graphics.fillRect(0, 0, width, height + 55)

    var x = 0
    var textX = first.width / 2 - 80
    val textY = height + 35
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

object DeskRenderer {
    val Opacity = 0.3f
    val Padding = 20
    val BaseLength = mainCardImage.width
    val Height = mainCardImage.height
    val AQUA = Color(0x00ffff)
    val BLUE_VIOLET = Color(0x8a2be2)
    val AQUA_MARINE = Color(0x7fffd4)

    fun GetEachLength(baseLength: Int) = (baseLength / 5.0 * 2.0).toInt()

    fun GetLength(baseLength: Int, eachLength: Int, count: Int) =
        baseLength + eachLength * (count - 1)

    fun Graphics.RenderImageWithShadow(image: BufferedImage, x: Int, y: Int, radius: Float, opacity: Float)
    {
        //using (Image shadow = GaussianHelper.CreateShadow(image, radius, opacity))
        //    graphics.DrawImage(shadow, point);
        drawImage(image, x, y, null)
    }

    fun RenderBlankCards(count: Int, goldenCards: Int): BufferedImage {
        if (count == 0)
            return BufferedImage(1, 1, TYPE_INT_ARGB)
        if (count == 1)
            return mainCardImage

        val baseLength = BaseLength
        val eachWidth = GetEachLength(baseLength)
        val width = GetLength(baseLength, eachWidth, count)
        var x = 0
        val y = 0
        val bitmap = BufferedImage(width + Padding, Height + Padding, TYPE_INT_ARGB) // 增加一个阴影的padding
        val grap = bitmap.createGraphics()
        for (i in 0 until count - goldenCards)
        {
            grap.RenderImageWithShadow(mainCardImage, x, y, 5f, Opacity)
            x += eachWidth
        }

        for (i in 0 until goldenCards)
        {
            grap.RenderImageWithShadow(goldenCardImage, x, y, 5f, Opacity)
            x += eachWidth
        }

        return bitmap
    }

    fun RenderPlayers(players: List<UnoPlayer>): BufferedImage {
        // init
        val font = Font("Microsoft YaHei", Font.PLAIN, 52)
        val blankCards: List<BufferedImage> = players.map {
            if (it.publicCard)
                drawUnoCards(it.cards)
            else
                RenderBlankCards(
                    it.cards.size.coerceAtMost(50),
                    it.cards.count { card -> card is SpecialCard }.coerceAtMost(50))
        }
        val enumerable = blankCards
        val nicks = players.map { it.nick }
        val tempGraphics = BufferedImage(1, 1, TYPE_INT_ARGB).createGraphics()
        val metrics = tempGraphics.getFontMetrics(font)

        // text
        val longest = nicks.maxBy { it.length }?.length ?: 0

        val margin = 120

        val tempStr = "珂".repeat(longest)
        val maxTextSize = metrics.getLineMetrics(tempStr, tempGraphics)
        val textWidth = metrics.stringWidth(tempStr)
        val textHeight = maxTextSize.height
        val textCenterWidth = textWidth / 2
        val textCenter = textCenterWidth + margin
        val beforeRenderBlankCardWidth = margin + textWidth + margin
        var textPointX = margin
        var textPointY = 32

        // blank card
        val maxWidth = enumerable.maxBy { it.width }?.width ?: 0
        val baseHeight = enumerable.first().height
        var blankCardPointX = beforeRenderBlankCardWidth
        var blankCardPointY = 0

        // main image
        val eachHeight = GetEachLength(baseHeight).coerceAtLeast(100) // prevent zero
        val height = GetLength(baseHeight, eachHeight, players.size)
        val countWidth = margin + metrics.stringWidth("233") + margin
        val width = beforeRenderBlankCardWidth + maxWidth + countWidth

        val bitmap = BufferedImage(width, height, TYPE_INT_ARGB)
        val grap = bitmap.createGraphics()
        grap.color = Color.WHITE
        grap.drawRect(0, 0, width, height)
        grap.getFontMetrics(font).charWidth('2') * 3

        for (index in enumerable.indices)
        {
            val blankCard = enumerable[index]
            val player = players[index]

            grap.RenderImageWithShadow(blankCard, blankCardPointX, blankCardPointY, 5f, Opacity)
            grap.color = AQUA
            grap.drawString(player.cards.size.toString(), width - countWidth + margin, textPointY)
            textPointX = margin + textCenterWidth - metrics.stringWidth(player.nick) / 2
            grap.color = when {
                player.uno -> {
                    Color.RED
                }
                player.autoSubmit -> {
                    BLUE_VIOLET
                }
                else -> Color.GRAY
            }

            grap.drawString(player.nick, textPointX, textPointY)
            grap.color = Color.WHITE
            if (player.isCurrentPlayer)
            {
                grap.DrawTextRect(player.nick, font, textPointX, textPointY)
                if (player.desk.reversed)
                {
                    if (index == 0)
                    {
                        grap.color = Color.CYAN
                        grap.stroke = BasicStroke(15f)
                        val upLineY = (textPointY + (textHeight + 15) / 2).toInt()
                        val leftLineX = margin - 10
                        grap.drawLine(textPointX - 10, upLineY, leftLineX, upLineY) // 本玩家拉出来的线
                        val downLineY = (textPointY + eachHeight * players.size -
                                (eachHeight - textHeight) - 40).toInt()
                        grap.drawLine(leftLineX, upLineY - 5, leftLineX, downLineY)// 竖线
                        grap.DrawArraw(leftLineX - 5, downLineY, margin + textCenterWidth - metrics.stringWidth(players.last().nick) / 2 - 10, downLineY) // 到目标玩家的箭头
                    }
                    else
                        grap.DrawArraw(textCenter, textPointY - 20, textCenter, textPointY - 20 - 50)
                }
                else
                {
                    if (index == enumerable.size - 1)
                    {
                        grap.color = Color.CYAN
                        grap.stroke = BasicStroke(15f)
                        val upLineY = (textPointY + (textHeight + 15) / 2).toInt()
                        val leftLineX = margin - 10
                        grap.drawLine(textPointX - 10, upLineY, leftLineX, upLineY)
                        val downLineY = (textPointY - eachHeight * players.size +
                                (eachHeight - textHeight) + 130).toInt()
                        grap.drawLine(leftLineX, upLineY + 5, leftLineX, downLineY) // 竖线
                        grap.DrawArraw(leftLineX - 5, downLineY, margin + textCenterWidth - metrics.stringWidth(players.first().nick) / 2 - 10, downLineY)
                    }
                    else
                        grap.DrawArraw(textCenter, textPointY + 80 + 20, textCenter, textPointY + 80 + 20 + 50)
                }
            }
            blankCardPointY += eachHeight
            textPointY += eachHeight
        }

//        foreach (var image in enumerable)
//        {
//            if (!image.IsCachedImage())
//                image.Dispose()
//        }
//        font.Dispose()
        return bitmap
    }

    fun Graphics2D.DrawTextRect(text: String, font: Font, x: Int, y: Int)
    {
        val tempGraphics = BufferedImage(1, 1, TYPE_INT_ARGB).createGraphics()
        val metrics = tempGraphics.getFontMetrics(font)
        val rectSize = 10
        val margin = 10
        val size = metrics.stringWidth(text)
        val width = size + margin * 2 // margin*2/2
        val height = metrics.getLineMetrics(text, this).height + margin * 2
        stroke = BasicStroke(rectSize.toFloat())
        color = AQUA_MARINE
        drawRect(x - margin, y - margin, width, height.toInt())
        color = Color.WHITE
    }

    /**
     * Draw an arrow line between two points.
     * @param g the graphics component.
     * @param x1 x-position of first point.
     * @param y1 y-position of first point.
     * @param x2 x-position of second point.
     * @param y2 y-position of second point.
     * @param d  the width of the arrow.
     * @param h  the height of the arrow.
     */
    fun Graphics.drawArrowLine(x1: Int, y1: Int, x2: Int, y2: Int, d: Int, h: Int) {
        val dx = x2 - x1
        val dy = y2 - y1
        val D = sqrt(dx * dx + dy * dy.toDouble())
        var xm = D - d
        var xn = xm
        var ym = h.toDouble()
        var yn = -h.toDouble()
        var x: Double
        val sin = dy / D
        val cos = dx / D
        x = xm * cos - ym * sin + x1
        ym = xm * sin + ym * cos + y1
        xm = x
        x = xn * cos - yn * sin + x1
        yn = xn * sin + yn * cos + y1
        xn = x
        val xpoints = intArrayOf(x2, xm.toInt(), xn.toInt())
        val ypoints = intArrayOf(y2, ym.toInt(), yn.toInt())
        drawLine(x1, y1, x2, y2)
        fillPolygon(xpoints, ypoints, 3)
    }

    fun Graphics2D.DrawArraw(fromX: Int, fromY: Int, toX: Int, toY: Int)
    {
        // TODO SB AWT
//        val pen = new Pen(Color.Cyan, 15)
//        {
//            StartCap = LineCap.NoAnchor,
//            EndCap = LineCap.ArrowAnchor
//        }
        stroke = BasicStroke(15F)
        color = Color.CYAN
        drawArrowLine(fromX, fromY, toX, toY, 15, 15)
    }

    fun RenderLastCard(lastCard: BufferedImage): BufferedImage {
        // init
        val font = Font("Microsoft YaHei", Font.PLAIN, 48)

        // text
        val text = "上一张牌"
        val tempGraphics = BufferedImage(1, 1, TYPE_INT_ARGB).createGraphics()
        val metrics = tempGraphics.getFontMetrics(font)
        val textWidth = metrics.stringWidth(text)
        val margin = 44
        val beforeRenderLastCardWidth = margin + textWidth + margin
        val textPointX = margin
        val textPointY = 40

        // last card
        val cardWidth = lastCard?.width ?: 1
        val width = beforeRenderLastCardWidth + cardWidth
        var height = lastCard?.height ?: 1

        val bitmap = BufferedImage(width, height, TYPE_INT_ARGB)
        val grap = bitmap.createGraphics()

        grap.RenderImageWithShadow(lastCard!!, beforeRenderLastCardWidth, 0, 5f, Opacity)
        grap.color = Color.GRAY
        grap.font = font
        grap.drawString(text, textPointX, textPointY)

        return bitmap
    }

    fun Desk.RenderDesk() : BufferedImage {
        val playersImage = RenderPlayers(players)
        val lastCardImage = lastCard?.image!!

        val margin = 40
        val width = playersImage.width.coerceAtLeast(playersImage.width) + margin
        val height = margin + playersImage.height + margin + lastCardImage.height + margin
        val bitmap = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        //for (var i = 0; i < width; i++)
        //   for (var j = 0; j < height; j++)
        //       bitmap.SetPixel(i, j, Color.White);

        val grap = bitmap.createGraphics()
        grap.color = Color.WHITE
        grap.drawRect(0, 0, width, height)
        var y = 0

        y += margin
        grap.RenderImageWithShadow(playersImage, 0, y, 5f, Opacity)
        y += playersImage.height
        y += margin
        grap.RenderImageWithShadow(lastCardImage, 0, y, 5f, Opacity)
        y += margin

        return bitmap
    }
}