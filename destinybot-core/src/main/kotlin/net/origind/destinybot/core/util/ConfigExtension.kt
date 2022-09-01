package net.origind.destinybot.core.util

import com.electronwill.nightconfig.core.Config

fun <T> Config.getOrThrow(path: String, exception: () -> Throwable): T {
	val value = get<T>(path)
	return value ?: throw exception()
}