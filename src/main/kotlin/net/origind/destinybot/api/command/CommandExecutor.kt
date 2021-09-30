package net.origind.destinybot.api.command

interface CommandExecutor {
    fun hasPermission(node: String): Boolean
    fun sendMessage(text: String)
    fun sendPrivateMessage(text: String) = sendMessage(text)
}
