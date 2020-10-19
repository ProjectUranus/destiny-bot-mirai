package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.response.lightgg.ItemDefinition
import com.fasterxml.jackson.module.kotlin.readValue
import org.litote.kmongo.findOne

object Database {
    suspend fun getItemDefinition(itemId: String): ItemDefinition {
        return mapper.readValue(DestinyBot.db.getCollection("DestinyInventoryItemDefinition_chs").findOne("""{"hash": $itemId}""")?.toJson()!!)
    }
}
