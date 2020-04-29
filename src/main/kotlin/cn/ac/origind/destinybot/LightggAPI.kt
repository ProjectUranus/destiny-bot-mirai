package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.response.lightgg.ItemPerk
import cn.ac.origind.destinybot.response.lightgg.ItemPerks
import com.google.gson.Gson
import io.ktor.client.request.get
import org.litote.kmongo.findOne

val lightggGson = Gson()

suspend fun getItemPerks(itemId: String): ItemPerks {
    val regex = Regex("<div class=\"clearfix perks\">(.+)<div id=\"my-rolls\">", RegexOption.DOT_MATCHES_ALL)
    val text = regex.find(DestinyBot.client.get<String>("https://www.light.gg/db/items/$itemId"))?.groupValues?.get(0)!!
    val iconRegex = Regex("src=\"https://bungie.net(/common/destiny2_content/icons/(\\w+).png)")

    // MongoDB
    val perksCollection = DestinyBot.db.getCollection("DestinySandboxPerkDefinition_chs")

    val perks = ItemPerks()

    fun getPerk(icon: String) = lightggGson.fromJson(perksCollection.findOne("""{"displayProperties.icon": "$icon"}""")
        ?.toJson(), ItemPerk::class.java)

    // Curated rolls
    val curatedText = Regex("Not all curated rolls actually drop in-game.(.+)Random Rolls", RegexOption.DOT_MATCHES_ALL).find(text)?.groupValues?.get(0)!!
    perks.curated = curatedText.split("list-unstyled sockets").mapNotNull {
        // Curated rolls
        iconRegex.find(it)?.groupValues?.get(1)?.let { it1 -> getPerk(it1) }
    }

    val pvp = mutableListOf<ItemPerk>()
    val pve = mutableListOf<ItemPerk>()
    val favorite = mutableListOf<ItemPerk>()
    val normal = mutableListOf<ItemPerk>()
    val randomText = text.split("Hide Recommendations")[1]
    randomText.split("<li").forEach {
        val icon = iconRegex.find(it)?.groupValues?.get(1)
        if (it.contains("prefpvp")) {
            // PvP Perk
            icon?.let { it1 -> getPerk(it1) }?.let { it1 -> pvp += it1 }
        } else if (it.contains("prefpve")) {
            // PvE perk
            icon?.let { it1 -> getPerk(it1) }?.let { it1 -> pve += it1 }
        } else if (it.contains("pref")) {
            // Community Favorite
            icon?.let { it1 -> getPerk(it1) }?.let { it1 -> favorite += it1 }
        } else {
            // Rubbish
            icon?.let { it1 -> getPerk(it1) }?.let { it1 -> normal += it1 }
        }
    }
    perks.pvp = pvp
    perks.pve = pve
    perks.favorite = favorite
    perks.normal = normal
    return perks
}