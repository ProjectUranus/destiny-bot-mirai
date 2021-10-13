package net.origind.destinybot.features.apex.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApexMapRotation (
    @Json(name = "battle_royale")
    val battleRoyale: Rotation,

    val arenas: Rotation,
    val ranked: RankedRotation,
    val arenasRanked: Rotation
)

@JsonClass(generateAdapter = true)
data class Rotation (
    val current: RotationData,
    val next: RotationData
)

@JsonClass(generateAdapter = true)
data class RotationData (
    val start: Long,
    val end: Long,

    @Json(name = "readableDate_start")
    val readableDateStart: String,

    @Json(name = "readableDate_end")
    val readableDateEnd: String,

    val map: String,

    @Json(name = "DurationInSecs")
    val durationInSecs: Long,

    @Json(name = "DurationInMinutes")
    val durationInMinutes: Long,

    // Ranked will have these values null
    val remainingSecs: Long?,
    val remainingMins: Long?,
    val remainingTimer: String?
)

@JsonClass(generateAdapter = true)
data class RankedRotation (
    val current: BasicRotationData,
    val next: BasicRotationData
)

@JsonClass(generateAdapter = true)
data class BasicRotationData (
    val map: String
)
