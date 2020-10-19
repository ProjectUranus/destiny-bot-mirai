package cn.ac.origind.destinybot.response.bungie

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude
open class SingleResponse<T> : DestinyMessageResponse() {
    var Response: T? = null
    override fun toString(): String {
        return "SingleResponse(Response=$Response) ${super.toString()}"
    }
}

data class PrivacyData<T>(var data: T? = null, var privacy: Int = 1)
