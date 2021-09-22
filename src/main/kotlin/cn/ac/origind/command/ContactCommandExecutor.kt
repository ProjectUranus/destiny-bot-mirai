package cn.ac.origind.command

import net.mamoe.mirai.contact.Contact

class ContactCommandExecutor(val contact: Contact) : CommandExecutor {
    override fun hasPermission(node: String): Boolean =
        !node.startsWith("admin.")


    override suspend fun sendMessage(text: String) {
        contact.sendMessage(text)
    }
}
