package cn.ac.origind.destinybot

import ch.qos.logback.core.UnsynchronizedAppenderBase
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact

class MiraiLoggerAdapter<E> : UnsynchronizedAppenderBase<E>() {
    var contact: Contact? = null

    override fun append(eventObject: E) {
        contact?.launch {
            contact?.sendMessage(eventObject.toString())
        }
    }
}
