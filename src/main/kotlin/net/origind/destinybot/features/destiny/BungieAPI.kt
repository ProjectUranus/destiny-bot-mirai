package net.origind.destinybot.features.destiny

import io.ktor.network.sockets.*
import kotlinx.coroutines.*
import net.origind.destinybot.core.getJson
import net.origind.destinybot.features.destiny.response.*

const val endpoint = "https://www.bungie.net/Platform"
const val key = "9654e41465f34fb6a7aea347abd5deeb"

suspend fun getDestinyProfiles(membershipId: String, membershipType: Int): UserMembershipData? =
    getJson<GetMembershipsResponse>("$endpoint/User/GetMembershipsById/$membershipId/$membershipType/") {
        header("X-API-Key", key)
    }.Response

suspend fun bungieUserToDestinyUser(membershipId: String): DestinyMembershipQuery? = withContext(Dispatchers.IO) { getDestinyProfiles(membershipId, 3) }?.destinyMemberships?.firstOrNull()

suspend fun searchUsers(criteria: String): Set<DestinyMembershipQuery> {
    val result =
        withContext(Dispatchers.Default) { searchUsersInternal(criteria) }
    val profiles =
        withContext(Dispatchers.Default) { searchProfiles(criteria) }
    if (result.isEmpty() && profiles.isEmpty()) {
        throw PlayerNotFoundException("没有搜索到玩家，请检查你的搜索内容")
    }

    // Filter Destiny 2 players
    val players = mutableSetOf<DestinyMembershipQuery>()
    players.addAll(profiles)
    result.map { profile ->
        GlobalScope.launch {
            try {
                val destinyMembership = bungieUserToDestinyUser(profile.membershipId)
                if (destinyMembership != null) {
                    players.add(destinyMembership)
                }
            } catch (e: ConnectTimeoutException) {
                throw ConnectTimeoutException("尝试获取玩家 ${profile.steamDisplayName ?: profile.displayName} 信息时超时。", e)
            }
        }
    }.joinAll()
    return players
}

suspend fun searchUsersProfile(criteria: String) =
    searchUsers(criteria).map {
        withContext(Dispatchers.IO) { getProfile(it.membershipType, it.membershipId) }
    }


suspend fun searchUsersInternal(criteria: String): List<GeneralUser> =
    getJson<UserSearchResponse>("$endpoint/User/SearchUsers/?q=$criteria") {
        header("X-API-Key", key)
    }.Response


suspend fun searchProfiles(criteria: String): List<DestinyMembershipQuery> =
    getJson<DestinyProfileSearchResponse>("$endpoint/Destiny2/SearchDestinyPlayer/TigerSteam/$criteria/ ") {
        header("X-API-Key", key)
    }.Response

suspend fun getProfile(membershipType: Int, membershipId: String): DestinyProfile? =
    getJson<DestinyProfileResponse>("$endpoint/Destiny2/${membershipType}/Profile/${membershipId}/?components=Profiles%2CCharacters%2CProfileCurrencies") {
        header("X-API-Key", key)
    }.Response

suspend fun getCharacter(membershipType: Int, membershipId: String, characterId: String): DestinyCharacterResponseComponent? =
    getJson<DestinyCharacterResponse>("$endpoint/Destiny2/${membershipType}/Profile/${membershipId}/Character/${characterId}/?components=Characters%2CCharacterInventories%2CCharacterEquipment%2CItemPerks") {
        header("X-API-Key", key)
    }.Response

suspend fun getMembershipFromHardLinkedCredential(credential: String, crType: String = "SteamId"): DestinyMembershipQuery? =
    getJson<DestinyMembershipQueryResponse>("$endpoint/User/GetMembershipFromHardLinkedCredential/${crType}/${credential}/") {
        header("X-API-Key", key)
    }.Response

