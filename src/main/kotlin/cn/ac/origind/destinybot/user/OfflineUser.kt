package cn.ac.origind.destinybot.user

import cn.ac.origind.destinybot.data.UserData
import kotlin.coroutines.CoroutineContext

abstract class OfflineUser(val qq: Long, override val coroutineContext: CoroutineContext, override val data: UserData) : User {
}
