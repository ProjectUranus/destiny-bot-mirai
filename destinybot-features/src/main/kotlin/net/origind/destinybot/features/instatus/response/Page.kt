package net.origind.destinybot.features.instatus.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Page(val id: String, val subdomain: String, val name: String, val status: String)

