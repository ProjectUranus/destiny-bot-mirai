package cn.ac.origind.minecraft

import cn.ac.origind.destinybot.reply
import net.mamoe.mirai.event.MessageEventSubscribersBuilder

fun MessageEventSubscribersBuilder.curseForgeCommands() {
    startsWith("搜索mod ", removePrefix = true, trim = true) {
        reply(buildString {
            searchImmibis(it).forEachIndexed { index, mod ->
                append("${index + 1}. ")
                appendLine(mod.name + " " + mod.url)
                appendLine("作者: ${mod.author}")
                appendLine("下载量: ${mod.downloads}")
                appendLine("创建时间: ${mod.createdTime}")
                appendLine("最后更新时间: ${mod.updatedTime}")
                appendLine()
            }
        })
    }
}
