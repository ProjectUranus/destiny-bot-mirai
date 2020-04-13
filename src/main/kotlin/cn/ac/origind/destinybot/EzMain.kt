package cn.ac.origind.destinybot

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.join

data class Score(var x: Int = 0)

val map = hashMapOf<Long, Score>()

fun main() = runBlocking {
    val bot = Bot(1519989615L, "AnriNumber2")
    bot.login()
    bot.subscribeMessages {
        case("获取积分") {
            map[sender.id] = map.getOrDefault(sender.id, Score()).apply { x += 1000 }
            reply("你的积分是: ${map[sender.id]?.x}")
        }
        case("我需要机器人") {
        }

    }
    bot.join()
}