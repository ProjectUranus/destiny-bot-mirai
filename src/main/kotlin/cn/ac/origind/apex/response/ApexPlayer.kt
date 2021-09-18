package cn.ac.origind.apex.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApexPlayer (
    val global: GlobalData,
    val realtime: RealtimeData,
//    val legends: LegendsData,

    val total: TotalData,
    val Error: String?
)

@JsonClass(generateAdapter = true)
data class GlobalData (
    val name: String,
    val uid: Long,
    val avatar: String,
    val platform: String,
    val level: Long,
    val toNextLevelPercent: Long,
    val internalUpdateCount: Long,
    val bans: Bans,
    val rank: Arena,
    val arena: Arena,
    val battlepass: Battlepass,
    val badges: List<Badge>?
)

@JsonClass(generateAdapter = true)
data class Arena (
    val rankScore: Long,
    val rankName: String,
    val rankDiv: Long,
    val ladderPosPlatform: Long,
    val rankImg: String,
    val rankedSeason: String
)

@JsonClass(generateAdapter = true)
data class Bans (
    val isActive: Boolean,
    val remainingSeconds: Long,

    @Json(name = "last_banReason")
    val lastBanReason: String
)

@JsonClass(generateAdapter = true)
data class Battlepass (
    val level: String,
    val history: Map<String, Long>
)

@JsonClass(generateAdapter = true)
data class LegendsData (
    val selected: Selected,
    val all: All
)

@JsonClass(generateAdapter = true)
data class All (
    @Json(name = "Revenant")
    val revenant: LegendStats,

    @Json(name = "Crypto")
    val crypto: LegendStats,

    @Json(name = "Horizon")
    val horizon: LegendStats,

    @Json(name = "Gibraltar")
    val gibraltar: LegendStats,

    @Json(name = "Wattson")
    val wattson: LegendStats,

    @Json(name = "Fuse")
    val fuse: LegendStats,

    @Json(name = "Bangalore")
    val bangalore: LegendStats,

    @Json(name = "Wraith")
    val wraith: LegendStats,

    @Json(name = "Octane")
    val octane: LegendStats,

    @Json(name = "Bloodhound")
    val bloodhound: LegendStats,

    @Json(name = "Caustic")
    val caustic: LegendStats,

    @Json(name = "Lifeline")
    val lifeline: LegendStats,

    @Json(name = "Pathfinder")
    val pathfinder: LegendStats,

    @Json(name = "Loba")
    val loba: LegendStats,

    @Json(name = "Mirage")
    val mirage: LegendStats,

    @Json(name = "Rampart")
    val rampart: LegendStats,

    @Json(name = "Valkyrie")
    val valkyrie: LegendStats,

    @Json(name = "Seer")
    val seer: LegendStats
)

@JsonClass(generateAdapter = true)
data class LegendStats (
    val data: List<LegendStat>?,
    val gameInfo: LegendGameInfo?,

    @Json(name = "ImgAssets")
    val imgAssets: ImgAssets
)

@JsonClass(generateAdapter = true)
data class LegendStat (
    val name: String,
    val value: Long,
    val key: String,
    val rank: Rank,
    val rankPlatformSpecific: Rank
)

@JsonClass(generateAdapter = true)
data class Rank (
    val rankPos: Long,
    val topPercent: Double
)

@JsonClass(generateAdapter = true)
data class LegendGameInfo (
    val badges: List<Badge>
)

@JsonClass(generateAdapter = true)
data class ImgAssets (
    val icon: String,
    val banner: String
)


@JsonClass(generateAdapter = true)
data class Selected (
    @Json(name = "LegendName")
    val legendName: String,

    val data: List<SelectedDatum>,
    val gameInfo: SelectedGameInfo,

    @Json(name = "ImgAssets")
    val imgAssets: ImgAssets
)

@JsonClass(generateAdapter = true)
data class SelectedDatum (
    val name: String,
    val value: Long,
    val key: String
)

@JsonClass(generateAdapter = true)
data class SelectedGameInfo (
    val skin: String,
    val skinRarity: String,
    val frame: String,
    val frameRarity: String,
    val pose: String,
    val poseRarity: String,
    val intro: String,
    val introRarity: String,
    val badges: List<Badge>
)

@JsonClass(generateAdapter = true)
data class Badge (
    val name: String?,
    val value: Long?,
    val category: String?
)

@JsonClass(generateAdapter = true)
data class RealtimeData (
    val lobbyState: String,
    val isOnline: Long,
    val isInGame: Long,
    val canJoin: Long,
    val partyFull: Long,
    val selectedLegend: String,
    val currentState: String,
    val currentStateSinceTimestamp: Long,
    val currentStateAsText: String
)

@JsonClass(generateAdapter = true)
data class TotalData (
    val kills: Badge?,

    @Json(name = "wins_season_3")
    val winsSeason3: Badge?,

    @Json(name = "wins_season_4")
    val winsSeason4: Badge?,

    @Json(name = "games_played")
    val gamesPlayed: Badge?,

    @Json(name = "wins_season_1")
    val winsSeason1: Badge?,

    @Json(name = "creeping_barrage_damage")
    val creepingBarrageDamage: Badge?,

    @Json(name = "kills_season_1")
    val killsSeason1: Badge?,

    @Json(name = "wins_season_2")
    val winsSeason2: Badge?,

    @Json(name = "top_3")
    val top3: Badge?,

    @Json(name = "beast_of_the_hunt_kills")
    val beastOfTheHuntKills: Badge?,

    val damage: Badge?,

    @Json(name = "dropped_items_for_squadmates")
    val droppedItemsForSquadmates: Badge?,

    @Json(name = "pistol_kills")
    val pistolKills: Badge?,

    @Json(name = "beacons_scanned")
    val beaconsScanned: Badge?,

    @Json(name = "ar_kills")
    val Badge: Badge?,

    val kd: Kd?
)

@JsonClass(generateAdapter = true)
data class Kd (
    val value: String,
    val name: String
)
