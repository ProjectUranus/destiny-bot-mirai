package net.origind.destinybot.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.awt.image.RenderedImage
import java.io.File
import javax.imageio.ImageIO

suspend fun RenderedImage.upload(contact: Contact): Image =
    coroutineScope {
        async(Dispatchers.IO) {
            val temp = File.createTempFile("img", ".png", null)
            ImageIO.write(this@upload, "png", temp)
            contact.uploadImage(temp.toExternalResource("png"))
        }
    }.await()

suspend fun MessageEvent.reply(message: String) = subject.sendMessage(message)
suspend fun MessageEvent.reply(message: Message) = subject.sendMessage(message)

