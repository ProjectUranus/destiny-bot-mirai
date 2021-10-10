package net.origind.destinybot.core.config

import com.uchuhimo.konf.ConfigSpec

object AccountSpec : ConfigSpec() {
    val qq by required<Long>()
    val password by required<String>()
}
