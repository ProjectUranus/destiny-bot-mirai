package cn.ac.origind.rainbow6

import cn.ac.origind.destinybot.getJson

const val r6sTracker = "https://api2.r6stats.com/public-api"


suspend fun searchR6SProfile(criteria: String) {
    val response = getJson<GenericStats>("")
}
