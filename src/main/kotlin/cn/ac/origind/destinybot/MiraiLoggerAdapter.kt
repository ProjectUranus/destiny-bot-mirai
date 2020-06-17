package cn.ac.origind.destinybot

import ch.qos.logback.core.Appender
import ch.qos.logback.core.UnsynchronizedAppenderBase

class MiraiLoggerAdapter<E> : UnsynchronizedAppenderBase<E>() {
    override fun append(eventObject: E) {
    }
}
