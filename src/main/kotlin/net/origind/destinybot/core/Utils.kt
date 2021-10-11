package net.origind.destinybot.core

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.origind.destinybot.core.DestinyBot.client
import net.origind.destinybot.core.DestinyBot.config
import net.origind.destinybot.core.DestinyBot.logger
import net.origind.destinybot.core.config.AppSpec
import okhttp3.Request
import java.awt.image.RenderedImage
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Duration
import javax.imageio.ImageIO

val DEBUG : Boolean get() = config[AppSpec.debug]

val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

suspend fun RenderedImage.upload(contact: Contact): Image =
    coroutineScope {
        async(Dispatchers.IO) {
            val temp = File.createTempFile("img", ".png", null)
            ImageIO.write(this@upload, "png", temp)
            contact.uploadImage(temp.toExternalResource("png"))
        }
    }.await()

/**
 * @throws RuntimeException
 */
inline fun <reified T> T?.orLogThrow(msg: String, e: Throwable? = null) : T {
    if (this != null) return this
    else {
        val exception = e ?: NullPointerException()
        logger.error(msg, exception)
        throw RuntimeException(exception)
    }
}

suspend inline fun getBodyAsync(url: String, crossinline init: Request.Builder.() -> Unit = {}) = coroutineScope {
    val request = Request.Builder().apply {
        url(url)
        init()
    }.build()
    val call = client.newCall(request)
    val response = async(Dispatchers.IO) { call.execute().body?.string() ?: "" }
    response
}

suspend inline fun <reified T> getJson(url: String, crossinline init: Request.Builder.() -> Unit = {}): T =
    moshi.adapter(T::class.java).fromJson(getBodyAsync(url, init).await())!!


suspend fun MessageEvent.reply(message: String) = subject.sendMessage(message)
suspend fun MessageEvent.reply(message: Message) = subject.sendMessage(message)

fun Throwable.joinToString(): String {
    val writer = StringWriter()
    printStackTrace(PrintWriter(writer))
    writer.close()
    return writer.toString()
}

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
