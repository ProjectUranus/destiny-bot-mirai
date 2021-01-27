package cn.ac.origind.destinybot.command

import cn.ac.origind.destinybot.DestinyBot
import cn.ac.origind.destinybot.ItemNotFoundException
import cn.ac.origind.destinybot.database.searchItemDefinitions
import cn.ac.origind.destinybot.exception.WeaponNotFoundException
import cn.ac.origind.destinybot.exception.joinToString
import cn.ac.origind.destinybot.getItemPerks
import cn.ac.origind.destinybot.reply
import com.mojang.brigadier.CommandDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nederlib.bka.literal
import net.mamoe.mirai.event.events.MessageEvent

fun destinyBrigadierCommands(dispatcher: CommandDispatcher<MessageEvent>) {
    dispatcher.literal("/perk") {
        string("weapon") {
            run {
                val message = this
                subject.launch {
                    for (item in searchItemDefinitions(it.argumentOf("weapon", String::class))) {
                        GlobalScope.launch(Dispatchers.Default) {
                            try {
                                val perks = getItemPerks(item._id!!)
                                DestinyBot.replyPerks(item, perks, message)
                            } catch (e: WeaponNotFoundException) {
                                reply(e.message ?: "")
                            } catch (e: ItemNotFoundException) {
                                reply("搜索失败: ${e.localizedMessage}, 正在尝试其他方式")
                            } catch (e: Exception) {
                                reply("搜索失败：" + e.joinToString())
                            }
                        }
                    }
                }
            }
        }
    }
}
