package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.config.AppSpec
import net.mamoe.mirai.event.MessageEventSubscribersBuilder

fun MessageEventSubscribersBuilder.backupCommands() {
    case("/backup") {
        if (sender.id in DestinyBot.config[AppSpec.ops]) {
            reply("非机器人管理员")
            return@case
        }

    }
}
