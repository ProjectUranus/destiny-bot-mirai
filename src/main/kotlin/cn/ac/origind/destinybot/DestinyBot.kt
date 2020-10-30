package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.config.AccountSpec
import cn.ac.origind.destinybot.config.AppSpec
import cn.ac.origind.destinybot.config.DictSpec
import cn.ac.origind.destinybot.data.DataStore
import cn.ac.origind.destinybot.debug.LatencyEventListener
import cn.ac.origind.destinybot.image.toImage
import cn.ac.origind.destinybot.response.QueryType
import cn.ac.origind.destinybot.response.bungie.DestinyMembershipQuery
import cn.ac.origind.destinybot.response.lightgg.ItemDefinition
import cn.ac.origind.destinybot.response.lightgg.ItemPerks
import cn.ac.origind.minecraft.MinecraftSpec
import cn.ac.origind.minecraft.curseForgeCommands
import cn.ac.origind.minecraft.initMinecraftVersion
import cn.ac.origind.minecraft.minecraftCommands
import cn.ac.origind.pricechallange.priceChallengeCommands
import cn.ac.origind.uno.initUnoGame
import cn.ac.origind.uno.unoGames
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mojang.brigadier.CommandDispatcher
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.uchuhimo.konf.Config
import io.ktor.client.features.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.join
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.upload
import net.mamoe.mirai.utils.toExternalImage
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.bson.Document
import org.litote.kmongo.KMongo
import org.litote.kmongo.findOne
import org.slf4j.LoggerFactory
import java.io.File
import java.net.InetSocketAddress
import java.net.Proxy
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.collections.component1
import kotlin.collections.component2


val races = arrayOf("人类", "觉醒者", "EXO", "未知")
val classes = arrayOf("泰坦", "猎人", "术士", "未知")
val genders = arrayOf("男", "女", "未知")

val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

val client = OkHttpClient.Builder()
    .cache(Cache(directory = File("web_cache"), maxSize = 10L * 1024L * 1024L))
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .retryOnConnectionFailure(true)
    .followRedirects(true)
    .followSslRedirects(true)
    .proxy(Proxy(Proxy.Type.SOCKS, InetSocketAddress("127.0.0.1", 1080)))
    .callTimeout(10, TimeUnit.SECONDS)
    .eventListener(LatencyEventListener())
    .build()

val rawClient = OkHttpClient.Builder()
    .cache(Cache(directory = File("web_cache"), maxSize = 10L * 1024L * 1024L))
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .retryOnConnectionFailure(true)
    .followRedirects(true)
    .followSslRedirects(true)
    .callTimeout(10, TimeUnit.SECONDS)
    .eventListener(LatencyEventListener())
    .build()

object DestinyBot {
    init {
        System.setProperty("org.litote.mongo.test.mapping.service", "org.litote.kmongo.jackson.JacksonClassMappingTypeService")
        System.setProperty("org.litote.mongo.mapping.service", "org.litote.kmongo.jackson.JacksonClassMappingTypeService")
    }

    val logger = LoggerFactory.getLogger("DestinyBot")

    // 用户在查询什么?
    val userQueries = ConcurrentHashMap<Long, QueryType>()

    val config = Config {
        addSpec(AccountSpec)
        addSpec(MinecraftSpec)
        addSpec(DictSpec)
        addSpec(AppSpec)
    }.from.json.watchFile("config.json", delayTime = 15)
    val bot by lazy { Bot(config[AccountSpec.qq], config[AccountSpec.password]) {
        fileBasedDeviceInfo()
    } }

    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(Locale.PRC).withZone(ZoneId.systemDefault())

    val dispatcher = CommandDispatcher<MessageEvent>()
    val mongoClient = KMongo.createClient()
    val     db = mongoClient.getDatabase("destiny2")
    val activities = hashMapOf<String, String>()
    val lores = hashMapOf<String, String>()
    val server = DestinyBotServer()

