package net.origind.destinybot.api.timer

import java.time.Duration
import java.time.LocalDateTime

class TimedTask(val task: suspend () -> Unit, var interval: Duration, var lastExecuted: LocalDateTime = LocalDateTime.MIN) {
}
