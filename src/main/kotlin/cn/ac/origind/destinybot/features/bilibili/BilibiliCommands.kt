package cn.ac.origind.destinybot.features.bilibili

import cn.ac.origind.destinybot.DestinyBot
import cn.ac.origind.destinybot.config.BilibiliSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.event.MessageEventSubscribersBuilder

fun MessageEventSubscribersBuilder.bilibiliCommands() {
    case("下饭主播").reply {
        buildString {
            var anyOnline = false
            for (id in DestinyBot.config[BilibiliSpec.lives]) {
                val roomInfo = withContext(Dispatchers.IO) {
                    getLiveRoomInfo(id)
                }
                if (roomInfo.live_status == 1) {
                    appendLine("你喜爱的主播：" + roomInfo.title + " 正在直播并有${roomInfo.online}人气值！https://live.bilibili.com/$id")
                    anyOnline = true
                }
            }
            if (!anyOnline) append("你喜爱的主播们都不在直播哦O(∩_∩)O")
        }.trim()
    }
}
