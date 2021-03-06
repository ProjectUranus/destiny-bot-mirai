package cn.ac.origind.destinybot.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Lore(val name: String, @Json(name = "description") val lore: String)
