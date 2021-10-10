package net.origind.destinybot.features.destiny.response.lightgg

import com.squareup.moshi.JsonClass

enum class PerkType : Comparable<PerkType> {
    BARREL, MAGAZINE, PERK1, PERK2
}

enum class ItemTier {
    LEGENDARY, EXOTIC, OTHER
}

@JsonClass(generateAdapter = true)
data class DisplayProperties(var description: String? = "", var name: String? = "", var icon: String? = "")

@JsonClass(generateAdapter = true)
data class Item(var perks: ItemPerks? = null, var definition: ItemDefinition? = null)

@JsonClass(generateAdapter = true)
data class ItemDefinition(var _id: String? = "", var screenshot: String? = "", var itemTypeAndTierDisplayName: String? = "", var displayProperties: DisplayProperties? = DisplayProperties()) {
    val tier: ItemTier get() = when {
        itemTypeAndTierDisplayName?.contains("传说") == true -> ItemTier.LEGENDARY
        itemTypeAndTierDisplayName?.contains("异域") == true -> ItemTier.EXOTIC
        else -> ItemTier.OTHER
    }
}

@JsonClass(generateAdapter = true)
data class ItemPerks(var curated: List<ItemPerk> = emptyList(), var favorite: List<ItemPerk> = emptyList(),
                     var pvp: List<ItemPerk> = emptyList(), var pve: List<ItemPerk> = emptyList(), var normal: List<ItemPerk> = emptyList(), var onlyCurated: Boolean = false) {
    operator fun plusAssign(perk: ItemPerk) {
        if (perk.isCurated) curated += perk
        else when(perk.perkRecommend) {
            -1 -> normal += perk
            0 -> pve += perk
            1 -> pvp += perk
            2 -> favorite += perk
        }
    }
    val all get() = (favorite.asSequence() + pvp + pve + normal).sortedBy { -it.perkRecommend }
}

@JsonClass(generateAdapter = true)
data class ItemPerk(var displayProperties: DisplayProperties? = DisplayProperties(), var isCurated: Boolean = false,
                    var _id: String? = "", var type: PerkType? = null, var itemTypeDisplayName: String? = null, var perkRecommend: Int = -1, var url: String? = null)
