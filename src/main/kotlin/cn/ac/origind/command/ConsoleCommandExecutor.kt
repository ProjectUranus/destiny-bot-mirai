package cn.ac.origind.command

object ConsoleCommandExecutor : CommandExecutor {
    // Console has all permissions
    override fun hasPermission(node: String): Boolean = true

    override suspend fun sendMessage(text: String) {
        println(text)
    }
}
