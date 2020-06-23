package cn.ac.origind.uno

import cn.ac.origind.destinybot.caseAny
import com.google.gson.Gson
import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.MessagePacketSubscribersBuilder
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import java.io.InputStreamReader
import java.util.function.Predicate

val unoGameMap = Long2ObjectOpenHashMap<Desk>()
val Contact.unoGame get() = unoGameMap[this.id]
lateinit var messages: JsonObject
val messagesValues: List<String> by lazy { messages.entrySet().asSequence().flatMap { it.value.asJsonArray.asSequence() }.map { it.asJsonPrimitive.asString }.toList() }
val messageCommandMap: Map<String, String> by lazy { messages.entrySet().asSequence().flatMap { it.value.asJsonArray.asSequence().map { value -> value.asJsonPrimitive.asString to it.key } }.toMap() }

suspend fun initUnoGame() {
    val gson = Gson()
    messages = withContext(Dispatchers.IO) { gson.fromJson(InputStreamReader(Desk::class.java.getResourceAsStream("/messages.json")), JsonObject::class.java) }
}

@ExperimentalStdlibApi
fun MessagePacketSubscribersBuilder.unoGames() {
    subscriber(caseAny(messagesValues).filter) {
        val command = messageCommandMap[message[PlainText]!!.content]
        val member = (subject as Group)[sender.id]
        when (command) {
            "join" -> {
                val desk = unoGameMap.getOrPut(subject.id) { Desk(subject as Group) }
                if (desk.hasPlayer(sender)) {
                    reply(buildMessageChain {
                        add("已经加入：")
                        add(At(member))
                    })
                } else {
                    desk.players += UnoPlayer(member, desk)
                    reply(buildMessageChain {
                        add("加入成功：")
                        add(At(member))
                        add("\n")
                        add("UNO当前玩家有：")
                        for (player in desk.players) {
                            add(player.toReferMessage())
                            add(", ")
                        }
                    })
                }
                return@subscriber
            }
            "start" -> {
                val desk = unoGameMap.getOrPut(subject.id) { Desk(subject as Group) }
                if (desk.players.size < 2) {
                    reply("喂伙计，玩家人数不够！")
                    return@subscriber
                }
                desk.players.shuffle()
                desk.lastCard = desk.nextCard(Predicate { it.color != CardColor.Special && it.color != CardColor.Wild }).first()
                reply(buildMessageChain {
                    desk.players.forEachIndexed { index, player ->
                        player.index = index
                        desk.addCards(player, 7)
                        add("$index. ")
                        add(At(player.member))
                        add("\n")
                    }
                })
                desk.state = Desk.GamingState.Gaming
                desk.sendLastCardMessage()
                return@subscriber
            }
        }
        if (unoGameMap[subject.id] != null) {
            val desk = unoGameMap.getOrPut(subject.id) { Desk(subject as Group) }
            val player = desk.players.find { it.member.id == sender.id }!!
            if (desk.state == Desk.GamingState.Gaming) {
                when (command) {
                    "draw" -> {
                        desk.addCards(player, 1)
                        desk.moveNext()
                        desk.sendLastCardMessage()
                    }
                    "uno" -> {
                        if ((desk.currentPlayer == player && player.cards.size == 2) || (desk.lastSendPlayer == player && player.cards.size == 1)) {
                            player.uno = true
                            reply("UNO!")
                        } else {
                            reply("你还不能说 UNO!")
                        }
                        return@subscriber
                    }
                    "doubtUno" -> if (desk.lastSendPlayer?.cards?.size == 1 && desk.lastSendPlayer?.uno == false) {
                        reply(desk.atMessage(desk.lastSendPlayer!!.member, "没有说 UNO，被罚牌两张！"))
                        desk.addCards(desk.lastSendPlayer!!, 2)
                        desk.sendLastCardMessage()
                    }
                    "publicCard" -> {
                        player.publicCard = true
                        reply(desk.atMessage(player.member, " 明牌成功"))
                        return@subscriber
                    }
                    "myRound" -> {
                        reply("是是，我们都知道是你的回合")
                        return@subscriber
                    }
                    "autoSubmit" -> {
                        player.autoSubmit = true
                        reply("完成.")
                        return@subscriber
                    }
                    "disableAutoSubmit" -> {
                        player.autoSubmit = false
                        reply("搞定.")
                        return@subscriber
                    }
                }
            }
            else if (command == "draw" && (desk.state == Desk.GamingState.WaitingDrawFourOverlay || desk.state == Desk.GamingState.WaitingDrawTwoOverlay)) {
                desk.finishDraw(desk.currentPlayer!!)
                desk.moveNext()
                desk.sendLastCardMessage()
            }
            else if (desk.state == Desk.GamingState.Doubting) {
                when (command) {
                    "draw" -> {
                        reply("我在问你要不要质疑! 不是问你摸不摸!")
                    }
                    "doubt" -> desk.finishDoubt(player, true)
                    "nonDoubt" -> desk.finishDoubt(player, false);

                    else -> reply("不是一个标准的质疑命令。")
                }
            }
        }
    }
    /*
    content({Card(it) != null}) {
        if (unoGameMap[subject.id] != null) {
            val desk = unoGameMap.getOrPut(subject.id) { Desk(subject as Group) }
            val player = desk.players.find { it.member.id == sender.id }!!
            if (desk.state == Desk.GamingState.Gaming) {
                val card = Card(message[PlainText]!!.content)
                if (card != null && UnoRule.IsValidForFollowCard(card, desk.lastCard, desk.state)) {
                    if (!UnoRule.IsValid(card, desk.lastCard!!, desk.state)) {
                        reply("你想出的牌并不匹配 UNO 规则.");
                        return@content
                    }
                    if (!card.IsValidForPlayerAndRemove(player)) {
                        reply("你的手里并没有这些牌.");
                        return@content
                    }

                    desk.afterSend(player, card);
                    desk.currentIndex = player.index
                    desk.moveNext()
                    desk.sendLastCardMessage()
                }
            }
        }
    }
     */
    case("印卡") {
        val desk = Desk(subject as Group)
        sendImage(drawUnoCards(desk.generateCards(20).sorted()))
    }
}
