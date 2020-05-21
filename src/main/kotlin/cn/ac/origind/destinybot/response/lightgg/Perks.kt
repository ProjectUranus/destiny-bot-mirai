package cn.ac.origind.destinybot.response.lightgg

import com.squareup.moshi.JsonClass

enum class PerkType : Comparable<PerkType> {
    BARREL, MAGAZINE, PERK1, PERK2
}

@JsonClass(generateAdapter = true)
data class DisplayProperties(var description: String? = "", var name: String? = "", var icon: String? = "")

@JsonClass(generateAdapter = true)
data class Item(var perks: ItemPerks? = null, var definition: ItemDefinition? = null)

@JsonClass(generateAdapter = true)
data class ItemDefinition(var _id: String? = "", var screenshot: String? = "", var itemTypeAndTierDisplayName: String? = "", var displayProperties: DisplayProperties? = DisplayProperties())

@JsonClass(generateAdapter = true)
data class ItemPerks(var curated: List<ItemPerk> = emptyList(), var favorite: List<ItemPerk> = emptyList(),
                     var pvp: List<ItemPerk> = emptyList(), var pve: List<ItemPerk> = emptyList(), var normal: List<ItemPerk> = emptyList(), var onlyCurated: Boolean = false) {
    val all get() = (favorite.asSequence() + pvp + pve + normal).sortedBy { -it.perkRecommend }
}

@JsonClass(generateAdapter = true)
data class ItemPerk(var displayProperties: DisplayProperties? = DisplayProperties(), var _id: String? = "", var type: PerkType? = null, var itemTypeDisplayName: String? = null, var perkRecommend: Int = -1)