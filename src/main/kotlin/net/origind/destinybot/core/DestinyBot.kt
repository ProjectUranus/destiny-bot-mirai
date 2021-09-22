package net.origind.destinybot.core

import cn.ac.origind.destinybot.DestinyBot
import cn.ac.origind.destinybot.config.AccountSpec
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import com.uchuhimo.konf.Config
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.utils.BotConfiguration
import org.litote.kmongo.KMongo
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class DestinyBot {
    val bot: Bot

    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(Locale.PRC).withZone(ZoneId.systemDefault())
    val mongoClient: MongoClient
    val db: MongoDatabase
    val config: Config

    constructor() {
        mongoClient = KMongo.createClient()
        db = DestinyBot.mongoClient.getDatabase("destiny2")
        config = Config {
            addSpec(cn.ac.origind.destinybot.config.AccountSpec)
            addSpec(cn.ac.origind.minecraft.MinecraftSpec)
            addSpec(cn.ac.origind.destinybot.config.DictSpec)
            addSpec(cn.ac.origind.destinybot.config.AppSpec)
            addSpec(cn.ac.origind.destinybot.config.BilibiliSpec)
        }.from.json.watchFile("config.json", delayTime = 15)

        bot = BotFactory.newBot(DestinyBot.config[AccountSpec.qq], DestinyBot.config[AccountSpec.password]) {
            fileBasedDeviceInfo()
            protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE
        }
    }
}
