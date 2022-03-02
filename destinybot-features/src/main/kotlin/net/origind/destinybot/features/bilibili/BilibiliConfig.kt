package net.origind.destinybot.features.bilibili

import com.electronwill.nightconfig.core.Config

class BilibiliConfig(config: Config) {
    val lives: MutableList<Long> = config.get("bilibili.lives")
    val cookie: String = config.get("bilibili.cookie")
}
