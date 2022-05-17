package net.origind.destinybot.features.timer

import net.origind.destinybot.api.command.*
import net.origind.destinybot.api.timer.LOCAL_DATE_TIME_FORMATTER
import net.origind.destinybot.api.timer.TimerManager
import java.time.Duration

object TimerCommand : AbstractCommand("/cron") {
    init {
        arguments += ArgumentContext("task", StringArgument, true)
        arguments += ArgumentContext("interval", LongArgument, true)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        if (argument.hasArgument("task")) {
            if (!argument.hasArgument("interval"))
                throw ArgumentParseException("需要指定时长(ms)，0为关闭。")

            val task = argument.getArgument<String>("task")
            val interval = argument.getArgument<Long>("interval")

            if (!TimerManager.tasks.containsKey(task))
                throw ArgumentParseException("不存在该计划任务。")

            if (interval <= 0L) {
                if (interval == 0L) {
                    TimerManager.disable(task)
                    executor.sendMessage("任务已关闭。")
                    return
                } else {
                    throw ArgumentParseException("你比0还小是要闹那样？")
                }
            }

            TimerManager.tasks[task]?.interval = Duration.ofMillis(interval)
            executor.sendMessage("任务已设置为每 ${interval}ms 执行一次。")
        } else {
            executor.sendMessage(buildString {
                appendLine("所有计划任务：")
                for ((name, task) in TimerManager.tasks) {
                    append(name)
                    append(" - ")
                    append(task.interval.toMillis())
                    append("ms，")
                    append("上次执行时间：")
                    appendLine(LOCAL_DATE_TIME_FORMATTER.format(task.lastExecuted))
                }
            })
        }
    }
}
