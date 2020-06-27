package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.DestinyBot.config
import cn.ac.origind.destinybot.config.AppSpec
import com.uchuhimo.konf.NoSuchItemException
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.MessagePacketSubscribersBuilder
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain

/**
 * 如果是群，则为@信息
 * 如果不是群，则为普通QQ号
 */
fun MessageEvent.plainOrAt(qq: Long) = buildMessageChain {
    if (subject is Group)
        add(At((subject as Group)[qq]))
    else
        add("$qq")
}

fun MessagePacketSubscribersBuilder.configCommands() {
    matching(Regex("/op \\w+")).reply {
        val qq = it.removePrefix("/op ").toLong()
        config[AppSpec.ops] = config[AppSpec.ops] + qq
        buildMessageChain {
            add("Opped ")
            add(plainOrAt(qq))
        }
    }
    matching(Regex("/deop \\w+")).reply {
        val qq = it.removePrefix("/deop ").toLong()
        config[AppSpec.ops] = config[AppSpec.ops] - qq
        buildMessageChain {
            add("De-opped ")
            add(plainOrAt(qq))
        }
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
                    return@reply "暂不支持设置。"
                }
            }
        } catch (e: NoSuchItemException) {
            return@reply "未知配置 ${e.name}。"
        }
        "未知子命令。"
    }
}
