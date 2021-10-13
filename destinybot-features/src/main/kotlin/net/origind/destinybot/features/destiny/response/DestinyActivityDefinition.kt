package net.origind.destinybot.features.destiny.response

import net.origind.destinybot.features.destiny.response.lightgg.DisplayProperties
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DestinyActivityDefinition(val hash: Long, val displayProperties: DisplayProperties, val originalDisplayProperties: DisplayProperties?, val selectionDisplayProperties: DisplayProperties?)
