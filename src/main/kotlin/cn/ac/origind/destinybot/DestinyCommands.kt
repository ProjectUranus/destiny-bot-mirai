package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.config.DictSpec
import cn.ac.origind.destinybot.data.DataStore
import cn.ac.origind.destinybot.data.User
import cn.ac.origind.destinybot.data.users
import cn.ac.origind.destinybot.exception.joinToString
import cn.ac.origind.destinybot.response.bungie.DestinyMembershipQuery
import cn.ac.origind.destinybot.response.lightgg.ItemDefinition
import io.ktor.client.features.ServerResponseException
import io.ktor.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.*
import net.mamoe.mirai.event.MessagePacketSubscribersBuilder
import net.mamoe.mirai.message.data.PlainText
import org.bson.Document
import org.litote.kmongo.aggregate
import org.litote.kmongo.findOne
import java.util.concurrent.ConcurrentHashMap

val profileQuerys = ConcurrentHashMap<Long, List<DestinyMembershipQuery>>()

fun MessagePacketSubscribersBuilder.destinyCommands() {
    case("传奇故事") {
        val collection = DestinyBot.db.getCollection("DestinyLoreDefinition_chs")
        val doc = collection.aggregate<Document>("""{${'$'}sample: { size: 1 }}""").firstOrNull()
        val displayProperties = doc?.get("displayProperties", Document::class.java)
        displayProperties?.let { display ->
            reply("传奇故事：" + display.getString("name") + '\n' + display.getString("description"))
        }
    }
    case("我的信息") {
        val user = users[sender.id]
        if (user?.destinyMembershipId == null) reply("你还没有绑定账号! 请搜索一个玩家并绑定之。")
        else {
            DestinyBot.replyProfile(user.destinyMembershipType, user.destinyMembershipId!!, this)
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
        GlobalScope.launch {
            val result = profileQuerys[packet.sender.id]!!
            val index = packet.message[PlainText].stringValue.toInt() - 1
            if (result.size < index + 1) return@launch
            val destinyMembership = result[index]
            try {
                DestinyBot.replyProfile(
                    destinyMembership.membershipType,
                    destinyMembership.membershipId,
                    packet
                )
            } catch (e: ServerResponseException) {
                packet.reply("获取详细信息时失败，请重试。\n${e.localizedMessage}")
            }
        }
    }
    matching(Regex("/ds item .+")) {
        val itemDefinitionCollection = DestinyBot.db.getCollection("DestinyInventoryItemDefinition_chs")
        var itemSearch = get(PlainText).stringValue.removePrefix("/ds item ")
        if (DestinyBot.searchToWeaponMap.containsKey(itemSearch)) itemSearch = DestinyBot.searchToWeaponMap[itemSearch]!!
        else {
            for ((weapon, alias) in DestinyBot.config[DictSpec.aliases]) {
                if (itemSearch.matches(Regex(alias))) {
                    DestinyBot.searchToWeaponMap[itemSearch] = weapon
                    itemSearch = weapon
                    break
                }
            }
        }

        val document = itemDefinitionCollection.findOne("""{"displayProperties.name": "${DestinyBot.searchToWeaponMap[itemSearch] ?: itemSearch}"}""")
        if (document == null) reply("无法找到该物品，请检查你的内容并用简体中文译名搜索。")
        else {
            try {
                val item = lightggGson.fromJson(document.toJson(), ItemDefinition::class.java)
                val perks = getItemPerks(item._id!!)
                DestinyBot.replyPerks(item, perks, this)
            } catch (e: Exception) {
                reply("搜索失败：" + e.joinToString())
            }
        }
    }
    matching(Regex("/ds \\d+")) {
        DestinyBot.replyProfile(3, message[PlainText].stringValue.removePrefix("/ds "), this)
    }
    startsWith("/ds search ") {
        val packet = this
        profileQuerys.remove(packet.sender.id)
        GlobalScope.launch {
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
                        val destinyMembership = DestinyBot.bungieUserToDestinyUser(profile.membershipId)
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
}