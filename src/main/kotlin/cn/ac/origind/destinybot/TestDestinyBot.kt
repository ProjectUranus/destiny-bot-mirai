package cn.ac.origind.destinybot

import io.ktor.client.HttpClient
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.runBlocking

object TestDestinyBot {

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            val client = HttpClient() {
                install(JsonFeature) {
                    serializer = GsonSerializer()
                }
            }

            // Get the content of an URL.
            val firstBytes = client.get<String>("$endpoint/Destiny2/3/Profile/4611686018494241466/?components=Profiles%2CCharacters%2CProfileCurrencies") {
                header("X-API-Key", key)
            }

            println(firstBytes)

            client.close()
        }
    }
}