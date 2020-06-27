package cn.ac.origind.minecraft

import net.mamoe.mirai.event.MessagePacketSubscribersBuilder

fun MessagePacketSubscribersBuilder.curseForgeCommands() {
    startsWith("搜索mod ", removePrefix = true, trim = true) {
        reply(buildString {
            searchImmibis(it).forEachIndexed { index, mod ->
                append("${index + 1}. ")
                appendln(mod.name + " " + mod.url)
                appendln("作者: ${mod.author}")
                appendln("下载量: ${mod.downloads}")
                appendln("创建时间: ${mod.createdTime}")
                appendln("最后更新时间: ${mod.updatedTime}")
                appendln()
            }
        })
    }
}
