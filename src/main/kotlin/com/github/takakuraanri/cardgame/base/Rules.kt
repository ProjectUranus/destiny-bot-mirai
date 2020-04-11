package com.github.takakuraanri.cardgame.base

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap

interface Rule {
    val name: String
    fun validate(player: Player, lastRule: Rule?, cards: List<Card>, lastCards: List<Card>): Boolean
}

interface RuleCardGroup: Rule {
    override fun validate(player: Player, lastRule: Rule?, cards: List<Card>, lastCards: List<Card>) = validate(player, lastRule, cards, lastCards, cards.toCardGroups(), lastCards.toCardGroups())
    fun validate(player: Player, lastRule: Rule?, cards: List<Card>, lastCards: List<Card>, cardGroups: List<CardGroup>, lastCardGroups: List<CardGroup>): Boolean
}

object RuleKingBomb: Rule {
    override val name = "王炸"

    override fun validate(player: Player, lastRule: Rule?, cards: List<Card>, lastCards: List<Card>) =
        cards.first() == Cards.CARD_GHOST && cards.last() == Cards.CARD_KING
}

object RuleBomb: Rule {
    override val name = "炸弹"

    override fun validate(player: Player, lastRule: Rule?, cards: List<Card>, lastCards: List<Card>): Boolean =
        if (cards.size == 4 && cards.all { it == cards.first() })
            if (lastCards.isEmpty() || lastRule != RuleBomb) {
                true
            } else {
                Card.Comparator.compare(cards.first(), lastCards.first()) > 0
            } else false
}

object RuleSingle: Rule {
    override val name = "单"

    override fun validate(player: Player, lastRule: Rule?, cards: List<Card>, lastCards: List<Card>): Boolean =
        cards.size == 1 && (lastCards.isEmpty() || cards.first().amount > lastCards.first().amount) && (lastRule == null || lastRule == RuleSingle)
}

object RuleDouble: Rule {
    override val name = "对"

    override fun validate(player: Player, lastRule: Rule?, cards: List<Card>, lastCards: List<Card>): Boolean =
        cards.size == 2 && cards.first() == cards.last() && (lastCards.isEmpty() || cards.first().amount > lastCards.first().amount) && (lastRule == null || lastRule == RuleDouble)
}

object RuleTriple: Rule {
    override val name = "三张"

    override fun validate(player: Player, lastRule: Rule?, cards: List<Card>, lastCards: List<Card>): Boolean =
        cards.size == 3 && cards.count { it == cards.first() } == 3 && (lastCards.isEmpty() || cards.first().amount > lastCards.first().amount) && (lastRule == null || lastRule == RuleTriple)
}

object RuleChain: RuleCardGroup {
    override val name = "顺子"

    override fun validate(
        player: Player,
        lastRule: Rule?,
        cards: List<Card>,
        lastCards: List<Card>,
        cardGroups: List<CardGroup>,
        lastCardGroups: List<CardGroup>
    ): Boolean {
        if (cards.any { it == Cards.CARD_2 || it == Cards.CARD_GHOST || it == Cards.CARD_KING })
            return false
        val size = cardGroups.first().size
        return (lastCardGroups.isEmpty() || size == lastCardGroups.size && cards.first().amount > lastCards.first().amount) &&
                cardGroups.all { it.size == size } &&
                when (size) {
                    1 -> cardGroups.size >= 5
                    2 -> cardGroups.size >= 3
                    3 -> cardGroups.size >= 2
                    else -> false
                } &&
                cards.distinct().isContinuously()
    }
}

object RuleThreeWithOne: RuleCardGroup {
    override val name = "三带一"

    override fun validate(
        player: Player,
        lastRule: Rule?,
        cards: List<Card>,
        lastCards: List<Card>,
        cardGroups: List<CardGroup>,
        lastCardGroups: List<CardGroup>
    ): Boolean {
        if (cardGroups.size != 2)
            return false
        val threes = cardGroups.find { it.size == 3 }
        val taken = cardGroups.find { it.size == 1 }
        val lastThrees = lastCardGroups.find { it.size == 3 }
        return threes != null && taken != null &&
                (lastRule == null || (lastRule == RuleThreeWithOne && threes > lastThrees!!) || lastCards.isEmpty())

    }
}

object RuleThreeWithTwo: RuleCardGroup {
    override val name = "三带二"

