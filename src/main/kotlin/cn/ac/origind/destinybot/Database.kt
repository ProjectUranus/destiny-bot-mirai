package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.response.bungie.DestinyActivityDefinition
import cn.ac.origind.destinybot.response.lightgg.DisplayProperties
import cn.ac.origind.destinybot.response.lightgg.ItemDefinition
import org.litote.kmongo.find
import org.litote.kmongo.findOne

object Database {
    fun getItemDefinition(itemId: String): ItemDefinition {
        return moshi.adapter(ItemDefinition::class.java).fromJson(DestinyBot.db.getCollection("DestinyInventoryItemDefinition_chs").findOne("""{"hash": $itemId}""")?.toJson()!!)!!
    }

    fun getItemDefinitions(displayName: String): List<ItemDefinition> {
        val itemDefinitionCollection = DestinyBot.db.getCollection("DestinyInventoryItemDefinition_chs")
        return itemDefinitionCollection.find("""{"displayProperties.name": "$displayName"}""").map { document ->
            mapper.readValue(document.toJson(), ItemDefinition::class.java)
        }.toList()
    }

    fun translate(name: String): DisplayProperties? {
        val itemDefinitionCollection = DestinyBot.db.getCollection("DestinyInventoryItemDefinition_eng")
        return itemDefinitionCollection.find("""{"displayProperties.name": "$name"}""").map { document ->
            getItemDefinition(document["_id"].toString()).displayProperties
        }.firstOrNull()
    }

    fun getActivity(id: Long): DestinyActivityDefinition {
        return moshi.adapter(DestinyActivityDefinition::class.java).fromJson(DestinyBot.db.getCollection("DestinyActivityDefinition_chs_chs").findOne("""{"hash": $id}""")?.toJson()!!)!!
    }
}
