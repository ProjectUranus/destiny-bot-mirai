package net.origind.destinybot.api.command

import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.isAdministrator
import net.mamoe.mirai.message.data.Message

class UserCommandExecutor(val user: User) : CommandExecutor {
    override fun hasPermission(node: String): Boolean = if (user is Member && user.permission.isAdministrator()) true
        else !node.startsWith("admin.")

    override fun sendMessage(text: String) {
        if (text.isBlank()) return
        user.launch {
            if (user is Member) {
                user.group.sendMessage(text.trim())
            } else {
                user.sendMessage(text.trim())
            }
        }
    }

    override fun sendPrivateMessage(text: String) {
        user.launch { user.sendMessage(text) }
    }

    fun sendMessage(miraiMsg: Message) {
        user.launch {
            if (user is Member) {
                user.group.sendMessage(miraiMsg)
            } else {
                user.sendMessage(miraiMsg)
            }
        }
    }
}
