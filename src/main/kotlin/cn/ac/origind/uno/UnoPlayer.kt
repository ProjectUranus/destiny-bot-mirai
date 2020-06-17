package cn.ac.origind.uno

import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.sendImage
import java.time.Instant

class UnoPlayer(val member: Member, val desk: Desk) {
    var index = 0
    var lastSendTime = Instant.now()
    var autoSubmit = false
    var publicCard = false
    var nick = "${member.id}"
    var uno = false
    val isCurrentPlayer get() = desk.currentIndex == index
    val cards = mutableListOf<Card>()

    fun toReferMessage() = At(member)

    suspend fun sendCards() {
        if (cards.size > 50) {
            member.sendMessage(if (cards.size > 200)
                "你特娘的是疯了吗，这里是你的卡：" + cards.take(100).joinToString(", ") { it.shortName } + "...${cards.size - 100} more"
            else
                "你的卡太多啦, 这里是你的卡：" + cards.joinToString(", ") { it.shortName })
        } else {
            member.sendImage(drawUnoCards(cards))
        }
    }
}
