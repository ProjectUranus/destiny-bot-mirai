package cn.ac.origind.destinybot.response.tracker

import com.google.gson.annotations.SerializedName

open class TrackerResponse<T>(var data: T? = null)

data class BaseTrackerProfile(
    var platformId: Int = 0, var platformSlug: String = "", var platformUserIdentifier: String = "", var platformUserHandle: String = "",
    var avatarUrl: String = ""
)

data class TrackerCharacterAttribute(var mobility: Int = 0, var resilience: Int = 0, var recovery: Int = 0)

data class TrackerCharacterMetadata(var backgroundImage: String = "", var emblemImage: String = "", var lightLevel: String = "",
var level: String = "", @SerializedName("class") var classType: String = "", var race: String = "")

data class TrackerCharacter(var id: String = "", var activeCharacter: Boolean = false,
                            var metadata: TrackerCharacterMetadata = TrackerCharacterMetadata(),
                            var attributes: TrackerCharacterAttribute = TrackerCharacterAttribute()
)

class TrackerProfileResponse : TrackerResponse<List<BaseTrackerProfile>>()

class TrackerCharactersResponse : TrackerResponse<List<TrackerCharacter>>()
