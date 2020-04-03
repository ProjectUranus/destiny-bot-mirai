package cn.ac.origind.destinybot.response.bungie

import kotlinx.serialization.Serializable

@Serializable
open class SingleResponse<T> : DestinyMessageResponse() {
    var Response: T? = null
    override fun toString(): String {
        return "SingleResponse(Response=$Response) ${super.toString()}"
    }
}

data class PrivacyData<T>(var data: T? = null, var privacy: Int = 1)