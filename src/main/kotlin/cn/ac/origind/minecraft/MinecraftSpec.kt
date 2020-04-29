package cn.ac.origind.minecraft

import com.uchuhimo.konf.ConfigSpec

object MinecraftSpec : ConfigSpec() {
    val host by required<String>()
    val port by optional(25565)
}