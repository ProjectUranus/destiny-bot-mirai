package cn.ac.origind.destinybot.user

import cn.ac.origind.destinybot.data.UserData
import kotlinx.coroutines.CoroutineScope
import net.mamoe.mirai.message.data.Message

interface User : CoroutineScope {
    val data: UserData

    suspend fun sendMessage(s: String)

    suspend fun sendMessage(message: Message)

    fun hasPermission(perm: String): Boolean
}
