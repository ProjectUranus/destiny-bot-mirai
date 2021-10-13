package net.origind.destinybot.features.destiny.response.tracker

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

open class TrackerResponse<T>(var data: T? = null, var errors: List<TrackerError>? = null)

@JsonClass(generateAdapter = true)
data class TrackerError(val code: Int, val message: String)

class TrackerException(val error: List<TrackerError>) : Exception(error.joinToString { it.message + "\n" })

data class BaseTrackerProfile(
    var platformId: Int = 0, var platformSlug: String = "", var platformUserIdentifier: String = "", var platformUserHandle: String = "",
    var avatarUrl: String = ""
)

data class TrackerCharacterAttribute(var mobility: Int = 0, var resilience: Int = 0, var recovery: Int = 0)

data class TrackerCharacterMetadata(var backgroundImage: String = "", var emblemImage: String = "", var lightLevel: String = "",
                                    var level: String = "", @Json(name = "class") var classType: String = "", var race: String = "")

data class TrackerCharacter(var id: String = "", var activeCharacter: Boolean = false,
                            var metadata: TrackerCharacterMetadata = TrackerCharacterMetadata(),
                            var attributes: TrackerCharacterAttribute = TrackerCharacterAttribute()
)

class TrackerProfileResponse : TrackerResponse<List<BaseTrackerProfile>>()

class TrackerCharactersResponse : TrackerResponse<List<TrackerCharacter>>()

class TrackerWeaponResponse : TrackerResponse<TrackerWeapon>()
