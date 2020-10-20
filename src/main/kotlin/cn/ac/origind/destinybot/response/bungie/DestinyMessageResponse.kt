package cn.ac.origind.destinybot.response.bungie

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class DestinyMessageResponse {
    var ErrorCode: Int = 1
    var ThrottleSeconds: Int = 0
    var ErrorStatus: String = ""
    var Message: String = ""
    var MessageData: Map<String, String> = emptyMap()
    override fun toString(): String {
        return "DestinyMessageResponse(ErrorCode=$ErrorCode, ThrottleSeconds=$ThrottleSeconds, ErrorStatus='$ErrorStatus', Message='$Message', MessageData=${MessageData})"
    }
}
