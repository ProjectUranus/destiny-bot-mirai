package cn.ac.origind.destinybot.response.bungie

data class GeneralUser(var membershipId: String = "", var displayName: String = "",
                       var uniqueName: String = "", var xboxDisplayName: String? = "", var psnDisplayName: String? = "", var steamDisplayName: String? = "",
                       var firstAccess: String = "")

data class UserMembershipData(var bungieNetUser: GeneralUser = GeneralUser(), var destinyMemberships: List<DestinyMembershipQuery> = emptyList())

class GetMembershipsResponse : SingleResponse<UserMembershipData>()

class UserSearchResponse : BungieMultiResponse<GeneralUser>()

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

class DestinyProfileSearchResponse : BungieMultiResponse<DestinyMembershipQuery>()

data class DestinyProfileComponent(var userInfo: DestinyMembershipQuery = DestinyMembershipQuery(), var characterIds: List<String> = emptyList(), var dateLastPlayed: String = "")

open class CharacterComponent(
    var dateLastPlayed: String = "", var minutesPlayedThisSession: Long = 0, var minutesPlayedTotal: Long = 0,
    var light: Int = 0, var stats: Map<String, Int> = emptyMap(),
    var raceType: Int = 3, var classType: Int = 3, var genderType: Int = 2
)

data class DestinyProfile(var profile: PrivacyData<DestinyProfileComponent> = PrivacyData(), var characters: PrivacyData<Map<String, CharacterComponent>> = PrivacyData())

data class DestinyCharacterResponseComponent(var character: PrivacyData<CharacterComponent> = PrivacyData(), var itemComponents: DestinyItemComponentSetOfint64 = DestinyItemComponentSetOfint64(),
var equipment: PrivacyData<DestinyItemsComponent> = PrivacyData())

data class DestinyItemPerkComponent(var perkHash: String = "", var iconPath: String = "", var isActive: Boolean = false, var visible: Boolean = true)
data class DestinyItemComponent(var itemHash: Long = 0, var itemInstanceId: String = "", var quantity: Int = 0, var bindStatus: Int = 0,
var location: Int = 0, var bucketHash: Long = 0, var transferStatus: Int = 0)

data class DestinyItemPerksComponent(var perks: List<DestinyItemPerkComponent> = emptyList())

data class DestinyItemsComponent(var items: List<DestinyItemComponent> = emptyList())

data class DestinyItemComponentSetOfint64(var perks: PrivacyData<Map<String, DestinyItemPerksComponent>> = PrivacyData())

class DestinyProfileResponse : SingleResponse<DestinyProfile>()

class DestinyCharacterResponse : SingleResponse<DestinyCharacterResponseComponent>()

