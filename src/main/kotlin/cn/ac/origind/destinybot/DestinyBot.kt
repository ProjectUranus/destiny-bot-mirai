package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.config.AccountSpec
import cn.ac.origind.destinybot.config.DictSpec
import cn.ac.origind.destinybot.data.DataStore
import cn.ac.origind.destinybot.image.toImage
import cn.ac.origind.destinybot.response.QueryType
import cn.ac.origind.destinybot.response.bungie.DestinyItemPerksComponent
import cn.ac.origind.destinybot.response.bungie.DestinyMembershipQuery
import cn.ac.origind.destinybot.response.lightgg.ItemDefinition
import cn.ac.origind.destinybot.response.lightgg.ItemPerks
import cn.ac.origind.destinybot.response.lightgg.PerkType
import cn.ac.origind.minecraft.MinecraftSpec
import cn.ac.origind.minecraft.initMinecraftVersion
import cn.ac.origind.minecraft.minecraftCommands
import cn.ac.origind.uno.initUnoGame
import cn.ac.origind.uno.unoGames
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.uchuhimo.konf.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.TypeInfo
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.features.ServerResponseException
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.JsonSerializer
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.utils.io.core.Input
import io.ktor.utils.io.core.readText
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
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
import org.bson.Document
import org.litote.kmongo.KMongo
import org.litote.kmongo.findOne
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.component1
import kotlin.collections.component2


val races = arrayOf("人类", "觉醒者", "EXO", "未知")
val classes = arrayOf("泰坦", "猎人", "术士", "未知")
val genders = arrayOf("男", "女", "未知")

val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

object DestinyBot {
    init {
        System.setProperty("org.litote.mongo.test.mapping.service", "org.litote.kmongo.jackson.JacksonClassMappingTypeService")
        System.setProperty("org.litote.mongo.mapping.service", "org.litote.kmongo.jackson.JacksonClassMappingTypeService")
    }

    val logger = LoggerFactory.getLogger("DestinyBot")

    // 用户在查询什么?
    val userQuerys = ConcurrentHashMap<Long, QueryType>()

    val config = Config { addSpec(AccountSpec); addSpec(MinecraftSpec); addSpec(DictSpec) }
        .from.json.file("config.json")
        .from.env()
        .from.systemProperties()
    val bot by lazy { Bot(config[AccountSpec.qq], config[AccountSpec.password]) {
        fileBasedDeviceInfo()
    } }

    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(Locale.PRC).withZone(ZoneId.systemDefault());

    val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = object : JsonSerializer {
                private val backend = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

                override fun read(type: TypeInfo, body: Input): Any {
                    val text = body.readText()
                    return backend.adapter<Any>(type.reifiedType).fromJson(text)!!
                }

                override fun write(data: Any, contentType: ContentType): OutgoingContent =
                    TextContent(backend.adapter(data.javaClass).toJson(data), contentType)
            }
        }
        engine {
            endpoint {
                /**
                 * Maximum number of requests for a specific endpoint route.
                 */
                maxConnectionsPerRoute = 100

                /**
                 * Max size of scheduled requests per connection(pipeline queue size).
                 */
                pipelineMaxSize = 20

                /**
                 * Max number of milliseconds to keep iddle connection alive.
                 */
                keepAliveTime = 5000

                /**
                 * Number of milliseconds to wait trying to connect to the server.
                 */
                connectTimeout = 2000

                /**
                 * Maximum number of attempts for retrying a connection.
                 */
                connectRetryAttempts = 2
            }
        }
    }

    val mongoClient = KMongo.createClient()
    val db = mongoClient.getDatabase("destiny2")
    val activities = Object2ObjectOpenHashMap<String, String>()
    val lores = Object2ObjectOpenHashMap<String, String>()

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
            activities.putAll(collection.find().map { it.get("displayProperties", Document::class.java)?.getString("name") to it.getString("_id") })

            val loreCollection = db.getCollection("DestinyLoreDefinition_chs")
            lores.putAll(loreCollection.find().map { it.get("displayProperties", Document::class.java)?.getString("name") to it.getString("_id") })
            println(activities.keys.toString())
        }
        bot.subscribeMessages()
        bot.join()
        client.close()
        bot.close()
    }

    @ExperimentalStdlibApi
    private fun Bot.subscribeMessages() {
        subscribeMessages {
            /*
            content(matching(Regex("[？?¿]*")).filter) {
                reply("你扣个锤子问号？")
            }
            */
            content { str -> activities.containsKey(str) }.reply {
                if (it.isNullOrEmpty()) return@reply Unit
                val collection = db.getCollection("DestinyActivityDefinition_chs")
                val doc = collection.findOne("""{"_id": "${activities[it]}"}""")
                doc?.get("displayProperties", Document::class.java)?.getString("description") ?: ""
            }
            content { str -> lores.containsKey(str) }.reply {
                if (it.isNullOrEmpty()) return@reply Unit
                val collection = db.getCollection("DestinyLoreDefinition_chs")
                val doc = collection.findOne("""{"_id": "${lores[it]}"}""")
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
                        appendln("搜索到玩家: ")
                        result.forEachIndexed { index, profile ->
                            appendln("${index + 1}. ${profile.platformUserHandle}: https://destinytracker.com/destiny-2/profile/${profile.platformSlug}/${profile.platformUserIdentifier}/overview")
                        }
                    })
                }
            }
