package com.github.takakuraanri.cardgame.base

import java.util.*

interface Card {
    val amount: Int
    object Comparator: kotlin.Comparator<Card> {
        override fun compare(o1: Card?, o2: Card?): Int =
            o1?.amount?.compareTo(o2?.amount ?: Int.MIN_VALUE) ?: 0
    }
}

data class BasicCard(override val amount: Int): Card

enum class Cards(val theName: String): Card {
    CARD_3("3"), CARD_4("4"), CARD_5("5"),
    CARD_6("6"), CARD_7("7"), CARD_8("8"),
    CARD_9("9"), CARD_10("10"), CARD_J("J"),
    CARD_Q("Q"), CARD_K("K"), CARD_A("A"),
    CARD_2("2"), CARD_GHOST("鬼"), CARD_KING("王");

    override val amount: Int = ordinal
    override fun toString() = theName

    companion object {
        operator fun get(index: Int) = values()[index]
        operator fun get(name: String) = when(name) {
            "鬼" -> CARD_GHOST
            "王" -> CARD_KING
            else -> valueOf("CARD_${name.toUpperCase()}")
        }
    }
}

fun newCardSet(): MutableList<Cards> {
    val cards = mutableListOf(
        Cards.CARD_GHOST,
        Cards.CARD_KING
    )
    for (i in 0 until Cards.values().size - 2)
        for (j in 0 until 4)
            cards += Cards[i]
    return cards
}

fun newShuffledCardSet() = newCardSet().shuffled()

data class CardGroup(val card: Card, val size: Int): Comparable<CardGroup> {
    override fun compareTo(other: CardGroup) =
        Card.Comparator.compare(card, other.card)
}

fun Iterable<Card>.toCardGroups(): List<CardGroup> {
    val array = Array(Cards.values().size) { 0 }
    forEach {
        array[it.amount] += 1
    }
    return array.asSequence().filter { it > 0 }.mapIndexed { card, amount ->
        CardGroup(
            Cards[card],
            amount
        )
    }.toList()
}

fun deserializeCards(message: String): List<Cards> {
    var tens = 0
    var msg = message
    while (msg != msg.replaceFirst("10", "")) {
        tens++; msg = msg.replaceFirst("10", "")
    }
    val withoutTen = message.replace("10", "")
    return withoutTen.toCharArray().asSequence().map { Cards[it.toUpperCase().toString()] }.plus(Array(tens) { Cards.CARD_10 }).sortedWith(
        Card.Comparator
    ).toList()
}

fun Iterable<CardGroup>.toCards() = flatMap { group -> Array(group.size) { group.card }.asIterable() }

fun Iterable<Card>.toString(): String = StringBuilder().let { sb -> sortedWith(Card.Comparator).forEach { sb.append("[$it]") }; sb.toString() }

fun <T> Collection<T>.containsAllWithNum(c: Collection<T>): Boolean {
    val set = c.toHashSet()
    return set.all {
        Collections.frequency(this, it) >= Collections.frequency(c, it)
    }
}
