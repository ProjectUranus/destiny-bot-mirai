package cn.ac.origind.destinybot.command

import cn.ac.origind.destinybot.response.QueryType
import cn.ac.origind.destinybot.response.bungie.DestinyMembershipQuery
import java.time.Duration
import java.time.Instant

open class Query<T>(val type: QueryType, val time: Instant, val timeout: Duration, val data: T) {
    fun isTimeout() = time.plus(timeout).isBefore(Instant.now())
}

class CharacterQuery(type: QueryType, time: Instant, timeout: Duration, data: List<DestinyMembershipQuery>) : Query<List<DestinyMembershipQuery>>(type, time, timeout, data)
