package cn.ac.origind.destinybot

import cn.ac.origind.pricechallange.searchChallengeProduct
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test

class TestBot {
    @Test
    fun test() = runBlocking {
        searchChallengeProduct("k30s").collect {
            println(it)
        }
    }
}