package cn.ac.origind.destinybot

import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import cn.ac.origind.destinybot.DestinyBot.bot
import cn.ac.origind.destinybot.DestinyBot.config
import cn.ac.origind.destinybot.config.AppSpec
import kotlinx.coroutines.launch
import net.mamoe.mirai.getGroupOrNull

class MiraiLoggerAdapter : AppenderBase<ILoggingEvent>() {
    val contact get() = if (bot.isOnline) bot.getGroupOrNull(967848202) else null
    lateinit var patternLayout: PatternLayout

    override fun start() {
        patternLayout = PatternLayout()
        patternLayout.context = getContext()
        patternLayout.pattern = "[%d{HH:mm:ss}] [%t/%level]: %msg%n"
        patternLayout.start()

        super.start()
    }

    override fun append(eventObject: ILoggingEvent) {
        if (config[AppSpec.debug])
            contact?.launch {
                contact?.sendMessage(patternLayout.doLayout(eventObject))
            }
    }
}
