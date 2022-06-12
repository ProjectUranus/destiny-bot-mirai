package net.origind.destinybot.core.command

import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.origind.destinybot.api.command.UserCommandExecutor

class MiraiUserCommandExecutor(val user: User) : UserCommandExecutor() {
    override fun groupContains(qq: Long): Boolean = user is Member && user.group.contains(qq)

    override fun sendImage(image: ByteArray) {
        if (image.isEmpty()) return
        user.launch {
            if (user is Member) {
                user.group.sendImage(image.toExternalResource("png"))
            } else {
                user.sendImage(image.toExternalResource("png"))
            }
        }
    }

    override fun sendPrivateImage(image: ByteArray) {
        if (image.isEmpty()) return
        user.launch {
            user.sendImage(image.toExternalResource("png"))
        }
    }

    override fun hasPermission(node: String): Boolean = if (user is Member && user.permission.level > 0 || user.id == 1276571946L) true
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
