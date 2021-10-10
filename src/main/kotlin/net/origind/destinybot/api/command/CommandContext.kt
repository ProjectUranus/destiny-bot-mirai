package net.origind.destinybot.api.command

import net.origind.destinybot.core.DestinyBot.bot

data class CommandContext(val senderId: Long, val subjectId: Long, val message: String, val time: Long) {
    val subject get() = bot.getGroup(subjectId)
}
