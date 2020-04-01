package cn.ac.origind.destinybot.response

data class GeneralUser(var membershipId: String = "", var displayName: String = "",
                       var uniqueName: String = "", var xboxDisplayName: String? = "", var psnDisplayName: String? = "", var steamDisplayName: String? = "",
                       var firstAccess: String = "")

data class UserMembershipData(var bungieNetUser: GeneralUser = GeneralUser(), var destinyMemberships: List<DestinyMembershipQuery> = emptyList())

class GetMembershipsResponse : SingleResponse<UserMembershipData>()

class UserSearchResponse : MultiResponse<GeneralUser>()

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

class DestinyProfileSearchResponse : MultiResponse<DestinyMembershipQuery>()

data class DestinyProfileComponent(var userInfo: DestinyMembershipQuery = DestinyMembershipQuery(), var characterIds: List<String> = emptyList(), var dateLastPlayed: String = "")

data class CharacterComponent(
    var dateLastPlayed: String = "", var minutesPlayedThisSession: Long = 0, var minutesPlayedTotal: Long = 0,
    var light: Int = 0, var stats: Map<String, Int> = emptyMap(),
    var raceType: Int = 3, var classType: Int = 3, var genderType: Int = 2,
)

data class DestinyProfile(var profile: PrivacyData<DestinyProfileComponent> = PrivacyData(), var characters: PrivacyData<Map<String, CharacterComponent>> = PrivacyData())

class DestinyProfileResponse : SingleResponse<DestinyProfile>()