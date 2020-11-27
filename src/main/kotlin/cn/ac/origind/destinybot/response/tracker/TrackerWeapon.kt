package cn.ac.origind.destinybot.response.tracker

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackerWeapon(val block: Block, val socketTypes: Map<Long, SocketType>, val plugs: Map<Long, Plug>, val plugSets: Map<Long, PlugSet>) {
    @JsonClass(generateAdapter = true)
    data class Block(val socketEntries: List<SocketEntry>, val intrinsicSockets: List<IntrinsicSocket>, val socketCategories: List<SocketCategory>) {
        @JsonClass(generateAdapter = true)
        data class SocketEntry(val socketTypeHash: Long, val singleInitialItemHash: Long, val reusablePlugItems: List<PlugItem>,
                               val plugSources: Int, val reusablePlugSetHash: Long?, val randomizedPlugSetHash: Long?, val defaultVisible: Boolean)

        @JsonClass(generateAdapter = true)
        data class IntrinsicSocket(val plugItemHash: Long, val socketTypeHash: Long, val defaultVisible: Boolean)

        @JsonClass(generateAdapter = true)
        data class SocketCategory(val socketCategoryHash: Long, val socketIndexes: List<Int>)
    }
    @JsonClass(generateAdapter = true)
    data class SocketType(val hash: Long, val index: Int, val socketCategoryHash: Long)

    @JsonClass(generateAdapter = true)
    data class Plug(val hash: Long, val plugCategoryHash: Long, val perks: List<Perk>, val name: String?, val imageUrl: String?) {
        @JsonClass(generateAdapter = true)
        data class Perk(val hash: Long, val visibility: String?, val name: String?)
    }

    @JsonClass(generateAdapter = true)
    data class PlugSet(val hash: Long, val index: Int, val isFakePlugSet: Boolean, val reusablePlugItems: List<PlugItem>)

    @JsonClass(generateAdapter = true)
    data class PlugItem(val plugItemHash: Long)
}
