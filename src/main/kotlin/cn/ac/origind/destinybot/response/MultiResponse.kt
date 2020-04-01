package cn.ac.origind.destinybot.response

import kotlinx.serialization.Serializable

@Serializable
open class MultiResponse<T> : DestinyMessageResponse() {
    var Response: List<T> = emptyList()
    override fun toString(): String {
        return "MultiResponse(Response=$Response) ${super.toString()}"
    }
}