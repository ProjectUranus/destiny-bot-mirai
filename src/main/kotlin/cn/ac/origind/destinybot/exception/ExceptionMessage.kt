package cn.ac.origind.destinybot.exception

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.io.StringWriter

suspend fun Throwable.joinToString(): String = withContext(Dispatchers.IO) {
    val writer = StringWriter()
    this@joinToString.printStackTrace(PrintWriter(writer))
    writer.close()
    writer.toString()
}