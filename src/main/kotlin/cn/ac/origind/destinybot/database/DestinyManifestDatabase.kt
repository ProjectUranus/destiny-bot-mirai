package cn.ac.origind.destinybot.database

import cn.ac.origind.destinybot.DestinyBot
import cn.ac.origind.destinybot.config.DictSpec
import cn.ac.origind.destinybot.data.Lore
import cn.ac.origind.destinybot.exception.WeaponNotFoundException
import cn.ac.origind.destinybot.mapper
import cn.ac.origind.destinybot.response.lightgg.ItemDefinition
import cn.ac.origind.destinybot.searchToWeaponMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.litote.kmongo.aggregate
import org.litote.kmongo.find

/**
 * @throws WeaponNotFoundException if specified item is not found
 */
suspend fun searchItemDefinitions(displayName: String): List<ItemDefinition> {
    val itemDefinitionCollection = DestinyBot.db.getCollection("DestinyInventoryItemDefinition_chs")
    var itemSearch = displayName
    if (searchToWeaponMap.containsKey(itemSearch)) itemSearch = searchToWeaponMap[itemSearch]!!
    else {
        for ((weapon, alias) in DestinyBot.config[DictSpec.aliases]) {
            if (itemSearch.matches(Regex(alias))) {
                searchToWeaponMap[itemSearch] = weapon
                itemSearch = weapon
                break
            }
        }
    }

    val documents = itemDefinitionCollection.find("""{"displayProperties.name": "${searchToWeaponMap[itemSearch] ?: itemSearch}"}""").toList()
    if (documents.isEmpty()) throw WeaponNotFoundException("无法找到该物品，请检查你的内容并用简体中文译名搜索。")
    else {
        return documents.map { document ->
            withContext(Dispatchers.IO) { mapper.readValue(document.toJson(), ItemDefinition::class.java) }
        }
    }
}

suspend fun getRandomLore() : Lore {
    val collection = DestinyBot.db.getCollection("DestinyLoreDefinition_chs")
    val doc = collection.aggregate<Document>("""{${'$'}sample: { size: 1 }}""").firstOrNull()
    val displayProperties = doc?.get("displayProperties", Document::class.java)
    if (displayProperties?.getString("name").isNullOrEmpty()) return getRandomLore()
    return displayProperties!!.let {
        Lore(it.getString("name"), it.getString("description"))
    }
}
