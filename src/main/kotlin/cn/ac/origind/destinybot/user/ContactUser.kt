package cn.ac.origind.destinybot.user

import cn.ac.origind.destinybot.data.DataStore
import cn.ac.origind.destinybot.data.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.Message

class ContactUser(val contact: Contact) : User, CoroutineScope by contact {
    override val data: UserData
        get() = DataStore[contact.id]

    override suspend fun sendMessage(s: String) {
        contact.launch { contact.sendMessage(s) }
    }

    override suspend fun sendMessage(message: Message) {
        contact.launch { contact.sendMessage(message) }
    }

    override fun hasPermission(perm: String): Boolean {
        TODO("Not yet implemented")
    }
}
