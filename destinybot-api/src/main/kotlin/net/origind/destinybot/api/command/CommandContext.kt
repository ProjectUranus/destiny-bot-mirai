package net.origind.destinybot.api.command

data class CommandContext(val senderId: Long, val subjectId: Long, val message: String, val time: Long)
