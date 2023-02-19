package net.origind.destinybot.features.destiny

import io.ktor.client.network.sockets.*
import kotlinx.coroutines.*
import net.origind.destinybot.features.destiny.response.*
import net.origind.destinybot.features.getJson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

const val endpoint = "https://www.bungie.net/Platform"
const val key = "9654e41465f34fb6a7aea347abd5deeb"

suspend fun getDestinyProfiles(displayName: String, displayNameCode: Int, membershipType: Int): DestinyMembershipQuery? =
    getJson<GetMembershipsResponse>("$endpoint/Destiny2/SearchDestinyPlayerByBungieName/$membershipType/") {
        header("X-API-Key", key)
        post("""{"displayName": "$displayName", "displayNameCode": $displayNameCode}""".toRequestBody("application/json".toMediaType()))
    }.Response.firstOrNull()

suspend fun bungieUserToDestinyUser(displayName: String, displayNameCode: Int): DestinyMembershipQuery? = withContext(Dispatchers.IO) { getDestinyProfiles(displayName, displayNameCode, 3) }

suspend fun searchUsers(criteria: String): Set<DestinyMembershipQuery> {
    val result =
        withContext(Dispatchers.Default) { searchUsersInternal(criteria) }
    if (result.isEmpty()) {
        throw PlayerNotFoundException("没有搜索到玩家，请检查你的搜索内容")
    }

    // Filter Destiny 2 players
    val players = mutableSetOf<DestinyMembershipQuery>()
    result.map { profile ->
        GlobalScope.launch {
            try {
                val destinyMembership = bungieUserToDestinyUser(profile.bungieGlobalDisplayName, profile.bungieGlobalDisplayNameCode)
                if (destinyMembership != null) {
                    players.add(destinyMembership)
                }
            } catch (e: ConnectTimeoutException) {
                throw ConnectTimeoutException("尝试获取玩家 $profile 信息时超时。", e)
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
    getJson<UserSearchResponse>("$endpoint/User/Search/Prefix/$criteria/0/") {
        header("X-API-Key", key)
    }.Response?.searchResults!!


suspend fun searchProfiles(criteria: String): List<DestinyMembershipQuery> =
    getJson<DestinyProfileSearchResponse>("$endpoint/Destiny2/SearchDestinyPlayer/TigerSteam/$criteria/ ") {
        header("X-API-Key", key)
        post("""{"displayNamePrefix":"$criteria"}""".toRequestBody("application/json".toMediaType()))
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

