package net.origind.destinybot.features.apex

import net.origind.destinybot.features.apex.response.ApexMapRotation
import net.origind.destinybot.features.apex.response.ApexPlayer
import net.origind.destinybot.features.getJson
import java.io.IOException

const val APEX_API_ENDPOINT = "https://api.mozambiquehe.re"
const val APEX_API_KEY = "7wMiKAiijuIC1Ts99Ek8"

class ApexApiException(message: String) : IOException(message)

fun localizeMapName(map: String) = when(map) {
    "Kings Canyon" -> "诸王峡谷"
    "World's Edge" -> "世界尽头"
    "Olympus" -> "奥林匹斯"
    "Storm Point" -> "风暴点"
    else -> map
}

fun localizeRankName(rank: String) = when(rank) {
    "Bronze" -> "青铜"
    "Silver" -> "白银"
    "Gold" -> "黄金"
    "Platinum" -> "铂金"
    "Diamond" -> "钻石"
    "Master" -> "大师"
    "Apex Predator" -> "猎杀"
    else -> "无"
}

suspend fun searchApexPlayer(name: String): ApexPlayer
     = getJson("$APEX_API_ENDPOINT/bridge?version=5&platform=PC&player=${name}&auth=$APEX_API_KEY")

suspend fun getMapRotation() : ApexMapRotation = getJson("$APEX_API_ENDPOINT/maprotation?version=2&auth=$APEX_API_KEY")

