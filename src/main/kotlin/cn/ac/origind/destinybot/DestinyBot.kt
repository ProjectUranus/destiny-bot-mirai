package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.response.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.features.ServerResponseException
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.*
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.MessagePacket
import net.mamoe.mirai.message.data.PlainText
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.concurrent.ConcurrentHashMap
import java.util.Locale
import java.time.ZoneId

const val endpoint = "https://www.bungie.net/Platform"
const val key = "9654e41465f34fb6a7aea347abd5deeb"

val races = arrayOf("人类", "觉醒者", "EXO", "未知")
val classes = arrayOf("泰坦", "猎人", "术士", "未知")
val genders = arrayOf("男", "女", "未知")

object DestinyBot : PluginBase() {
    // Key: QQ
    val profileQuerys = ConcurrentHashMap<Long, List<DestinyMembershipQuery>>()

    // 用户在查询什么?
    val userQuerys = ConcurrentHashMap<Long, QueryType>()

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

    override fun onDisable() {
        client.close()
    }

    override fun onEnable() {
        subscribeMessages {
            this.subscriber(this.finding(Regex("\\d+")).filter) {
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
            this.subscriber(this.matching(Regex("/destiny \\d+")).filter) {
                replyProfile(3, message[PlainText].stringValue.removePrefix("/destiny "), this)
            }
            this.subscriber(this.startsWith("/destiny search ").filter) {
                val packet = this
                profileQuerys.remove(packet.sender.id)
                launch {
                    val criteria = packet.message[PlainText].removePrefix("/destiny search ").toString()
                    val result = searchUsers(criteria).await()
                    val profiles = searchProfiles(criteria).await()
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
            this.subscriber(this.startsWith("/tracker ").filter) {
                val packet = this
                launch {
                    val criteria = packet.message[PlainText].removePrefix("/tracker ").toString()
                    val result = searchProfiles(criteria).await()
                    packet.reply("搜索命运2玩家: $criteria")
                    if (result.isNullOrEmpty()) {
                        packet.reply("没有搜索到玩家，请检查你的搜索内容")
                        return@launch
                    }
                    packet.reply(buildString {
                        appendln("搜索到玩家: ")
                        result.forEachIndexed { index, profile ->
                            appendln("${index + 1}. ${profile.displayName}: https://destinytracker.com/destiny-2/profile/steam/${profile.membershipId}/overview")
                        }
                    })
                }
            }
        }
    }

    suspend fun bungieUserToDestinyUser(membershipId: String): DestinyMembershipQuery? = getDestinyProfiles(membershipId, 3).await()?.destinyMemberships?.firstOrNull()

    suspend fun replyProfile(membershipType: Int, membershipId: String, packet: MessagePacket<*, *>) {
        try {
            packet.reply("Tracker: https://destinytracker.com/destiny-2/profile/steam/${membershipId}/overview")
            val profile = getProfile(3, membershipId).await()
            if (profile == null)
                packet.reply("获取详细信息时失败，请重试。")
            val userProfile = profile?.profile?.data?.userInfo
            packet.reply(buildString {
                appendln("玩家: ${userProfile?.displayName}")
                appendln("ID: ${userProfile?.membershipId}")
            })
            packet.reply(buildString {
                profile?.characters?.data?.forEach { (id, character) ->
                    appendln("角色 $id：")
                    appendln("${classes[character.classType]} ${races[character.raceType]} ${genders[character.genderType]}")
                    appendln("光等: ${character.light}")
                    appendln("最后上线时间: ${formatter.format(Instant.parse(character.dateLastPlayed))}")
                    appendln("总游戏时间: ${character.minutesPlayedTotal}分钟")
                    appendln()
                }
            })
        } catch (e: ServerResponseException) {
            packet.reply("获取详细信息时失败，请重试。\n${e.localizedMessage}")
        }
    }

    override fun onLoad() {
        logger.info("Loaded Origind Plugin")
    }

    suspend fun getDestinyProfiles(membershipId: String, membershipType: Int) = async {
        client.get<GetMembershipsResponse>("$endpoint/User/GetMembershipsById/$membershipId/$membershipType/") {
            header("X-API-Key", key)
        }.Response
    }

    suspend fun searchUsers(criteria: String) = async {
        client.get<UserSearchResponse>("$endpoint/User/SearchUsers/?q=$criteria") {
            header("X-API-Key", key)
        }.Response
    }

    suspend fun searchProfiles(criteria: String) = async {
        client.get<DestinyProfileSearchResponse>("$endpoint/Destiny2/SearchDestinyPlayer/TigerSteam/$criteria/ ") {
            header("X-API-Key", key)
        }.Response
    }

    suspend fun getProfile(membershipType: Int, membershipId: String) = async {
        client.get<DestinyProfileResponse>("$endpoint/Destiny2/${membershipType}/Profile/${membershipId}/?components=Profiles%2CCharacters%2CProfileCurrencies") {
            header("X-API-Key", key)
        }.Response
    }
}