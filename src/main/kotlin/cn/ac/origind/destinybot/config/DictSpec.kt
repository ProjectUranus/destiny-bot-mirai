package cn.ac.origind.destinybot.config

import com.uchuhimo.konf.ConfigSpec

object DictSpec : ConfigSpec() {
    val aliases by optional<Map<String, String>>(mapOf())
    val userAliases by optional<Map<String, Array<String>>>(mapOf())
}