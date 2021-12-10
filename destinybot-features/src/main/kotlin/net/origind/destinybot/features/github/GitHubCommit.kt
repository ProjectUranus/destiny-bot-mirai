package net.origind.destinybot.features.github

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommitInfo(
    @Json(name = "sha") var sha: String,
    @Json(name = "node_id") var nodeId: String,
    @Json(name = "commit") var commit: Commit,
    @Json(name = "url") var url: String,
    @Json(name = "html_url") var htmlUrl: String,
    @Json(name = "comments_url") var commentsUrl: String,
    @Json(name = "author") var author: Author,
    @Json(name = "committer") var committer: Committer,
    @Json(name = "parents") var parents: List<Parents> = arrayListOf()

)

data class CommitAuthor(
    @Json(name = "name") var name: String,
    @Json(name = "email") var email: String,
    @Json(name = "date") var date: String
)

data class Tree(
    @Json(name = "sha") var sha: String,
    @Json(name = "url") var url: String

)


data class Verification(
    @Json(name = "verified") var verified: Boolean,
    @Json(name = "reason") var reason: String,
    @Json(name = "signature") var signature: String,
    @Json(name = "payload") var payload: String
)


data class Commit(
    @Json(name = "author") var author: GitAuthor,
    @Json(name = "committer") var committer: GitAuthor,
    @Json(name = "message") var message: String,
    @Json(name = "tree") var tree: Tree,
    @Json(name = "url") var url: String,
    @Json(name = "comment_count") var commentCount: Int,
//    @Json(name = "verification") var verification: Verification

)

data class GitAuthor(
    var name: String,
    var email: String,
    var date: String
)

data class Author(
    @Json(name = "login") var login: String,
    @Json(name = "id") var id: Int,
    @Json(name = "node_id") var nodeId: String,
    @Json(name = "avatar_url") var avatarUrl: String,
    @Json(name = "gravatar_id") var gravatarId: String,
    @Json(name = "url") var url: String,
    @Json(name = "html_url") var htmlUrl: String,
    @Json(name = "followers_url") var followersUrl: String,
    @Json(name = "following_url") var followingUrl: String,
    @Json(name = "gists_url") var gistsUrl: String,
    @Json(name = "starred_url") var starredUrl: String,
    @Json(name = "subscriptions_url") var subscriptionsUrl: String,
    @Json(name = "organizations_url") var organizationsUrl: String,
    @Json(name = "repos_url") var reposUrl: String,
    @Json(name = "events_url") var eventsUrl: String,
    @Json(name = "received_events_url") var receivedEventsUrl: String,
    @Json(name = "type") var type: String,
    @Json(name = "site_admin") var siteAdmin: Boolean

)

data class Committer(
    @Json(name = "login") var login: String,
    @Json(name = "id") var id: Int,
    @Json(name = "avatar_url") var avatarUrl: String,
    @Json(name = "url") var url: String,
    @Json(name = "html_url") var htmlUrl: String,
)

data class Parents(
    @Json(name = "sha") var sha: String,
    @Json(name = "url") var url: String,
    @Json(name = "html_url") var htmlUrl: String
)
