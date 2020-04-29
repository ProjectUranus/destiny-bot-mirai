package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.data.DataStore
import cn.ac.origind.destinybot.data.User
import cn.ac.origind.destinybot.data.users
import cn.ac.origind.destinybot.response.bungie.DestinyItemPerksComponent
import cn.ac.origind.destinybot.response.bungie.DestinyMembershipQuery
import cn.ac.origind.destinybot.response.lightgg.ItemDefinition
import cn.ac.origind.destinybot.response.lightgg.ItemPerks
import cn.ac.origind.minecraft.MinecraftClientLogin
import cn.ac.origind.minecraft.MinecraftSpec
import cn.ac.origind.uno.initUnoGame
import cn.ac.origind.uno.unoGames
import com.uchuhimo.konf.Config
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.features.ServerResponseException
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.network.sockets.ConnectTimeoutException
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import kotlinx.coroutines.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.join
import net.mamoe.mirai.message.ContactMessage
import net.mamoe.mirai.message.MessagePacket
import net.mamoe.mirai.message.data.PlainText
import org.bson.Document
import org.litote.kmongo.KMongo
import org.litote.kmongo.aggregate
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

    val config = Config { addSpec(AccountSpec); addSpec(MinecraftSpec) }
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
    val activities = Object2ObjectOpenHashMap<String, String>()
    val lores = Object2ObjectOpenHashMap<String, String>()

    @ExperimentalStdlibApi
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        DataStore.init()
        bot.login()
        logger.info("Logged in")
        initUnoGame()
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
            content({ str -> activities.containsKey(str) }) {
                val collection = db.getCollection("DestinyActivityDefinition_chs")
                val doc = collection.findOne("""{"_id": "${activities[it]}"}""")
                reply(doc?.get("displayProperties", Document::class.java)?.getString("description") ?: "")
            }
            content({ str -> lores.containsKey(str) }) {
                val collection = db.getCollection("DestinyLoreDefinition_chs")
                val doc = collection.findOne("""{"_id": "${lores[it]}"}""")
                val displayProperties = doc?.get("displayProperties", Document::class.java)
                displayProperties?.let { display ->
                    reply("传奇故事：" + display.getString("name") + '\n' + display.getString("description"))
                }
            }
            case("花园世界") {
                reply("前往罗斯128b，与你的扭曲人伙伴们一起延缓凋零的复苏。")
            }
            case("小行星带") {
                reply("调查新近出现的bart遗迹，查明它的装配线生成概率。")
            }
            case("传奇故事") {
                val collection = db.getCollection("DestinyLoreDefinition_chs")
                val doc = collection.aggregate<Document>("""{${'$'}sample: { size: 1 }}""").firstOrNull()
                val displayProperties = doc?.get("displayProperties", Document::class.java)
                displayProperties?.let { display ->
                    reply("传奇故事：" + display.getString("name") + '\n' + display.getString("description"))
                }
            }
            case("/ping") {
                MinecraftClientLogin.statusAsync(subject)
            }
            matching(Regex("/ping (\\w(\\.)?)+(:\\d+)?")) {
                val address = get(PlainText).stringValue.removePrefix("/ping ")
                if (address.contains(':')) {
                    try {
                        MinecraftClientLogin.statusAsync(subject, address.substringBefore(':'), Integer.parseInt(address.substringAfter(':')))
                    } catch (e: NumberFormatException) {
                        reply(e.localizedMessage)
                    }
                } else {
                    MinecraftClientLogin.statusAsync(subject, address)
                }
            }
            case("咱…") {
                reply("咱…")
            }

            case("我的信息") {
                val user = users[sender.id]
                if (user?.destinyMembershipId == null) reply("你还没有绑定账号! 请搜索一个玩家并绑定之。")
                else {
                    replyProfile(user.destinyMembershipType, user.destinyMembershipId!!, this)
                }
            }
            matching(Regex("绑定 (\\d+)")) {
                val id = get(PlainText).stringValue.removePrefix("绑定 ").toLong()
                if (profileQuerys[sender.id]?.get(id.toInt() - 1) == null) {
                    // 直接绑定 ID
                    if (get(PlainText).stringValue.length < 8) reply("你输入的命运2 ID是不是稍微短了点？")
                    else {
                        val destinyMembership = getProfile(3, id.toString())?.profile?.data?.userInfo
                        if (destinyMembership == null) reply("无法找到该玩家，检查一下？")
                        else {
                            users.getOrPut(sender.id) { User(sender.id) }.apply {
                                destinyMembershipId = destinyMembership.membershipId
                                destinyMembershipType = destinyMembership.membershipType
                                destinyDisplayName = destinyMembership.displayName
                            }
                            DataStore.save()
                            reply("绑定 ${destinyMembership.displayName}(${destinyMembership.membershipId}) 到 ${sender.id} 成功。")
                        }
                    }
                } else {
                    // 绑定搜索序号
                    val result = profileQuerys[sender.id]!!
                    val index = id - 1
                    if (result.size < index + 1) reply("你的序号太大了点。")
                    val destinyMembership = result[index.toInt()]
                    try {
                        users.getOrPut(sender.id) { User(sender.id) }.apply {
                            destinyMembershipId = destinyMembership.membershipId
                            destinyMembershipType = destinyMembership.membershipType
                            destinyDisplayName = destinyMembership.displayName
                        }
                        DataStore.save()
                        reply("绑定 ${destinyMembership.displayName}(${destinyMembership.membershipId}) 到 ${sender.id} 成功。")
                    } catch (e: ServerResponseException) {
                        reply("获取详细信息时失败，请重试。\n${e.localizedMessage}")
                    }
                }
            }

            matching(Regex("\\d+")) {
                if (profileQuerys[sender.id].isNullOrEmpty())
                    return@matching
                val packet = this
                launch {
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
            matching(Regex("/ds item .+")) {
                val itemDefinitionCollection = db.getCollection("DestinyInventoryItemDefinition_chs")
                val document = itemDefinitionCollection.findOne("""{"displayProperties.name": "${get(PlainText).stringValue.removePrefix("/ds item ")}"}""")
                if (document == null) reply("无法找到该物品，请检查你的内容并用简体中文译名搜索。")
                else {
                    try {
                        val item = lightggGson.fromJson(document.toJson(), ItemDefinition::class.java)
                        val perks = getItemPerks(item._id!!)
                        println(perks)
                        replyPerks(item, perks, this)
                    } catch (e: NullPointerException) {
                        reply("无法找到该物品，请检查你的内容并用简体中文译名搜索。")
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
                    val result =
                        withContext(Dispatchers.Default) { searchUsers(criteria) }
                    val profiles =
                        withContext(Dispatchers.Default) { searchProfiles(criteria) }
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
                        appendln("请直接回复前面的序号来获取详细信息。")
                        appendln("或者，回复 绑定 [序号] 来将该用户绑定到你的 QQ 上。")
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
//            doudizhuGames()
            unoGames()
        }
    }

    suspend fun replyPerks(item: ItemDefinition, perks: ItemPerks, packet: ContactMessage) {
        packet.reply(buildString {
            appendln("信息来自 light.gg")
            appendln(item.displayProperties?.name + " " + item.itemTypeAndTierDisplayName)
            appendln(item.displayProperties?.description)
            appendln()
            append("Godroll(可能并不存在): ")
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