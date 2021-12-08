package net.origind.destinybot.core.command

import net.origind.destinybot.api.command.AbstractCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor

object HelpCommand: AbstractCommand("/dshelp") {
    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        executor.sendMessage(buildString {
            appendLine("欢迎使用 LG 的各种乱七八糟功能机器人 2.0 版。")
            appendLine("获取该帮助: /dshelp")
            appendLine("参数的帮助: 带()的为必填内容, []为选填内容")
            appendLine("如有任何问题[想被LG喷一顿] 请@你群中的LG")
        })
        executor.sendMessage(CommandManager.helpText)
    }
}
