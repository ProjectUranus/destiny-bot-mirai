package net.origind.destinybot.api.timer

import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

val LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

object TimerManager {
    private val scope: CoroutineScope
    private val taskScheduler: CoroutineDispatcher
    val tasks = mutableMapOf<String, TimedTask>()
    private val disabledTasks = hashSetOf<String>()

    init {
        taskScheduler = Executors.newFixedThreadPool(8).asCoroutineDispatcher()
        scope = CoroutineScope(taskScheduler)
    }

    fun schedule(name: String, task: TimedTask) {
        tasks[name] = task
    }

    fun disable(name: String) {
        disabledTasks += name
    }

    fun enable(name: String) {
        disabledTasks -= name
    }

    fun run() {
        for ((name, task) in tasks) {
            scope.launch {
                while (name !in disabledTasks) {
                    task.task()
                    task.lastExecuted = LocalDateTime.now()
                    delay(task.interval.toMillis())
                }
            }
        }
    }

    fun cancel() {
        scope.cancel()
    }
}
