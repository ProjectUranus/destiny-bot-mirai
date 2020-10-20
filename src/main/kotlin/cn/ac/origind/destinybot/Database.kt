package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.response.lightgg.ItemDefinition
import org.litote.kmongo.findOne

object Database {
    suspend fun getItemDefinition(itemId: String): ItemDefinition {
        return moshi.adapter(ItemDefinition::class.java).fromJson(DestinyBot.db.getCollection("DestinyInventoryItemDefinition_chs").findOne("""{"hash": $itemId}""")?.toJson()!!)!!
    }
}
