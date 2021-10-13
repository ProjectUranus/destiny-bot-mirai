package net.origind.destinybot.features.yahtzee


class YahtzeeGame() {
    val players = mutableListOf<YahtzeePlayer>()
    val stage = GameStage.MATCHMAKING

    suspend fun start() {
        if (players.size <= 1) {
            return
        }
        sendDices()
    }

    suspend fun sendDices() {
    }
}
