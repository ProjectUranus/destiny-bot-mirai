package cn.ac.origind.destinybot.response.bungie

import kotlinx.serialization.Serializable

@Serializable
open class BungieMultiResponse<T> : DestinyMessageResponse() {
    var Response: List<T> = emptyList()
    override fun toString(): String {
        return "MultiResponse(Response=$Response) ${super.toString()}"
    }
}