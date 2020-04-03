package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.DestinyBot.client
import cn.ac.origind.destinybot.response.bungie.GetMembershipsResponse
import cn.ac.origind.destinybot.response.tracker.TrackerProfileResponse
import io.ktor.client.request.get
import io.ktor.client.request.header

const val trackerEndpoint = "https://api.tracker.gg/api"
const val trackerKey = "a9660274-5674-4ad5-ad87-08e5ec9348a7"

suspend fun searchTrackerProfiles(query: String, platform: String = "steam") =
    client.get<TrackerProfileResponse>("$trackerEndpoint/v2/destiny-2/standard/search?platform=$platform&query=$query&autocomplete=true") {
        header("TRN-Api-Key", trackerKey)
    }.data

