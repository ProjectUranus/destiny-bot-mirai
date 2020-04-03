package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.response.bungie.DestinyItemPerksComponent
import cn.ac.origind.destinybot.response.bungie.DestinyMembershipQuery
import com.uchuhimo.konf.Config
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.features.ServerResponseException
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.MessagePacket
import net.mamoe.mirai.message.data.PlainText
import org.bson.Document
import org.litote.kmongo.KMongo
import org.litote.kmongo.findOne
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set


val races = arrayOf("人类", "觉醒者", "EXO", "未知")
val classes = arrayOf("泰坦", "猎人", "术士", "未知")
val genders = arrayOf("男", "女", "未知")

object DestinyBot {
    init {
        System.setProperty("org.litote.mongo.test.mapping.service", "org.litote.kmongo.jackson.JacksonClassMappingTypeService")
        System.setProperty("org.litote.mongo.mapping.service", "org.litote.kmongo.jackson.JacksonClassMappingTypeService")
    }

    val logger = LoggerFactory.getLogger("DestinyBot")

    // Key: QQ
    val profileQuerys = ConcurrentHashMap<Long, List<DestinyMembershipQuery>>()

    // 用户在查询什么?
    val userQuerys = ConcurrentHashMap<Long, QueryType>()

    val config = Config { addSpec(AccountSpec) }
        .from.json.file("config.json")
        .from.env()
        .from.systemProperties()
    val bot by lazy { Bot(config[AccountSpec.qq], config[AccountSpec.password]) }

    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(Locale.PRC).withZone(ZoneId.systemDefault());

    val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer()
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

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        bot.login()
        logger.info("Logged in")
        bot.subscribeMessages()
        bot.join()
        client.close()
        bot.close()
    }

    private fun Bot.subscribeMessages() {
        subscribeMessages {
            finding(Regex("\\d+")) {
                val packet = this
                launch {
                    if (profileQuerys[packet.sender.id].isNullOrEmpty())
                        return@launch
                    val result = profileQuerys[packet.sender.id]!!
                    val index = packet.message[PlainText].stringValue.toInt() - 1
                    if (result.size < index + 1) return@launch
                    val destinyMembership = result[index]
                    try {
                        replyProfile(destinyMembership.membershipType, destinyMembership.membershipId, packet)
                    } catch (e: ServerResponseException) {
                        packet.reply("获取详细信息时失败，请重试。\n${e.localizedMessage}")
                    }
                }
            }
            matching(Regex("/ds \\d+")) {
                replyProfile(3, message[PlainText].stringValue.removePrefix("/ds "), this)
            }
            startsWith("/ds search ") {
                val packet = this
                profileQuerys.remove(packet.sender.id)
                launch {
                    val criteria = packet.message[PlainText].removePrefix("/ds search ").toString()
                    val result = async { searchUsers(criteria) }.await()
                    val profiles = async { searchProfiles(criteria) }.await()
                    packet.reply("搜索命运2玩家: $criteria")
                    if (result.isNullOrEmpty() && profiles.isNullOrEmpty()) {
                        packet.reply("没有搜索到玩家，请检查你的搜索内容")
                        return@launch
                    }

                    // Filter Destiny 2 players
                    val players = mutableSetOf<DestinyMembershipQuery>()
                    players.addAll(profiles ?: emptyList())
                    result.map { profile ->
                        launch {
                            try {
                                val destinyMembership = bungieUserToDestinyUser(profile.membershipId)
                                if (destinyMembership != null) {
                                    players.add(destinyMembership)
                                }
                            } catch (e: ConnectTimeoutException) {
                                packet.reply("尝试获取玩家 ${profile.steamDisplayName ?: profile.displayName} 信息时超时。")
                            }
                        }
                    }.joinAll()
                    profileQuerys[packet.sender.id] = players.toList()
                    packet.reply(buildString {
                        appendln("搜索到玩家: ")
                        players.forEachIndexed { index, profile ->
                            appendln("${index + 1}. ${profile.displayName}: ...${profile.membershipId.takeLast(3)}")
                        }
                        appendln("请直接回复编号来获取详细信息。")
                    })
                }
            }
            startsWith("/tracker ") {
                val packet = this
                launch {
                    val criteria = packet.message[PlainText].removePrefix("/tracker ").toString()
                    val result = async { searchTrackerProfiles(criteria) }.await()
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
        }
    }

    suspend fun bungieUserToDestinyUser(membershipId: String): DestinyMembershipQuery? = withContext(Dispatchers.IO) { getDestinyProfiles(membershipId, 3) }?.destinyMemberships?.firstOrNull()

    suspend fun replyProfile(membershipType: Int, membershipId: String, packet: MessagePacket<*, *>) {
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
            packet.reply(buildString {
                profile?.characters?.data?.forEach { (id, character) ->
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
                }
            })
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