package cn.ac.origind.destinybot

import cn.ac.origind.uno.drawUnoCards
import cn.ac.origind.uno.generateDefaultCards
import io.ktor.client.HttpClient
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.awt.image.RenderedImage
import java.io.File
import javax.imageio.ImageIO

object TestDestinyBot {

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            withContext(Dispatchers.IO) { ImageIO.write(drawUnoCards(generateDefaultCards()), "png", File("output.png")) }
        }
    }
}
