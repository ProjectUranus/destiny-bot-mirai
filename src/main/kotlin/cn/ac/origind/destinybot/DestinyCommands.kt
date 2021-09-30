package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.data.DataStore
import cn.ac.origind.destinybot.database.searchItemDefinitions
import cn.ac.origind.destinybot.exception.WeaponNotFoundException
import cn.ac.origind.destinybot.response.bungie.DestinyMembershipQuery
import io.ktor.client.features.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.MessageEventSubscribersBuilder
import net.mamoe.mirai.message.data.content
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

val profileQuerys = ConcurrentHashMap<Long, List<DestinyMembershipQuery>>()

fun MessageEventSubscribersBuilder.destinyCommands() {
    case("我的信息") {
        val user = DataStore[sender.id]
        if (user.destinyMembershipId.isEmpty()) {
            if (subject is Group && (!(subject as Group).contains(3320645904))) // CY BOT
                reply("你还没有绑定账号! 请搜索一个玩家并绑定之。")
        } else {
            DestinyBot.replyProfile(user.destinyMembershipType, user.destinyMembershipId, this)
        }
    }
    matching(Regex("绑定 (\\d+)")) {
        val content = message.content
        val id = content.removePrefix("绑定 ").toLong()
        if (profileQuerys[sender.id]?.get(id.toInt() - 1) == null) {

            // 直接绑定 ID
            if (content.length < 8) reply("你输入的命运2 ID是不是稍微短了点？")
            else {
                val destinyMembership = if (id.toString().startsWith("7656")) {
                    getMembershipFromHardLinkedCredential(id.toString())
                } else {
                    getProfile(3, id.toString())?.profile?.data?.userInfo
                }

                if (destinyMembership == null) reply("无法找到该玩家，检查一下？")
                else {
                    DataStore[sender.id].apply {
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
                DataStore[sender.id].apply {
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
        bot.launch {
            val result = profileQuerys[packet.sender.id]!!
            val index = packet.message.content.toInt() - 1
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
    startsWith("perk") {
        if (it.isBlank()) return@startsWith
        for (item in searchItemDefinitions(it)) {
            bot.launch {
                try {
                    val perks = getItemPerks(item._id!!)
                    DestinyBot.replyPerks(item, perks, this@startsWith)
                } catch (e: WeaponNotFoundException) {
                    reply(e.message ?: "")
                } catch (e: ItemNotFoundException) {
                    reply("搜索失败: ${e.localizedMessage}, 正在尝试其他方式")
                } catch (e: Exception) {
                    reply("搜索失败：${e.localizedMessage}, 正在尝试其他方式")
                }
            }
        }
    }
    matching(Regex("/j \\d+")) {
        val id = message.content.removePrefix("/j ")
        val query = getMembershipFromHardLinkedCredential(id)
        if (query == null) {
            reply("没有找到用户，请检查你的输入。")
            return@matching
        }
        DestinyBot.replyProfile(query.membershipType, query.membershipId, this)
    }
    matching(Regex("/你给翻译翻译 \\d+")) {
        val id = message.content.removePrefix("/你给翻译翻译 ")
        val query = getMembershipFromHardLinkedCredential(id)
        if (query == null) {
            reply("你不叫马邦德，我叫马邦德")
            return@matching
        }
        reply("好嘞。\n你的棒鸡ID：${query.membershipId}")
    }
    matching(Regex("/ds \\d+")) {
        val id = message.content.removePrefix("/ds ")
        if (id.startsWith("765")) {
            val query = getMembershipFromHardLinkedCredential(id)
            if (query == null) {
                reply("没有找到用户，请检查你的输入。")
                return@matching
            }
            DestinyBot.replyProfile(query.membershipType, query.membershipId, this)
        } else {
            DestinyBot.replyProfile(3, id, this)
        }
    }
    startsWith("/ds search ") {
        val packet = this
        profileQuerys.remove(packet.sender.id)
        subject.launch {
            val criteria = packet.message.content.removePrefix("/ds search ")
            val result =
                withContext(Dispatchers.Default) { searchUsersInternal(criteria) }
            val profiles =
                withContext(Dispatchers.Default) { searchProfiles(criteria) }
            packet.reply("搜索命运2玩家: $criteria")
            if (result.isNullOrEmpty() && profiles.isNullOrEmpty()) {
                packet.reply("没有搜索到玩家，请检查你的搜索内容")
                return@launch
            }

            // Filter Destiny 2 players
            val players = mutableSetOf<DestinyMembershipQuery>()
            players.addAll(profiles)
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
                appendLine("搜索到玩家: ")
                players.forEachIndexed { index, profile ->
                    appendLine("${index + 1}. ${profile.displayName}: ...${profile.membershipId.takeLast(3)}")
                }
                appendLine("请直接回复前面的序号（是1 2 3 不是375 668 451等等等）来获取详细信息。")
                appendLine("或者，回复 绑定 [序号] 来将该用户绑定到你的 QQ 上。")
            })
        }
    }
}