    @ExperimentalStdlibApi
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        DataStore.init()
        bot.login()
        logger.info("Logged in")
        initUnoGame()
        initMinecraftVersion()
        withContext(Dispatchers.Default) {
            val collection = db.getCollection("DestinyActivityDefinition_chs")
            activities.putAll(collection.find().map { it.get("displayProperties", Document::class.java)?.getString("name")!! to it.getString("_id") })

            val loreCollection = db.getCollection("DestinyLoreDefinition_chs")
            lores.putAll(loreCollection.find().map { it.get("displayProperties", Document::class.java)?.getString("name")!! to it.getString("_id") })
            println(activities.keys.toString())
        }
        bot.subscribeMessages()
        bot.join()
        bot.close()
    }

    fun registerCommands() {
    }

    @ExperimentalStdlibApi
    private fun Bot.subscribeMessages() {
        subscribeMessages {
            /*
            content(matching(Regex("[？?¿]*")).filter) {
                reply("你扣个锤子问号？")
            }
            */
            content { str -> str.startsWith('/') && activities.containsKey(str.removePrefix("/")) }.reply {
                if (it.isEmpty()) return@reply Unit
                val collection = db.getCollection("DestinyActivityDefinition_chs")
                val doc = collection.findOne("""{"_id": "${activities[it.removePrefix("/")]}"}""")
                doc?.get("displayProperties", Document::class.java)?.getString("description") ?: ""
            }
            content { str -> str.startsWith("传奇故事 ") && lores.containsKey(str.removePrefix("传奇故事 ")) }.reply {
                if (it.isEmpty()) return@reply Unit
                val collection = db.getCollection("DestinyLoreDefinition_chs")
                val doc = collection.findOne("""{"_id": "${lores[it.removePrefix("传奇故事 ")]}"}""")
                val displayProperties = doc?.get("displayProperties", Document::class.java)
                displayProperties?.let { display ->
                    "传奇故事：" + display.getString("name") + '\n' + display.getString("description")
                }
            }
            case("花园世界") {
                reply("前往罗斯128b，与你的扭曲人伙伴们一起延缓凋零的复苏。")
            }
            case("小行星带") {
                reply("调查新近出现的bart遗迹，查明它的装配线生成概率。")
            }
            case("咱…") {
                reply("咱…")
            }
            startsWith("/tracker ") {
                val packet = this
                launch {
                    val criteria = it.removePrefix("/tracker ")
                    val result = withContext(Dispatchers.Default) {
                        searchTrackerProfiles(criteria)
                    }
                    packet.reply("搜索Tracker上的命运2玩家: $criteria")
                    if (result.isNullOrEmpty()) {
                        packet.reply("没有搜索到玩家，请检查你的搜索内容")
                        return@launch
                    }
                    packet.reply(buildString {
                        appendLine("搜索到玩家: ")
                        result.forEachIndexed { index, profile ->
                            appendLine("${index + 1}. ${profile.platformUserHandle}: https://destinytracker.com/destiny-2/profile/${profile.platformSlug}/${profile.platformUserIdentifier}/overview")
                        }
                    })
                }
            }
//            doudizhuGames()
            configCommands()
            destinyCommands()
            unoGames()
            minecraftCommands()
            curseForgeCommands()
            priceChallengeCommands()
        }
    }

    suspend fun replyPerks(item: ItemDefinition, perks: ItemPerks, packet: MessageEvent) {
        packet.reply(item.toImage(perks).upload(packet.subject))
        packet.reply(buildMessageChain {
            add(buildString {
                appendLine("信息来自 light.gg")
                appendLine(item.displayProperties?.name + " " + item.itemTypeAndTierDisplayName)
                appendLine(item.displayProperties?.description)
                appendLine()
                append("官Roll(可能不掉落): ")
                appendLine(perks.curated.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
                if (perks.favorite.isNotEmpty()) {
                    append("社区精选 Perk: ")
                    appendLine(perks.favorite.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
                }
                if (perks.pvp.isNotEmpty()) {
                    append("PvP Perk: ")
                    appendLine(perks.pvp.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
                }
                if (perks.pve.isNotEmpty()) {
                    append("PvE Perk: ")
                    appendLine(perks.pve.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
                }
                if (perks.normal.isNotEmpty()) {
                    append("其他 Perk: ")
                    append(perks.normal.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
                }
            })
        })
    }

    suspend fun bungieUserToDestinyUser(membershipId: String): DestinyMembershipQuery? = withContext(Dispatchers.IO) { getDestinyProfiles(membershipId, 3) }?.destinyMemberships?.firstOrNull()

    suspend fun replyProfile(membershipType: Int, membershipId: String, packet: MessageEvent) {
        try {
            packet.reply(buildString {
                appendLine("Tracker: https://destinytracker.com/destiny-2/profile/steam/${membershipId}/overview")
                appendLine("Braytech: https://braytech.org/3/${membershipId}")
                append("Raid 报告: https://raid.report/pc/${membershipId}")
            })
            val profile = withContext(Dispatchers.IO) {
                getProfile(3, membershipId)
            }
            if (profile == null)
                packet.reply("获取详细信息时失败，请重试。")
            val userProfile = profile?.profile?.data?.userInfo
            packet.reply(buildMessageChain {
                add("玩家: ${userProfile?.displayName}\n")
                add("ID: ${userProfile?.membershipId}\n")
                add(packet.subject.uploadImage(profile?.characters?.data?.map { (id, character) ->
                    character
                }?.toImage()?.toExternalImage()!!))
            })

            /*
            val perks = mutableListOf<Pair<String, DestinyItemPerksComponent>>()
            val perkCollection = db.getCollection("DestinySandboxPerkDefinition_chs")
            val detail = getCharacter(membershipType, membershipId, id)
            appendLine("角色 $id：")
            appendLine("${classes[character.classType]} ${races[character.raceType]} ${genders[character.genderType]}")
            appendLine("光等: ${character.light}")
            appendLine("最后上线时间: ${formatter.format(Instant.parse(character.dateLastPlayed))}")
            appendLine("总游戏时间: ${character.minutesPlayedTotal}分钟")
            if (detail != null) {
                detail.equipment.data?.items?.forEachIndexed { index, it ->
                    val document = itemDefinitionCollection.findOne("""{"_id":"${it.itemHash}"}""")
                    val name = document?.get("displayProperties", Document::class.java)?.getString("name") ?: ""
                    if (detail.itemComponents.perks.data?.contains(it.itemInstanceId) == true) {
                        perks += name to (detail.itemComponents.perks.data?.get(it.itemInstanceId) as DestinyItemPerksComponent)
                    }
                    append(name)
                    append(" ")
                }
            }
            appendLine()
             */
            /*
            packet.reply(buildString {
                for ((name, perkList) in perks) {
                    append(name).append(": ")
                    for (perk in perkList.perks) {
                        val document = perkCollection.findOne("""{"_id":"${perk.perkHash}"}""")
                        append(document?.get("displayProperties", Document::class.java)?.getString("name") ?: "")
                        append(" ")
                    }
                    appendLine()
                }
            })*/
        } catch (e: ServerResponseException) {
            packet.reply("获取详细信息时失败，请重试。\n${e.localizedMessage}")
        }
    }
}
