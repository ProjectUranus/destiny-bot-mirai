package cn.ac.origind.destinybot.image

import cn.ac.origind.destinybot.DestinyBot
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.utils.toExternalImage
import net.mamoe.mirai.utils.upload
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import javax.imageio.ImageIO

/**
 * @param icon icon path in displayProperties
 * @see cn.ac.origind.destinybot.response.lightgg.DisplayProperties.icon
 */
suspend fun getImage(icon: String): BufferedImage = withContext(Dispatchers.IO) {
    // Check Directory
    val dir = Paths.get(when {
        icon.startsWith("/common/destiny2_content/icons") -> "destiny2_icons"
        icon.startsWith("/common/destiny2_content/screenshots") -> "destiny2_screenshots"
        else -> "destiny2_images"
    }).toAbsolutePath()
    val fileName = icon.substringAfterLast('/')
    val path = dir.resolve(fileName)

    if (Files.notExists(dir)) Files.createDirectories(dir)
    if (Files.exists(path)) {
        return@withContext ImageIO.read(path.toFile())
    }

    Files.write(path, DestinyBot.client.get<ByteArray>(if (icon.startsWith("http")) icon else "https://www.bungie.net$icon"), StandardOpenOption.WRITE, StandardOpenOption.CREATE)
    return@withContext ImageIO.read(path.toFile())
}

/**
 * @param icon icon path in displayProperties
 * @see cn.ac.origind.destinybot.response.lightgg.DisplayProperties.icon
 */
suspend fun getUploadedImage(icon: String, contact: Contact): Image = withContext(Dispatchers.IO) { getImage(icon).toExternalImage().upload(contact) }