    override fun validate(
        player: Player,
        lastRule: Rule?,
        cards: List<Card>,
        lastCards: List<Card>,
        cardGroups: List<CardGroup>,
        lastCardGroups: List<CardGroup>
    ): Boolean {
        if (cardGroups.size != 2)
            return false
        val threes = cardGroups.find { it.size == 3 }
        val taken = cardGroups.find { it.size == 2 }
        val lastThrees = lastCardGroups.find { it.size == 3 }
        return threes != null && taken != null &&
                (lastRule == null || (lastRule == RuleThreeWithTwo && threes > lastThrees!!) || lastCards.isEmpty())
    }
}

object RuleFourWithTwo: RuleCardGroup {
    override val name = "四带二"

    override fun validate(
        player: Player,
        lastRule: Rule?,
        cards: List<Card>,
        lastCards: List<Card>,
        cardGroups: List<CardGroup>,
        lastCardGroups: List<CardGroup>
    ): Boolean {
        if (cardGroups.size != 2)
            return false
        val fours = cardGroups.find { it.size == 4 }
        val taken = cardGroups.filter { it.size == 1 }
        val lastFours = lastCardGroups.find { it.size == 4 }
        return fours != null && taken.size == 2 &&
                (lastRule == null || (lastRule == RuleFourWithTwo && fours > lastFours!!) || lastCards.isEmpty())
    }
}

object RuleAirplaneOneWing: RuleCardGroup {
    override val name = "单翼飞机"

    override fun validate(
        player: Player,
        lastRule: Rule?,
        cards: List<Card>,
        lastCards: List<Card>,
        cardGroups: List<CardGroup>,
        lastCardGroups: List<CardGroup>
    ): Boolean {
        val plane = cardGroups.filter { it.size == 3 }
        val lastPlane = lastCardGroups.filter { it.size == 3 }
        val taken = cardGroups.filter { it.size == 1 }
        return plane.size >= 2 && plane.map { it.card }.isContinuously() && plane.size == taken.size &&
                (lastRule == null || (lastRule == RuleAirplaneOneWing && plane.first() > lastPlane.first()) || lastCards.isEmpty())
    }
}

object RuleAirplaneTwoWing: RuleCardGroup {
    override val name = "双翼飞机"

    override fun validate(
        player: Player,
        lastRule: Rule?,
        cards: List<Card>,
        lastCards: List<Card>,
        cardGroups: List<CardGroup>,
        lastCardGroups: List<CardGroup>
    ): Boolean {
        val plane = cardGroups.filter { it.size == 3 }
        val lastPlane = lastCardGroups.filter { it.size == 3 }
        val taken = cardGroups.filter { it.size == 2 }
        return plane.size >= 2 && plane.map { it.card }.isContinuously() && plane.size == taken.size &&
                (lastRule == null || (lastRule == RuleAirplaneTwoWing && plane.first() > lastPlane.first()) || lastCards.isEmpty())
    }
}

fun quickRuleMatch(cards: List<Card>): Map<Rule, List<List<CardGroup>>> = runBlocking {
    if (cards.isEmpty()) return@runBlocking emptyMap<Rule, List<List<CardGroup>>>()
    val cardGroups = cards.toCardGroups()
    val cardSet = cards.distinct()
    val matched = ConcurrentHashMap<Rule, List<List<CardGroup>>>()
    // Single
    launch {
        matched[RuleSingle] = cardSet.asSequence().map { listOf(CardGroup(it, 1)) }.toList()
    }
    // Double
    launch {
        matched[RuleDouble] = cardGroups.asSequence().filter { it.size >= 2 }.map { listOf(CardGroup(it.card, 2)) }.toList()
    }
    // Triple
    launch {
        matched[RuleTriple] = cardGroups.asSequence().filter { it.size >= 3 }.map { listOf(CardGroup(it.card, 3)) }.toList()
    }
    // Chain
    launch {
        val groups = mutableListOf<List<CardGroup>>()
        val list = mutableListOf<Card>()
        for (i in cardSet.indices) {
            if (list.isEmpty() || (list + cardSet[i]).isContinuously()) {
                list.add(cardSet[i])
            } else if (list.size >= 5) {
                for (j in 5..list.size) {
                    groups += list.windowed(j).map { it.map { card -> CardGroup(card, 1) } }
                }
                list.clear()
            }
        }
        matched[RuleChain] = groups
    }
    matched
}

val rules = listOf(
    RuleBomb,
    RuleSingle,
    RuleDouble,
    RuleTriple,
    RuleChain,
    RuleThreeWithOne,
    RuleThreeWithTwo,
    RuleFourWithTwo,
    RuleAirplaneOneWing,
    RuleAirplaneTwoWing,
    RuleKingBomb
)
