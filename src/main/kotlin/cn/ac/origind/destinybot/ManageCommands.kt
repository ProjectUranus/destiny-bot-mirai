package cn.ac.origind.destinybot

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.event.MessageEventSubscribersBuilder

fun MessageEventSubscribersBuilder.manageCommands() {
    matching(Regex("设置头衔 (\\d+) (.+)")) {
        val qq = it.groupValues[1].toLong()
        val specialTitle = it.groupValues[2]
        if (subject is Group) {
            val group = subject as Group
            if (group.getMember(sender.id)!!.permission == MemberPermission.MEMBER) {
                reply("你无权这么做。")
                return@matching
            }
            if (group.botPermission != MemberPermission.OWNER) {
                reply("机器人不是群主。")
                return@matching
            }
            if (specialTitle.length !in 1..6) {
                reply("头衔长度应处于 1~6 之间。")
                return@matching
            }
            if (qq !in group) {
                reply("要设置的成员不在群中。")
                return@matching
            }
            group[qq]!!.specialTitle = specialTitle
            reply("设置成功")
        }
    }
}
