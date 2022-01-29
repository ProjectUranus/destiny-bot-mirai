package net.origind.destinybot.core.util

import com.squareup.moshi.JsonClass
import net.mamoe.mirai.contact.NormalMember

@JsonClass(generateAdapter = true)
data class MemberData(val id: Long, val permission: Int, val nick: String, val name: String, val rank: String, val joinTimestamp: Int, val lastSpeakTimestamp: Int) {
    constructor(member: NormalMember) : this(member.id, member.permission.ordinal, member.nameCard, member.nick, member.specialTitle, member.joinTimestamp, member.lastSpeakTimestamp)
}
