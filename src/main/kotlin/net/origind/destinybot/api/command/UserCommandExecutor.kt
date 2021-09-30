package net.origind.destinybot.api.command

import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.Message

class UserCommandExecutor(val user: User) : CommandExecutor {
    override fun hasPermission(node: String): Boolean =
        !node.startsWith("admin.")

    override fun sendMessage(text: String) {
        user.launch {
            if (user is Member) {
                user.group.sendMessage(text)
            } else {
                user.sendMessage(text)
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
