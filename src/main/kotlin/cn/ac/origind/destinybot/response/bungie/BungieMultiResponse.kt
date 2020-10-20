package cn.ac.origind.destinybot.response.bungie

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class BungieMultiResponse<T> : DestinyMessageResponse() {
    var Response: List<T> = emptyList()
    override fun toString(): String {
        return "MultiResponse(Response=$Response) ${super.toString()}"
    }
}
