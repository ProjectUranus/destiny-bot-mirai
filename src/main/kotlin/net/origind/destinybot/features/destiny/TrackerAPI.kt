package net.origind.destinybot.features.destiny

import net.origind.destinybot.core.getJson
import net.origind.destinybot.features.destiny.response.tracker.*

const val trackerEndpoint = "https://api.tracker.gg/api"
const val trackerKey = "a9660274-5674-4ad5-ad87-08e5ec9348a7"

suspend fun searchTrackerProfiles(query: String, platform: String = "steam"): List<BaseTrackerProfile> {
    val response = getJson<TrackerProfileResponse>("$trackerEndpoint/v2/destiny-2/standard/search?platform=$platform&query=$query&autocomplete=true") {
        header("TRN-Api-Key", trackerKey)
    }
    if (!response.errors.isNullOrEmpty()) {
        throw TrackerException(response.errors!!)
    }
    return response.data!!
}

suspend fun getWeaponSockets(itemId: String): TrackerWeapon {
    val response = getJson<TrackerWeaponResponse>("$trackerEndpoint/v1/destiny-2/db/items/$itemId/sockets") {
        header("TRN-Api-Key", trackerKey)
    }
    if (!response.errors.isNullOrEmpty()) {
        throw TrackerException(response.errors!!)
    }
    return response.data!!
}
