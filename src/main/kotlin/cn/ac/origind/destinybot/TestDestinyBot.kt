package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.config.DictSpec
import com.hankcs.hanlp.HanLP
import com.hankcs.hanlp.dictionary.CustomDictionary
import kotlinx.coroutines.runBlocking
import java.awt.Color

val normalColor = Color(255, 255, 255)
val pveColor = Color(87, 145, 190)
val pvpColor = Color(245, 91, 91)
val godColor = Color(227, 202, 87)

fun main(args: Array<String>) {
    runBlocking {
        val userAliases = DestinyBot.config[DictSpec.userAliases]
        userAliases.values.flatMap { it.asIterable() }.forEach {
            CustomDictionary.add(it, "nr 1")
        }
        val sentense = HanLP.parseDependency("櫂人姐姐kkp")
        println(sentense)
        println(HanLP.segment("櫂人姐姐kkp"))
    }
}
