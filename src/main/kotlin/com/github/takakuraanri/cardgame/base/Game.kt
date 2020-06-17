package com.github.takakuraanri.cardgame.base

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText

const val SIZE_PLAYER = 3
const val BASE_SCORE = 1000

enum class GameState {
    STOP, WAIT_LANDLORD, RUNNING
}

abstract class Game: MessageSender {
    /**
     * Players on the tabletop
     */
    val players = mutableListOf<Player>()

    /**
     * Current card waiting player
     */
    val currentPlayer: Player
        get() = players[currentIndex]

    /**
     * Index of current player
     */
    var currentIndex = 0
    var passed = 2
    val lordCards = mutableListOf<Card>()
    var lastCards = listOf<Card>()
    var lastRule: Rule? = null
    var state = GameState.STOP

    /**
     * Amplifier of score
     */
    var amplifier = 1.0

    fun addPlayer(player: Player) {
        if (players.size + 1 <= SIZE_PLAYER)
            players += player
        else
            throw PlayerFullException()
    }

    fun removePlayer(player: Player) = players.remove(player)

    suspend fun init(debug: Boolean = false) {
        if (players.size != SIZE_PLAYER)
            throw PlayerNotEnoughException()
        players.shuffle()
        if (!debug) {
            val cards = newShuffledCardSet()
            val cardsPerPlayer = (cards.size - SIZE_PLAYER) / SIZE_PLAYER
            for (i in 0 until SIZE_PLAYER) {
                for (j in i * cardsPerPlayer until i * cardsPerPlayer + cardsPerPlayer) {
                    players[i].cards.add(cards[j])
                }
                players[i].cards.sortWith(Card.Comparator)
                players[i].sendCards()
            }
            lordCards.addAll(cards.subList(cards.size - SIZE_PLAYER - 1, cards.size - 1))
        }
        state = GameState.WAIT_LANDLORD

        sendMessage(mention(currentPlayer, "你是否要叫地主?"))
    }

    suspend fun takeLandlord(taken: Boolean) {
        if (taken) {
            sendMessage(mention(currentPlayer, "为地主，底牌为$lordCards"))
            currentPlayer.identity = Identity.LANDLORD
            currentPlayer.cards.addAll(lordCards)
            currentPlayer.cards.sortWith(Card.Comparator)
            currentPlayer.sendCards()
            start()
        }
        else if (!taken && currentIndex == SIZE_PLAYER - 1) {
            sendMessage(PlainText("无人叫地主，游戏结束。"))
            stop()
        }
        else {
            rollPlayer()
            sendMessage(mention(currentPlayer, "你是否要叫地主?"))
        }
    }

    suspend fun pass() {
        if (passed == 2) {
            sendMessage(mention(currentPlayer, " 过过过过你\uD83C\uDFC7呢，该你出了"))
        } else {
            sendMessage(mention(currentPlayer, " 过牌"))
            passed++
            lastCards = listOf()
            rollPlayer()

            if (passed == 2) {
                lastRule = null
                lastCards = emptyList()
            }
            requestCard()
        }
    }

    abstract fun mention(currentPlayer: Player, s: String): Message

    suspend fun submitCards(cards: List<Card>) {
        if (cards.isEmpty()) return

        println("尝试出牌: $cards, ${cards.toCardGroups()}")

        if (!currentPlayer.cards.containsAllWithNum(cards) || cards.isEmpty()) {
            println("你不能出这些牌!")
            return
        }

        val rule: Rule? = flow {
            rules.forEach {
                if (it.validate(currentPlayer, lastRule, cards, lastCards)) {
                    emit(it)
                }
            }
        }.firstOrNull()
        println(rule)
        if (rule == null) {
            sendMessage(PlainText("你出的牌无法匹配任何规则，请重新出牌！"))
            return
        } else {
            sendMessage(
                mention(
                    currentPlayer,
                    " 出牌 " + cards.sortedWith(Card.Comparator)
                )
            )
            passed = 0
            currentPlayer.cards.removeAll(cards)

            if (currentPlayer.cards.isEmpty())
                currentPlayer.win()

            lastCards = cards
            lastRule = rule
            currentPlayer.cards.sortWith(Card.Comparator)
            currentPlayer.sendCards()
            rollPlayer()
            requestCard()
            return
        }
    }

    suspend fun Player.win() {
        if (this.identity == Identity.LANDLORD) {
            val score = (BASE_SCORE * amplifier).toInt()
            sendMessage(
                PlainText(
                    """
                地主赢！
                """.trimIndent())
            )
            stop()
        } else {
            sendMessage(
                PlainText(
                    """
                农民赢！
                """.trimIndent()
                )
            )
        }
    }

    open suspend fun requestCard() {
        currentPlayer.requestCards(this)
    }


    fun rollPlayer() {
        currentIndex = if (currentIndex == SIZE_PLAYER - 1) 0 else currentIndex + 1
    }

    suspend fun start() {
        if (players.size == 3) {
            state = GameState.RUNNING
            requestCard()
        } else {
            sendMessage(PlainText("人数不够！目前只有 ${players.size} 人。"))
        }
    }

    open fun stop() {
        lordCards.clear()
        lastCards = emptyList()
        state = GameState.STOP
    }

    fun reset() {
        players.clear()
        currentIndex = 0
        lordCards.clear()
    }

    override fun toString(): String {
        return "Game(players=$players, currentIndex=$currentIndex, lordCards=$lordCards)"
    }
}
