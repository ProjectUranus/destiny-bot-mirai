package net.origind.destinybot.features.destiny

import net.origind.destinybot.core.DestinyBot
import net.origind.destinybot.core.config.DictSpec
import net.origind.destinybot.core.data.Database
import net.origind.destinybot.core.moshi
import net.origind.destinybot.features.destiny.data.Lore
import net.origind.destinybot.features.destiny.response.lightgg.ItemDefinition
import org.bson.Document
import org.litote.kmongo.aggregate
import java.util.concurrent.ConcurrentHashMap

val searchToWeaponMap = ConcurrentHashMap<String, String>()

/**
 * @throws WeaponNotFoundException if specified item is not found
 */
suspend fun searchItemDefinitions(displayName: String): List<ItemDefinition> {
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

    val documents = Database.getItemDefinitions(itemSearch)
    if (documents.isEmpty()) throw WeaponNotFoundException("无法找到该物品，请检查你的内容并用简体中文译名搜索。")
    else return documents
}

suspend fun getRandomLore() : Lore {
    val collection = Database.db.getCollection("DestinyLoreDefinition_chs")
    val doc = collection.aggregate<Document>("""{${'$'}sample: { size: 1 }}""").firstOrNull()
    val displayProperties = doc?.get("displayProperties", Document::class.java)
    if (displayProperties?.getString("name").isNullOrEmpty()) return getRandomLore()
    return displayProperties!!.let {
        moshi.adapter(Lore::class.java).fromJson(it.toJson())!!
    }
}
