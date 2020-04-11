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

open class Card(
    val color: CardColor,
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
