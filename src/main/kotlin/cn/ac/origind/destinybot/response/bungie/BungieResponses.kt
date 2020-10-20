package cn.ac.origind.destinybot.response.bungie

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeneralUser(var membershipId: String = "", var displayName: String = "",
                       var uniqueName: String = "", var xboxDisplayName: String? = "", var psnDisplayName: String? = "", var steamDisplayName: String? = "",
                       var firstAccess: String = "")

@JsonClass(generateAdapter = true)
data class UserMembershipData(var bungieNetUser: GeneralUser = GeneralUser(), var destinyMemberships: List<DestinyMembershipQuery> = emptyList())

@JsonClass(generateAdapter = true)
class GetMembershipsResponse : SingleResponse<UserMembershipData>()

@JsonClass(generateAdapter = true)
class UserSearchResponse : BungieMultiResponse<GeneralUser>()

@JsonClass(generateAdapter = true)
data class DestinyMembershipQuery(var membershipType: Int = 0, var membershipId: String = "", var displayName: String = "", var isPublic: Boolean = false) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DestinyMembershipQuery) return false

        if (membershipType != other.membershipType) return false
        if (membershipId != other.membershipId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = membershipType
        result = 31 * result + membershipId.hashCode()
        return result
    }
}

@JsonClass(generateAdapter = true)
class DestinyProfileSearchResponse : BungieMultiResponse<DestinyMembershipQuery>()

@JsonClass(generateAdapter = true)
data class DestinyProfileComponent(var userInfo: DestinyMembershipQuery = DestinyMembershipQuery(), var characterIds: List<String> = emptyList(), var dateLastPlayed: String = "")

@JsonClass(generateAdapter = true)
open class CharacterComponent(
    var dateLastPlayed: String = "", var minutesPlayedThisSession: Long = 0, var minutesPlayedTotal: Long = 0,
    var light: Int = 0, var stats: Map<String, Int> = emptyMap(),
    var raceType: Int = 3, var classType: Int = 3, var genderType: Int = 2,
    var emblemBackgroundPath: String = ""
)

@JsonClass(generateAdapter = true)
data class DestinyProfile(var profile: PrivacyData<DestinyProfileComponent> = PrivacyData(), var characters: PrivacyData<Map<String, CharacterComponent>> = PrivacyData())

@JsonClass(generateAdapter = true)
data class DestinyCharacterResponseComponent(var character: PrivacyData<CharacterComponent> = PrivacyData(), var itemComponents: DestinyItemComponentSetOfint64 = DestinyItemComponentSetOfint64(),
var equipment: PrivacyData<DestinyItemsComponent> = PrivacyData())

@JsonClass(generateAdapter = true)
data class DestinyItemPerkComponent(var perkHash: String = "", var iconPath: String = "", var isActive: Boolean = false, var visible: Boolean = true)
@JsonClass(generateAdapter = true)
data class DestinyItemComponent(var itemHash: Long = 0, var itemInstanceId: String = "", var quantity: Int = 0, var bindStatus: Int = 0,
var location: Int = 0, var bucketHash: Long = 0, var transferStatus: Int = 0)

@JsonClass(generateAdapter = true)
data class DestinyItemPerksComponent(var perks: List<DestinyItemPerkComponent> = emptyList())

@JsonClass(generateAdapter = true)
data class DestinyItemsComponent(var items: List<DestinyItemComponent> = emptyList())

@JsonClass(generateAdapter = true)
data class DestinyItemComponentSetOfint64(var perks: PrivacyData<Map<String, DestinyItemPerksComponent>> = PrivacyData())

@JsonClass(generateAdapter = true)
class DestinyProfileResponse : SingleResponse<DestinyProfile>()

@JsonClass(generateAdapter = true)
class DestinyCharacterResponse : SingleResponse<DestinyCharacterResponseComponent>()

@JsonClass(generateAdapter = true)
class DestinyMembershipQueryResponse: SingleResponse<DestinyMembershipQuery>()