//            doudizhuGames()
            destinyCommands()
            unoGames()
            minecraftCommands()
        }
    }

    suspend fun replyPerks(item: ItemDefinition, perks: ItemPerks, packet: MessageEvent) {
        packet.reply(item.toImage(perks).upload(packet.subject))
        packet.reply(buildMessageChain {
            val barrels = perks.all.filter { it.type == PerkType.BARREL }
            val magazines = perks.all.filter { it.type == PerkType.MAGAZINE }
            val perk1 = perks.all.filter { it.type == PerkType.PERK1 }
            val perk2 = perks.all.filter { it.type == PerkType.PERK2 }
            add(buildString {
                appendln("信息来自 light.gg")
                appendln(item.displayProperties?.name + " " + item.itemTypeAndTierDisplayName)
                appendln(item.displayProperties?.description)
                appendln()
                append("官Roll(可能并不存在): ")
                appendln(perks.curated.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
                append("社区精选 Perk: ")
                appendln(perks.favorite.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
                append("PvP Perk: ")
                appendln(perks.pvp.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
                append("PvE Perk: ")
                appendln(perks.pve.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
                append("其他 Perk: ")
                append(perks.normal.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
            })
        })
    }

    suspend fun bungieUserToDestinyUser(membershipId: String): DestinyMembershipQuery? = withContext(Dispatchers.IO) { getDestinyProfiles(membershipId, 3) }?.destinyMemberships?.firstOrNull()

    suspend fun replyProfile(membershipType: Int, membershipId: String, packet: MessageEvent) {
        try {
            packet.reply("Tracker: https://destinytracker.com/destiny-2/profile/steam/${membershipId}/overview")
            val profile = withContext(Dispatchers.IO) { getProfile(3, membershipId) }
            if (profile == null)
                packet.reply("获取详细信息时失败，请重试。")
            val userProfile = profile?.profile?.data?.userInfo
            packet.reply(buildString {
                appendln("玩家: ${userProfile?.displayName}")
                appendln("ID: ${userProfile?.membershipId}")
            })
            val perks = mutableListOf<Pair<String, DestinyItemPerksComponent>>()
            val itemDefinitionCollection = db.getCollection("DestinyInventoryItemLiteDefinition_chs")
            val perkCollection = db.getCollection("DestinySandboxPerkDefinition_chs")
            packet.sendImage(
                profile?.characters?.data?.map { (id, character) ->
                    /*
                    val detail = getCharacter(membershipType, membershipId, id)
                    appendln("角色 $id：")
                    appendln("${classes[character.classType]} ${races[character.raceType]} ${genders[character.genderType]}")
                    appendln("光等: ${character.light}")
                    appendln("最后上线时间: ${formatter.format(Instant.parse(character.dateLastPlayed))}")
                    appendln("总游戏时间: ${character.minutesPlayedTotal}分钟")
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
                    appendln()
                     */
                    character
                }?.toImage()!!
            )
            packet.reply(buildString {
                for ((name, perkList) in perks) {
                    append(name).append(": ")
                    for (perk in perkList.perks) {
                        val document = perkCollection.findOne("""{"_id":"${perk.perkHash}"}""")
                        append(document?.get("displayProperties", Document::class.java)?.getString("name") ?: "")
                        append(" ")
                    }
                    appendln()
                }
            })
        } catch (e: ServerResponseException) {
            packet.reply("获取详细信息时失败，请重试。\n${e.localizedMessage}")
        }
    }
}
