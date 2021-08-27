package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.DestinyBot.config
import cn.ac.origind.destinybot.DestinyBot.logger
import cn.ac.origind.destinybot.config.AppSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import okhttp3.Request
import java.awt.image.RenderedImage
import java.io.File
import javax.imageio.ImageIO

val DEBUG : Boolean get() = config[AppSpec.debug]

val MessageEvent.user get() = {
}

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
    val call = (if(proxy)client else client).newCall(request)
    val response = call.execute()
    groupLog("请求 $url (proxy = $proxy) 耗时：" + (response.receivedResponseAtMillis - response.sentRequestAtMillis) + "ms")
    response.body?.string() ?: ""
}

suspend inline fun <reified T> getJson(url: String, proxy: Boolean = true, crossinline init: Request.Builder.() -> Unit = {}): T = withContext(Dispatchers.IO) {
    val request = Request.Builder().apply {
        url(url)
        init()
    }.build()
    val call = (if(proxy)client else client).newCall(request)
    val response = call.execute()
    groupLog("请求 $url (proxy = $proxy) 耗时：" + (response.receivedResponseAtMillis - response.sentRequestAtMillis) + "ms")
    val json = response.body?.string()!!
    moshi.adapter(T::class.java).fromJson(json)!!
}

suspend fun MessageEvent.reply(message: String) = subject.sendMessage(message)
suspend fun MessageEvent.reply(message: Message) = subject.sendMessage(message)
