package net.origind.destinybot.features.bilibili

import com.electronwill.nightconfig.core.Config

class BilibiliConfig(config: Config) {
    val lives: List<Long> = config.get("bilibili.lives")
}
