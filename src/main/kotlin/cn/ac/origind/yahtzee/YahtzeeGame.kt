package cn.ac.origind.yahtzee

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain


class YahtzeeGame(val subject: Group) {
    val players = mutableListOf<YahtzeePlayer>()
    val stage = GameStage.MATCHMAKING

    suspend fun start() {
        if (players.size <= 1) {
            subject.sendMessage("人数不够！")
            return
        }
        sendDices()
    }

    fun at(player: YahtzeePlayer) = At(subject[player.qq]!!)

    suspend fun sendDices() {
        subject.sendMessage(buildMessageChain {
            players.forEach {
                add(at(it))
                add("- " + it.dices.joinToString())
                add("\n")
            }
        })
    }
}