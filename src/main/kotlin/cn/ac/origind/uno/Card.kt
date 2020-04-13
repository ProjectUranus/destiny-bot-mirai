package cn.ac.origind.uno

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import java.awt.Color

val white = Color.WHITE
val blue = Color(0x19, 0x76, 0xD2)
val yellow = Color(0xFF, 0xEB, 0x3B)
val green = Color(0x4C, 0xAF, 0x50)
val red = Color(0xF4, 0x43, 0x36)
val wild = Color(163, 154, 141)

fun Card(source: String) : Card? {
    var color: CardColor? = null
    var value: CardValue? = null

    fun SetColor(str: String) : Boolean {
        if (color != null)
            return false
        when (str) {
            "绿", "G" -> {
                color = CardColor.Green
                return true
            }
            "蓝", "B" -> {
                color = CardColor.Blue
                return true
            }
            "红", "R" -> {
                color = CardColor.Red
                return true
            }
            "黄", "Y" -> {
                color = CardColor.Yellow
                return true
            }
        }

        return false
    }

    fun SetValue(str: String, isFirst: Boolean) : Boolean
    {
        if (value != null)
            return false
        when (str)
        {
            "W" -> {
                value = CardValue.Wild
                return true
            }
            "S", "禁" -> {
                value = CardValue.Skip
                return true
            }
            "R" -> {
                value = CardValue.Reverse
                return true
            }
            "+2" -> {
                value = CardValue.DrawTwo
                return true
            }
            "+4" -> {
                value = CardValue.DrawFour
                return true
            }
            "转" -> {
                value = if (isFirst) CardValue.Wild else CardValue.Reverse
                return true
            }
        }

        if (str.length == 1 && str.matches(Regex("\\d")))
        {
            value = CardValue.values()[Integer.parseInt(str)]
            return true
        }

        return false
    }

    fun SetOne(str: String, isFirst: Boolean)
    {
        if (SetValue(str, isFirst))
            return
        SetColor(str)
    }

    fun Set(first: String, last: String)
    {
        if (first.contains("R")) {
            SetOne(last, false)
            SetOne(first, true)
            return
        }

//                if (last.Contains("R")) {
//                    SetOne(first, true);
//                    SetOne(last, false);
//                    return;
//                }
        SetOne(first, true)
        SetOne(last, false)
    }


    var s = source.trim().toUpperCase()
//    for (var specialCard in Card.SpecialCards.Where(specialCard => s == specialCard.ShortName))
//    return specialCard;

    when (s.length) {
        2 -> {
            var first = s.substring(0, s.length - 1)
            var last = s.substring(1, s.length - 1)
            Set(first, last)
        }
        3 -> {
            var f1 = s.substring(0, 1)
            var l1 = s.substring(1, 2)
            Set(f1, l1)
            if (color == null || value == null) {
                color = null
                value = null
            }

            var f2 = s.substring(0, 2)
            var l2 = s.substring(2, 1)
            Set(f2, l2)
        }
        else -> return null
    }

    return if (color == null || value == null) {
        null
    } else {
        Card(color!!, value!!)
    }
}

open class Card(
    var color: CardColor,
    val value: CardValue,
    val type: CardType = value.type
) : Comparable<Card> {
    val valueIndex = value.ordinal
    val ordinal = 15 * color.ordinal + valueIndex

    open val shortName: String = buildString {
        when (color) {
            CardColor.Wild -> {
            }
            CardColor.Red -> append("R")
            CardColor.Yellow -> append("Y")
            CardColor.Green -> append("G")
            CardColor.Blue -> append("B")
            CardColor.Special -> (this@Card as SpecialCard).shortName
        }
        when (type) {
            CardType.Wild -> append("W")
            CardType.DrawTwo -> append("+2")
            CardType.Number -> append(valueIndex)
            CardType.Reverse -> append("R")
            CardType.Skip -> append("S")
            CardType.DrawFour -> append("+4")
            else -> {}
        }
    }

    override fun compareTo(other: Card): Int = ordinal.compareTo(other.ordinal)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Card) return false

        if (color != other.color) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    fun IsValidForPlayerAndRemove(player: UnoPlayer) : Boolean
    {
        loop@ for (index in 0 until player.cards.size)
        {
            val playerCard = player.cards[index]
            when (type)
            {
                CardType.Number, CardType.Skip, CardType.Reverse, CardType.DrawTwo -> {
                    if (playerCard.value == value && playerCard.color == color) {
                        player.cards.remove(playerCard);
                        return true;
                    }
                    continue@loop;
                }
                CardType.DrawFour, CardType.Wild -> {
                    if (playerCard.valueIndex == valueIndex) {
                        player.cards.remove(playerCard);
                        return true;
                    }
                    continue@loop;
                }
                CardType.Special -> // 假装不知道如何重写运算符
                {
//                    if (!(playerCard is ISpecialCard)) continue;
//                    if (((ISpecialCard) card).ShortName == ((ISpecialCard) playerCard).ShortName) {
//                        player.Cards.RemoveAt(index);
//                        return true;
//                    }
                    continue@loop;
                }
            }
        }

        return false;
    }
}

enum class CardColor(val color: Color) {
    Red(red), Yellow(yellow), Green(green), Blue(blue), Wild(wild), Special(wild)
}

enum class CardValue {
    Zero,
    One,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,

    Reverse,
    Skip,
    DrawTwo,

    Wild,
    DrawFour,

    Special;

    val chance by lazy { if (this == Zero) 2 else 4 }

    val type by lazy {
        when (this) {
            Reverse -> CardType.Reverse
            Skip -> CardType.Skip
            DrawTwo -> CardType.DrawTwo
            DrawFour -> CardType.DrawFour
            Wild -> CardType.Wild
            Special -> CardType.Special
            else -> CardType.Number
        }
    }
}

enum class CardType {
    Number, Reverse, Skip, DrawTwo, Wild, DrawFour, Special
}

fun getDefaultColors(value: CardValue) = flow {
    when (value) {
        CardValue.DrawFour, CardValue.Wild -> {
            emit(CardColor.Wild)
            emit(CardColor.Wild)
        }
        CardValue.Special -> {}
        else -> {
            emit(CardColor.Red)
            emit(CardColor.Green)
            emit(CardColor.Blue)
            emit(CardColor.Yellow)
        }
    }
}

suspend fun generateDefaultCards() = withContext(Dispatchers.Default) {
    val list = mutableListOf<Card>()
    for (i in 0 until 15) {
        val value = CardValue.values()[i]
        for (j in 0 until value.chance) {
            list.addAll(getDefaultColors(value).map { Card(it, value) }.toList())
        }
    }
    list
}
