package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.DestinyBot.config
import cn.ac.origind.destinybot.config.AppSpec
import cn.ac.origind.destinybot.config.BilibiliSpec
import com.uchuhimo.konf.NoSuchItemException
import com.uchuhimo.konf.source.json.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.MessageEventSubscribersBuilder
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.message.data.buildMessageChain

suspend fun saveConfig() = withContext(Dispatchers.IO) {
    config.toJson.toFile("config.json")
}

/**
 * 如果是群，则为@信息
 * 如果不是群，则为普通QQ号
 */
fun MessageEvent.plainOrAt(qq: Long) = buildMessageChain {
    if (subject is Group)
        add(At((subject as Group)[qq]!!))
    else
        add("$qq")
}

fun MessageEventSubscribersBuilder.configCommands() {
    case("下饭主播").and(sentFrom(967848202).or(sentFrom(601897811))).reply {
        buildString {
            var anyOnline = false
            for (id in config[BilibiliSpec.lives]) {
                val roomInfo = withContext(Dispatchers.IO) {
                    getLiveRoomInfo(id)
                }
                if (roomInfo.live_status == 1) {
                    appendLine("你喜爱的主播：" + roomInfo.title + " 正在直播并有${roomInfo.online}人气值！https://live.bilibili.com/$id")
                    anyOnline = true
                }
            }
            if (!anyOnline) append("你喜爱的主播们都不在直播哦O(∩_∩)O")
        }
    }
    startsWith("sudo ") {
        return@startsWith
        val member = it.substringBefore(' ').trim().toLong()
        val message = it.substringAfter(' ').trim()
        val event = this
        if (subject is Group) {
            reply("SUDO AS $member")
            val group = subject as Group
            if (!group.contains(member)) { reply("群里没这人！"); return@startsWith }
            val realMember = group[member]!!

            (bot.eventChannel.asChannel() as Channel<BotEvent>).send(
                GroupMessageEvent(
                    senderName,
                    realMember.permission,
                    realMember,
                    buildMessageChain { add(event.message[MessageSource]!!); add(message) },
                    time
                )
            )
        }
    }
    matching(Regex("/op \\w+")).and(content { sender.id in config[AppSpec.ops] }).reply {
        val qq = it.removePrefix("/op ").toLong()
        config[AppSpec.ops] = config[AppSpec.ops] + qq
        buildMessageChain {
            add("Opped ")
            add(plainOrAt(qq))
        }
        saveConfig()
    }
    matching(Regex("/deop \\w+")).and(content { sender.id in config[AppSpec.ops] }).reply {
        val qq = it.removePrefix("/deop ").toLong()
        config[AppSpec.ops] = config[AppSpec.ops] - qq
        buildMessageChain {
            add("De-opped ")
            add(plainOrAt(qq))
        }
        saveConfig()
    }
    case("DEBUG").reply {
        config[AppSpec.debug] = !config[AppSpec.debug]
        "DEBUG MODE " + config[AppSpec.debug]
    }
    startsWith("config ").and(content { sender.id in config[AppSpec.ops] }).reply {
            val subcommand = it.removePrefix("config ")
        try {
            when {
                subcommand.startsWith("list") -> {
                    return@reply config.itemWithNames.map { (_, name) -> name }
                }
                subcommand.startsWith("get") -> {
                    if (subcommand.removePrefix("get ").startsWith("account")) return@reply "你还想看账号信息？想得美"
                    return@reply config[subcommand.removePrefix("get ")]
                }
                subcommand.startsWith("set") -> {
                    if (sender.id in config[AppSpec.ops]) return@reply "非机器人管理员"
                    val key = subcommand.removePrefix("set ").substringBefore(' ')
                    val value = subcommand.removePrefix("set ").substringAfter(' ')
                    config[key] = value
                    return@reply "暂不支持设置。"
                }
            }
        } catch (e: NoSuchItemException) {
            return@reply "未知配置 ${e.name}。"
        }
        "未知子命令。"
    }
}
