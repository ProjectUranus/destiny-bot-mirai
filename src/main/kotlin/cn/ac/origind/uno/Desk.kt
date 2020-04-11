package cn.ac.origind.uno

import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.function.Predicate
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Desk {
    var cardsPool: List<Card> = runBlocking { generateDefaultCards() }
    var cardsQueue = LinkedList<Card>()

    fun nextCard() =
        if (cardsQueue.isNotEmpty()) cardsQueue.poll() else cardsPool.random()

    @ExperimentalStdlibApi
    fun generateCards(n: Int) = buildList<Card> { for (i in 0 until n) add(nextCard()) }

    suspend fun nextCard(predicate: Predicate<Card>) = suspendCoroutine<Card> {
        while (true) {
            val card = cardsPool.random()
            if (predicate.test(card)) it.resume(card)
        }
    }
}
