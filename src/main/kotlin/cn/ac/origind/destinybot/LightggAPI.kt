package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.response.lightgg.*
import com.google.gson.Gson
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.findOne
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

val lightggGson = Gson()

suspend fun getItemPerks(itemId: String): ItemPerks = withContext(Dispatchers.IO) {
    val dir = Paths.get("destiny2_perks")
    val path = dir.resolve("$itemId.json")

    if (Files.notExists(dir)) Files.createDirectories(dir)
    if (Files.exists(path)) {
        return@withContext lightggGson.fromJson(Files.readString(path, StandardCharsets.UTF_8), Item::class.java)?.perks!!
    } else {
        val perks = getItemPerksInternal(itemId)
        val itemDefJson = DestinyBot.db.getCollection("DestinyInventoryItemDefinition_chs").findOne("""{"hash": $itemId}""")?.toJson()
        Files.writeString(path, lightggGson.toJson(Item(perks, lightggGson.fromJson(itemDefJson, ItemDefinition::class.java))), StandardCharsets.UTF_8)
        return@withContext perks
    }
}

suspend fun getItemPerksInternal(itemId: String): ItemPerks {
    val regex = Regex("<div class=\"clearfix perks\">(.+)<div id=\"my-rolls\">", RegexOption.DOT_MATCHES_ALL)
    val text = regex.find(DestinyBot.client.get<String>("https://www.light.gg/db/items/$itemId"))?.groupValues?.get(0)!!
    val iconRegex = Regex("src=\"https://bungie.net(/common/destiny2_content/icons/(\\w+).png)")
    val hashRegex = Regex("<a href=\"/db/items/(\\d+)/")

    // MongoDB
    val perksCollection = DestinyBot.db.getCollection("DestinySandboxPerkDefinition_chs")
    val itemsCollection = DestinyBot.db.getCollection("DestinyInventoryItemDefinition_chs")

    val perks = ItemPerks()

    fun getPerk(hash: String): ItemPerk? {
        val perkJson = perksCollection.findOne("""{"hash": $hash}""")
            ?.toJson()
        val itemJson = itemsCollection.findOne("""{"hash": $hash}""")
            ?.toJson()
        val perk = lightggGson.fromJson(itemJson ?: perkJson, ItemPerk::class.java)
        if (perk != null) {
            perk.type = when (perk.itemTypeDisplayName) {
                "枪管" -> PerkType.BARREL
                "弹匣" -> PerkType.MAGAZINE
                else -> null
            }
        }
        return perk
    }

    // Curated rolls
    val curatedText = Regex("Not all curated rolls actually drop in-game.(.+)Random Rolls", RegexOption.DOT_MATCHES_ALL).find(text)?.groupValues?.get(0)!!
    perks.curated = curatedText.split("list-unstyled sockets").mapNotNull {
        // Curated rolls
        hashRegex.find(it)?.groupValues?.get(1)?.let { it1 -> getPerk(it1) }
    }

    val pvp = mutableListOf<ItemPerk>()
    val pve = mutableListOf<ItemPerk>()
    val favorite = mutableListOf<ItemPerk>()
    val normal = mutableListOf<ItemPerk>()
    val randomText = text.split("Hide Recommendations")[1]
    randomText.split("<ul class=\"list-unstyled\">").takeLast(4).forEachIndexed { index, str ->
        str.split("<li").forEach {
            val perk = hashRegex.find(it)?.groupValues?.get(1)?.let { it1 -> getPerk(it1) }
            perk?.type = PerkType.values()[index]
            when {
                it.contains("prefpvp") -> {
                    // PvP Perk
                    perk?.let { it1 -> pvp += it1; it1.perkRecommend = 1 }
                }
                it.contains("prefpve") -> {
                    // PvE perk
                    perk?.let { it1 -> pve += it1; it1.perkRecommend = 0 }
                }
                it.contains("pref") -> {
                    // Community Favorite
                    perk?.let { it1 -> favorite += it1; it1.perkRecommend = 2 }
                }
                else -> {
                    // Rubbish
                    perk?.let { it1 -> normal += it1; it1.perkRecommend = -1 }
                }
            }
        }
    }
    perks.pvp = pvp
    perks.pve = pve
    perks.favorite = favorite
    perks.normal = normal
    return perks
}