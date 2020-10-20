package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.response.bungie.DestinyMembershipQueryResponse
import kotlinx.coroutines.runBlocking
import org.junit.Test

class TestBot {
    @Test
    fun test() = runBlocking {
        val response = getJson<DestinyMembershipQueryResponse>("$endpoint/User/GetMembershipFromHardLinkedCredential/SteamId/76561198185224274/") {
            header("X-API-Key", key)
        }
        println(response)
    }
}
