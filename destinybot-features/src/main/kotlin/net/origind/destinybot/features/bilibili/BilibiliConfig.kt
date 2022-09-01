package net.origind.destinybot.features.bilibili

import com.electronwill.nightconfig.core.Config

class BilibiliConfig(config: Config) {
    val lives: MutableList<Long> = config.getOrElse("bilibili.lives", mutableListOf())
    val cookie: String = config.getOrElse("bilibili.cookie", "")
    val replyStreamersTo: MutableList<Long> = config.getOrElse("bilibili.reply_to", mutableListOf())
}
