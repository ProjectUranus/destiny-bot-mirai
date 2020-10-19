package cn.ac.origind.destinybot.response.lightgg

import com.fasterxml.jackson.annotation.JsonInclude

enum class PerkType : Comparable<PerkType> {
    BARREL, MAGAZINE, PERK1, PERK2
}

enum class ItemTier {
    LEGENDARY, EXOTIC, OTHER
}

@JsonInclude
data class DisplayProperties(var description: String? = "", var name: String? = "", var icon: String? = "")

@JsonInclude
data class Item(var perks: ItemPerks? = null, var definition: ItemDefinition? = null)

@JsonInclude
data class ItemDefinition(var _id: String? = "", var screenshot: String? = "", var itemTypeAndTierDisplayName: String? = "", var displayProperties: DisplayProperties? = DisplayProperties()) {
    val tier: ItemTier get() = when {
        itemTypeAndTierDisplayName?.contains("传说") == true -> ItemTier.LEGENDARY
        itemTypeAndTierDisplayName?.contains("异域") == true -> ItemTier.EXOTIC
        else -> ItemTier.OTHER
    }
}

@JsonInclude
data class ItemPerks(var curated: List<ItemPerk> = emptyList(), var favorite: List<ItemPerk> = emptyList(),
                     var pvp: List<ItemPerk> = emptyList(), var pve: List<ItemPerk> = emptyList(), var normal: List<ItemPerk> = emptyList(), var onlyCurated: Boolean = false) {
    val all get() = (favorite.asSequence() + pvp + pve + normal).sortedBy { -it.perkRecommend }
}

@JsonInclude
data class ItemPerk(var displayProperties: DisplayProperties? = DisplayProperties(), var _id: String? = "", var type: PerkType? = null, var itemTypeDisplayName: String? = null, var perkRecommend: Int = -1)
