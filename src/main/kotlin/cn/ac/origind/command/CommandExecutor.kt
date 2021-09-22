package cn.ac.origind.command

interface CommandExecutor {
    fun hasPermission(node: String): Boolean
    suspend fun sendMessage(text: String)
    suspend fun sendPrivateMessage(text: String) = sendMessage(text)
}
