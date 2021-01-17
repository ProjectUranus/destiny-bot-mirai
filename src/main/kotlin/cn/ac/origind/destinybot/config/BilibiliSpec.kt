package cn.ac.origind.destinybot.config

import com.uchuhimo.konf.ConfigSpec

object BilibiliSpec : ConfigSpec() {
    val lives by required<List<Long>>()
}
