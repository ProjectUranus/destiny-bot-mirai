package cn.ac.origind.destinybot.config

import com.uchuhimo.konf.ConfigSpec

object AppSpec : ConfigSpec() {
    val debug by optional(false)
}
