package cn.ac.origind.destinybot

import cn.ac.origind.destinybot.DestinyBot.client
import cn.ac.origind.destinybot.response.bungie.*
import io.ktor.client.request.get
import io.ktor.client.request.header

const val endpoint = "https://www.bungie.net/Platform"
const val key = "9654e41465f34fb6a7aea347abd5deeb"

suspend fun getDestinyProfiles(membershipId: String, membershipType: Int) =
    client.get<GetMembershipsResponse>("$endpoint/User/GetMembershipsById/$membershipId/$membershipType/") {
        header("X-API-Key", key)
    }.Response


suspend fun searchUsers(criteria: String) =
    client.get<UserSearchResponse>("$endpoint/User/SearchUsers/?q=$criteria") {
        header("X-API-Key", key)
    }.Response


suspend fun searchProfiles(criteria: String) =
    client.get<DestinyProfileSearchResponse>("$endpoint/Destiny2/SearchDestinyPlayer/TigerSteam/$criteria/ ") {
        header("X-API-Key", key)
    }.Response

suspend fun getProfile(membershipType: Int, membershipId: String) =
    client.get<DestinyProfileResponse>("$endpoint/Destiny2/${membershipType}/Profile/${membershipId}/?components=Profiles%2CCharacters%2CProfileCurrencies") {
        header("X-API-Key", key)
    }.Response

suspend fun getCharacter(membershipType: Int, membershipId: String, characterId: String) =
    client.get<DestinyCharacterResponse>("$endpoint/Destiny2/${membershipType}/Profile/${membershipId}/Character/${characterId}/?components=Characters%2CCharacterInventories%2CCharacterEquipment%2CItemPerks") {
        header("X-API-Key", key)
    }.Response