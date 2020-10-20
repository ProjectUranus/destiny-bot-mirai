package cn.ac.origind.destinybot.response.bungie

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class SingleResponse<T> : DestinyMessageResponse() {
    var Response: T? = null
    override fun toString(): String {
        return "SingleResponse(Response=$Response) ${super.toString()}"
    }
}

@JsonClass(generateAdapter = true)
data class PrivacyData<T>(var data: T? = null, var privacy: Int = 1)
