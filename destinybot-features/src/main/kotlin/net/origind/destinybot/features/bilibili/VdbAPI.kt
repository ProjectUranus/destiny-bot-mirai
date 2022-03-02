package net.origind.destinybot.features.bilibili

import net.origind.destinybot.features.bilibili.vdb.VTuberList
import net.origind.destinybot.features.getJson

object VdbAPI {
    const val ENDPOINT = "https://api.vtbs.moe"
    const val LIST = "https://vdb.vtbs.moe/json/list.json"

    var vTuberList = VTuberList()

    suspend fun updateList() {
        vTuberList = getJson(LIST)
    }
}