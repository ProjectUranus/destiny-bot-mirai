package net.origind.destinybot.api.util

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.joinToString(): String {
    val writer = StringWriter()
    this.printStackTrace(PrintWriter(writer))
    writer.close()
    return writer.toString()
}
