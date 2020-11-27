package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.DestinyBot.logger
import cn.ac.origind.destinybot.response.lightgg.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Paths

const val WISHLIST_URL = "https://cdn.jsdelivr.net/gh/LittleLightForDestiny/littlelight_wishlists/littlelight_default.json"
val wishlist: Wishlist = Wishlist()

class ItemNotFoundException(itemId: String, displayName: String? = null) : Exception("未找到物品 $itemId")

suspend fun fetchWishlist() = withContext(Dispatchers.IO) {
    val wishlistData = getJson<WishlistData>(WISHLIST_URL, false)
    for (wishItemData in wishlistData.data) {
        val recommendation = wishItemData.tags.firstOrNull() ?: ""
        wishlist.weaponMap.getOrPut(wishItemData.hash) { Wishlist.WishlistItem() }.apply {
            wishItemData.plugs.flatten().map { it to recommendation }.forEach {
                put(it.first, it.second)
            }
        }
    }
    logger.info("Wishlist built")
}

suspend fun getItemPerks(item: ItemDefinition) = getItemPerks(item._id!!)

suspend fun getItemPerks(itemId: String): ItemPerks = withContext(Dispatchers.IO) {
    val dir = Paths.get("destiny2_perks")
    val path = dir.resolve("$itemId.json")

    if (Files.notExists(dir)) Files.createDirectories(dir)
    if (Files.exists(path)) {
        return@withContext mapper.readValue(path.toFile(), Item::class.java).perks!!
    } else {
        val perks = getItemPerksInternal(itemId)
        val itemDefinition = Database.getItemDefinition(itemId)
        Files.write(path, mapper.writeValueAsBytes(Item(perks, itemDefinition)))
        return@withContext perks
    }
}

suspend fun getItemPerksInternal(itemId: String): ItemPerks {
    val perks = ItemPerks()
    val sockets = getWeaponSockets(itemId)

    sockets.block.socketEntries.asSequence().drop(1).take(4).forEachIndexed { index, socketEntry ->
        val plugHashes = if (socketEntry.randomizedPlugSetHash == null) listOf(socketEntry.singleInitialItemHash) else sockets.plugSets[socketEntry.randomizedPlugSetHash]!!.reusablePlugItems.map { it.plugItemHash }
        val plugs = plugHashes.map { sockets.plugs[it]!! }
        val type = when (index) {
            0 -> PerkType.BARREL
            1 -> PerkType.MAGAZINE
            2 -> PerkType.PERK1
            else -> PerkType.PERK2
        }
        for (plug in plugs) {
            perks += ItemPerk(type = type).apply {
                url = plug.imageUrl
                isCurated = socketEntry.randomizedPlugSetHash == null
                perkRecommend = wishlist.getRecommendationForPlug(itemId.toLong(), plug.hash)
                displayProperties = Database.translate(plug.name!!) ?: DisplayProperties(name = plug.name)
            }
        }
    }
    return perks
}
