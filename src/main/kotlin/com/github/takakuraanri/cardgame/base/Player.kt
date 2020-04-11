package com.github.takakuraanri.cardgame.base

interface Player: MessageSender {
    var cards: MutableList<Card>
    val data: PlayerData
    var identity: Identity

    suspend fun sendCards()
    suspend fun requestCards(game: Game)
}

data class PlayerData(var name: String = "", var score: Int = 0)
