package cn.ac.origind.uno

import cn.ac.origind.uno.DeskRenderer.RenderDesk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.sendMessage
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.sendImage
import java.time.Instant
import java.util.*
import java.util.function.Predicate

class Desk(val group: Group) {
    var _firstSubmitDrawFourPlayer: UnoPlayer? = null
    val players = mutableListOf<UnoPlayer>()
    var cardsPool: List<Card> = runBlocking { generateDefaultCards() }
    var cardsQueue = LinkedList<Card>()

    var state = GamingState.Waiting
    val currentPlayer: UnoPlayer? get() = players[currentIndex]
    var currentIndex = 0
    var lastCard: Card? = null
    var lastNonDrawFourCard: Card? = null
    var lastSendPlayer: UnoPlayer? = null
    var overlayCardNum = 0
    var step = 0
    var reversed = false

    suspend fun finish(player: UnoPlayer) {
        group.sendMessage(atMessage(player.member, "赢了!"))
        for (player in players) player.publicCard = true
        group.sendImage(RenderDesk())
    }

    @ExperimentalStdlibApi
    suspend fun afterSend(player: UnoPlayer, card: Card) {
        lastCard = card
        if (card.type != CardType.DrawFour)
        {
            lastNonDrawFourCard = card
        }

        players.filter { it.cards.isEmpty() }.forEach { finish(it) }

        player.lastSendTime = Instant.now()
        lastSendPlayer = player
        Behave(player, card)
    }

    @ExperimentalStdlibApi
    suspend fun Behave(player: UnoPlayer, card: Card) {
        player.sendCards()
        moveNext()
        var nextPlayer = currentPlayer
        when (card.type)
        {
            CardType.Number ->
            // ignored
            return

            CardType.Reverse -> {
                group.sendMessage("方向反转.")
                reversed = !reversed
                moveNext()
                moveNext()
            }

            CardType.Skip -> {
                group.sendMessage(atMessage(nextPlayer?.member!!, "被跳过"))
                moveNext()
//                nextPlayer = currentPlayer
            }

            CardType.DrawTwo -> {
                state = GamingState.WaitingDrawTwoOverlay
                overlayCardNum += 2
                BehaveDrawTwo(nextPlayer!!)
            }

            CardType.Wild -> {
                group.sendMessage("变色.")
            }

            CardType.DrawFour -> {
                if (state == GamingState.Gaming) {
                    _firstSubmitDrawFourPlayer = Previous()
                    state = GamingState.WaitingDrawFourOverlay
                }
                overlayCardNum += 4
                BehaveDrawFour(nextPlayer!!)
            }

//            CardType.Special -> {
//                var special = (ISpecialCard) card;
//                desk.AddMessage($"特殊牌: {special.ShortName}, {special.Description}!");
//                special.Behave(desk);
//            }
        }
    }

    fun Previous() : UnoPlayer
    {
        val current = currentIndex
        reversed = !reversed
        moveNext()
        val cp = players[currentIndex]
        reversed = !reversed
        currentIndex = current
        return cp
    }

    @ExperimentalStdlibApi
    suspend fun BehaveDrawFour(nextPlayer: UnoPlayer) {
        if (nextPlayer.cards.any { it.type == CardType.DrawFour })
        {
            nextPlayer.member.sendMessage("你现在可以选择 叠加+4 或者 摸牌来让前面叠加的+2加到你手里")
        }
        else
        {
            if (overlayCardNum > 4)
            {
                FinishDraw(nextPlayer)
            }
            else
            {
                state = GamingState.Doubting
                group.sendMessage(atMessage(nextPlayer.member, "你要质疑吗？"))
            }
        }
    }

    @ExperimentalStdlibApi
    suspend fun FinishDraw(player: UnoPlayer)
    {
        if (state == GamingState.WaitingDrawFourOverlay || state == GamingState.WaitingDrawTwoOverlay || state == GamingState.Doubting)
        {
            state = GamingState.Gaming
            group.sendMessage(atMessage(player.member, "被加牌${overlayCardNum}张."))
            addCards(player, overlayCardNum)
            overlayCardNum = 0
            moveNext()
            sendLastCardMessage()
        }
    }

    @ExperimentalStdlibApi
    suspend fun BehaveDrawTwo(nextPlayer: UnoPlayer) {
        if (nextPlayer.cards.any { it.type == CardType.DrawTwo })
        {
            nextPlayer.member.sendMessage("你现在可以选择 叠加+2 或者 摸牌来让前面叠加的+2加到你手里")
        }
        else
        {
            FinishDraw(currentPlayer!!)
            state = GamingState.Gaming
        }
    }

    internal fun moveNext() {
        step++
        if (reversed) {
            currentIndex--
            if (currentIndex == -1)
                currentIndex = players.size - 1
        } else {
            currentIndex = (currentIndex + 1) % players.size
        }
    }

    fun hasPlayer(contact: Contact) : Boolean {
        return players.any { it.member.id == contact.id }
    }

    @ExperimentalStdlibApi
    suspend fun addCards(player: UnoPlayer, count: Int = 1, alsoSend: Boolean = true) {
        player.cards += generateCards(count)
        player.cards.sort()
        if (alsoSend)
            player.sendCards()
    }

    fun atMessage(member: Member, message: String) = buildMessageChain { add(At(member)); add(message) }
    fun atMessage(message: String, member: Member) = buildMessageChain { add(message); add(At(member)) }

    suspend fun sendLastCardMessage() {
        group.sendImage(RenderDesk())
        group.sendMessage(atMessage(currentPlayer!!.member, " 请出牌"))

    }

    @ExperimentalStdlibApi
    suspend fun finishDraw(player: UnoPlayer) {
        if (state == GamingState.WaitingDrawFourOverlay || state == GamingState.WaitingDrawTwoOverlay || state == GamingState.Doubting)
        {
            state = GamingState.Gaming
            group.sendMessage(atMessage(player.member, "被加牌${overlayCardNum}张."))
            addCards(player, overlayCardNum)
            overlayCardNum = 0
            moveNext()
            sendLastCardMessage()
        }
    }

    fun nextCard() =
        if (cardsQueue.isNotEmpty()) cardsQueue.poll() else cardsPool.random()

    @ExperimentalStdlibApi
    fun generateCards(n: Int) = buildList<Card> { for (i in 0 until n) add(nextCard()) }

    suspend fun nextCard(predicate: Predicate<Card>) = flow {
        while (true) {
            val card = cardsPool.random()
            if (predicate.test(card)) emit(card)
        }
    }

    @ExperimentalStdlibApi
    suspend fun finishDoubt(player: UnoPlayer, doubt: Boolean) {
        if (doubt) {
            if (lastNonDrawFourCard == null)
            {
                group.sendMessage("无法质疑: 没有上一张牌.")
                finishDraw(player)
                return
            }
            val valid = _firstSubmitDrawFourPlayer?.cards?.any { it.color == lastNonDrawFourCard?.color } == true ||
                    _firstSubmitDrawFourPlayer?.cards?.any { it.value == lastNonDrawFourCard?.value } == true
            if (valid) // doubt is valid
            {
                finishDraw(_firstSubmitDrawFourPlayer!!)
            }
            else
            {
                overlayCardNum += 2
                finishDraw(player)
            }
        }
        else
        {
            finishDraw(player)
        }
    }

    enum class GamingState {
        Gaming, WaitingDrawTwoOverlay, WaitingDrawFourOverlay, Doubting, Waiting
    }
}
