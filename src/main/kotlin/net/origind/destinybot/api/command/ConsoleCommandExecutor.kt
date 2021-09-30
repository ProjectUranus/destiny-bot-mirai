package net.origind.destinybot.api.command

object ConsoleCommandExecutor : CommandExecutor {
    // Console has all permissions
    override fun hasPermission(node: String): Boolean = true

    override fun sendMessage(text: String) {
        println(text)
    }
}
