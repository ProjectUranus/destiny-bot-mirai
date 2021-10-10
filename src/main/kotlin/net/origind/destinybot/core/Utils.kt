package net.origind.destinybot.core

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

private val loggingFriend get() = DestinyBot.bot.getFriend(1276571946)

suspend fun RenderedImage.upload(contact: Contact): Image {
    val temp = File.createTempFile("img", ".png", null)
    ImageIO.write(this, "png", temp)
    return contact.uploadImage(temp.toExternalResource("png"))
}

fun groupLog(message: String) {
    if (DEBUG)
        loggingFriend?.launch {
            loggingFriend?.sendMessage(message)
        }
}

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

suspend inline fun getBody(url: String, proxy: Boolean = true, crossinline init: Request.Builder.() -> Unit = {}) = withContext(Dispatchers.IO) {
    val request = Request.Builder().apply {
        url(url)
        init()
    }.build()
    val call = client.newCall(request)
    val response = call.execute()
    groupLog("请求 $url (proxy = $proxy) 耗时：" + (response.receivedResponseAtMillis - response.sentRequestAtMillis) + "ms")
    response.body?.string() ?: ""
}

suspend inline fun <reified T> getJson(url: String, proxy: Boolean = true, crossinline init: Request.Builder.() -> Unit = {}): T = withContext(Dispatchers.IO) {
    val request = Request.Builder().apply {
        url(url)
        init()
    }.build()
    val call = client.newCall(request)
    val response = call.execute()
    groupLog("请求 $url (proxy = $proxy) 耗时：" + (response.receivedResponseAtMillis - response.sentRequestAtMillis) + "ms")
    val json = response.body?.string()!!
    moshi.adapter(T::class.java).fromJson(json)!!
}

suspend fun MessageEvent.reply(message: String) = subject.sendMessage(message)
suspend fun MessageEvent.reply(message: Message) = subject.sendMessage(message)

suspend fun Throwable.joinToString(): String = withContext(Dispatchers.IO) {
    val writer = StringWriter()
    this@joinToString.printStackTrace(PrintWriter(writer))
    writer.close()
    writer.toString()
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
