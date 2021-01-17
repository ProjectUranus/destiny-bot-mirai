package cn.ac.origind.destinybot.image

import cn.ac.origind.destinybot.DestinyBot.bot
import cn.ac.origind.destinybot.client
import cn.ac.origind.destinybot.rawClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import javax.imageio.ImageIO

/**
 * @param icon icon path in displayProperties
 * @see cn.ac.origind.destinybot.response.lightgg.DisplayProperties.icon
 */
suspend fun getImage(icon: String, proxy: Boolean = true): BufferedImage {
    try {
        return ImageIO.read(getImageFile(icon, proxy))
    } catch (e: IOException) {
        bot.logger.warning(e)
        throw e
    }
}

suspend fun getImageFile(icon: String, proxy: Boolean = true): File = withContext(Dispatchers.IO) {
    // Check Directory
    val dir = Paths.get(when {
        icon.startsWith("/common/destiny2_content/icons") -> "destiny2_icons"
        icon.startsWith("https://titles.trackercdn.com/destiny/common/destiny2_content/icons") -> "destiny2_icons"
        icon.startsWith("/common/destiny2_content/screenshots") -> "destiny2_screenshots"
        else -> "destiny2_images"
    }).toAbsolutePath()
    val fileName = icon.substringAfterLast('/')
    val path = dir.resolve(fileName)

    if (Files.notExists(dir)) Files.createDirectories(dir)
    if (Files.exists(path)) {
        return@withContext path.toFile()
    }


    val request = Request.Builder().apply {
        url(if (icon.startsWith("http")) icon else "https://www.bungie.net$icon")
    }.build()

    val response = if (proxy) client.newCall(request).execute() else rawClient.newCall(request).execute()
    Files.write(path, response.body?.bytes()!!, StandardOpenOption.WRITE, StandardOpenOption.CREATE)

    return@withContext path.toFile()
}
