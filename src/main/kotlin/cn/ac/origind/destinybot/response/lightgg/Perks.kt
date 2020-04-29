package cn.ac.origind.destinybot.response.lightgg

data class DisplayProperties(var description: String? = "", var name: String? = "", var icon: String? = "")

data class Item(var perks: ItemPerks? = null, var definition: ItemDefinition? = null)

data class ItemDefinition(var _id: String? = "", var itemTypeAndTierDisplayName: String? = "", var displayProperties: DisplayProperties? = DisplayProperties())
data class ItemPerks(var curated: List<ItemPerk> = emptyList(), var favorite: List<ItemPerk> = emptyList(), var pvp: List<ItemPerk> = emptyList(), var pve: List<ItemPerk> = emptyList(), var normal: List<ItemPerk> = emptyList())
data class ItemPerk(var displayProperties: DisplayProperties? = DisplayProperties(), var _id: String? = "")