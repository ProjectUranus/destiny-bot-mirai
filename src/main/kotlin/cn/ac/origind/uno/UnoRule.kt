package cn.ac.origind.uno

import kotlin.random.Random

object UnoRule {
    fun IsValid(thisCard: Card, lastCard: Card, state: Desk.GamingState) : Boolean {
        when (state) {
            Desk.GamingState.Gaming ->
                return when (thisCard.type) {
                    CardType.DrawFour,
                    CardType.Wild,
                    CardType.Special
                    -> true
                    else -> thisCard.color == lastCard.color || thisCard.valueIndex == lastCard.valueIndex
                }

            Desk.GamingState.WaitingDrawTwoOverlay ->
            return thisCard.type == CardType.DrawTwo

            Desk.GamingState.WaitingDrawFourOverlay ->
            return thisCard.type == CardType.DrawFour

            Desk.GamingState.Doubting ->
            return false
        }
        return false
    }

    private val Rng = Random("YuukiAsuna-Kantinatron-Neutron-KirigayaKazuto-Cryptoshop-Nephren Ruq Insania".hashCode())

    fun IsValidForFollowCard(thisCard: Card, lastCard: Card?, state: Desk.GamingState) : Boolean = when (state) {
        Desk.GamingState.Gaming -> thisCard.type == CardType.Number && thisCard.color == lastCard?.color && thisCard.value == lastCard.value

        Desk.GamingState.WaitingDrawTwoOverlay,
        Desk.GamingState.WaitingDrawFourOverlay,
        Desk.GamingState.Doubting
        -> false
        else -> throw NullPointerException("上一张牌不存在...这本不该发生...")
    }

    fun ExtractCommand(cards: List<Card>, lastCard: Card, state: Desk.GamingState): String = when (state) {
        Desk.GamingState.Gaming,
        Desk.GamingState.WaitingDrawTwoOverlay,
        Desk.GamingState.WaitingDrawFourOverlay
        -> {
            val card = ExtractCard(cards, lastCard, state)
            if (card?.color == CardColor.Wild) card.color = ToWildColor(cards)
            card?.shortName ?: "摸了"
        }

        Desk.GamingState.Doubting -> {
            if (Rng.nextInt(8) > 5) "喵喵喵?" else "不质疑"
        }
        else -> throw Exception("这本不应该发生...")
    }

    fun PickColor() : CardColor = when (Rng.nextInt(4)) {
        0 -> CardColor.Red
        1 -> CardColor.Green
        2 -> CardColor.Blue
        3 -> CardColor.Yellow
        else -> throw Exception("WTF??")
    }

    fun ToWildColor(cards: List<Card>) : CardColor
    {
        val gcards = cards.filter { card -> card.color != CardColor.Wild && card.color != CardColor.Special }
        if (gcards.isEmpty()) return PickColor()
        val dic = mutableMapOf(
            CardColor.Red to 0,
            CardColor.Green to 0,
            CardColor.Blue to 0,
            CardColor.Yellow to 0
        )
        for (gcard in gcards)
            dic[gcard.color] = (dic[gcard.color] ?: 0) + 1

        return dic.asSequence().sortedBy { it.value }.first().key
    }

    fun ExtractCard(cards: List<Card>, lastCard: Card, state: Desk.GamingState) : Card?
    {
//        lock (Desk.Locker)
//        {
        val valids = cards.filter { IsValid(it, lastCard, state) }

        if (valids.isEmpty()) return null

        val valueWithCards = valids.asSequence().map { it to GetValue(it, lastCard, valids, cards) }

        var maxs = valueWithCards.filter { pair -> pair.second == valueWithCards.maxBy { it.second }?.second }
        return maxs.first().first
//        }
    }

    fun GetValue(valid: Card, lastCard: Card, valids: List<Card>, allCards: List<Card>) : Int
    {
        var value = 0
        if (valid.color == CardColor.Special) value += -50
        if (valid.color == CardColor.Wild) value += -80
        if (valid.type == CardType.Number) value += 1
        if (valid.type == CardType.Reverse) value += 10 // 功能牌优先
        if (valid.type == CardType.Skip) value += 10
        if (valid.type == CardType.DrawTwo) value += 10
        if (allCards.count { it.valueIndex == valid.valueIndex } > 1) value += 5
        return value
    }
}