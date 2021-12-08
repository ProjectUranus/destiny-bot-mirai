package net.origind.destinybot.api.util

import java.time.Duration

fun Duration.toLocalizedString() = buildString {
    val duration = this@toLocalizedString
    val days = duration.toDaysPart()
    val hours = duration.toHoursPart()
    val minutes = duration.toMinutesPart()
    val seconds = duration.toSecondsPart()
    if (days > 0) append("$days 天 ")
    if (hours > 0) append("$hours 小时 ")
    if (minutes > 0) append("$minutes 分 ")
    if (seconds > 0) append("$seconds 秒")
}.trim()
