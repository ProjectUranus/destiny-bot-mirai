package net.origind.destinybot.features.destiny.response.lightgg

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WishlistData(val data: List<WishlistItemData>)

@JsonClass(generateAdapter = true)
data class WishlistItemData(val description: String = "", val plugs: List<List<Long>>, val hash: Long, val tags: List<String>, val originalWishList: String = "")

data class Wishlist(val weaponMap: MutableMap<Long, WishlistItem> = mutableMapOf()) {
    data class WishlistItem(
        // Key: Plug Hash  Value: Recommendation
        val recommendationMap: MutableMap<Long, String> = mutableMapOf()
    ) {
        fun put(perkHash: Long, recommendation: String) {
            if (recommendation.startsWith("god")) recommendationMap[perkHash] = recommendation
            else if (recommendationMap[perkHash] != null && recommendationMap[perkHash] != recommendation) recommendationMap[perkHash] = "favorite"
            else recommendationMap[perkHash] = recommendation
        }
    }

    fun getRecommendationForPlug(weaponHash: Long, plugHash: Long) = when (weaponMap[weaponHash]?.recommendationMap?.get(plugHash)) {
        "pve" -> 0
        "pvp" -> 1
        "godpve" -> 2
        "godpvp" -> 2
        "favorite" -> 2
        else -> -1
    }
}
