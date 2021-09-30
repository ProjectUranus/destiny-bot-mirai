package net.origind.destinybot.core

import net.origind.destinybot.api.command.AbstractCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor

object HelpCommand: AbstractCommand("/dshelp") {
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        executor.sendMessage(buildString {
            appendLine("欢迎使用 LG 的命运2小帮手机器人 555EX780+1GGWP 版。")
            appendLine("获取该帮助: /ds help")
            appendLine("帮助的帮助: 带<>的为必填内容, []为选填内容")
            appendLine("命运2命令:")
            appendLine("“传奇故事” 或 <传奇故事的名称>: 获取一个随机或特定的传奇故事")
            appendLine("perk<武器>: 在 light.gg 上获取武器 Perk 信息")
            appendLine("/ds search <用户名>: 搜索一名命运2玩家")
            appendLine("/tracker <用户名>: 在 Destiny Tracker 上搜索一名玩家")
            appendLine("绑定 <搜索结果前的序号|玩家ID>: 绑定你的命运2账户到QQ号")
            appendLine("我的信息: 若绑定命运2账户则显示玩家信息")
            appendLine("/j <队伍码>: 用队伍码(SteamID64)查询你的棒鸡用户ID和个人信息")
            appendLine()
            appendLine("APEX命令:")
            appendLine("apex开盒 <橘子id>: 显示你的 Apex 信息")
            appendLine("地图轮换: 查询 Apex 当前地图轮换")
            appendLine()
            appendLine("bilibili命令:")
            appendLine("下饭主播: 你喜欢的主播列表")
            appendLine()
            appendLine("Minecraft 命令:")
            appendLine("/<MC版本, 去掉.> 如/1710: 显示你在玩的MC版本有多远古")
            appendLine("/latest: 显示最新 Minecraft 快照信息")
            appendLine("/release: 显示最新 Minecraft 信息")
            appendLine("/ping: 显示 Origind 服务器信息")
            appendLine("/ping <cy/咕咕>: 显示其他的服务器信息")
            appendLine("/ping <服务器地址>: 显示你指定的服务器信息, 暂不支持 SRV 记录")
            appendLine()
            appendLine("WIP: 斗地主、UNO、其他Tracker")
            append("如有任何问题[想被LG喷一顿] 请@你群中的LG")
        })
    }
}
