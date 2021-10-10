package net.origind.destinybot.core.config

import com.uchuhimo.konf.ConfigSpec

object AppSpec : ConfigSpec() {
    val debug by optional(false, description = "开启调试模式")
    val ops by optional(listOf(1276571946L), description = "全局机器人管理员")
}
