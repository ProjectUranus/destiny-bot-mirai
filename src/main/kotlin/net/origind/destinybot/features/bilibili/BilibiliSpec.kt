package net.origind.destinybot.features.bilibili

import com.uchuhimo.konf.ConfigSpec

object BilibiliSpec : ConfigSpec() {
    val lives by required<List<Long>>()
}
