package net.origind.destinybot.features.bilibili.vdb

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VTuberList(var vtbs: List<VTuberMeta> = emptyList())

@JsonClass(generateAdapter = true)
data class VTuberMeta(var uuid: String = "", var type: String = "", var bot: Boolean = false,
                      var accounts: List<VTuberAccount> = emptyList(), var name: VTuberName)

@JsonClass(generateAdapter = true)
data class VTuberAccount(var id: String, var type: String, var platform: String)

@JsonClass(generateAdapter = true)
data class VTuberName(var cn: String = "", var jp: String = "", var en: String = "", var default: String = "", var extra: List<String> = emptyList())