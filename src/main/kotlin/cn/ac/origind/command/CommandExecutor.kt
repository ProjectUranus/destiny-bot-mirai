package cn.ac.origind.command

interface CommandExecutor {
    fun hasPermission(node: String): Boolean
    fun sendMessage(text: String)
}
