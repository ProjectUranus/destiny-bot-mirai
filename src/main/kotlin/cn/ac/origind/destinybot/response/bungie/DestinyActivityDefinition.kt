package cn.ac.origind.destinybot.response.bungie

import cn.ac.origind.destinybot.response.lightgg.DisplayProperties
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DestinyActivityDefinition(val hash: Long, val displayProperties: DisplayProperties, val originalDisplayProperties: DisplayProperties?, val selectionDisplayProperties: DisplayProperties?)
