package net.origind.destinybot.features.destiny

import net.origind.destinybot.features.destiny.response.DestinyMembershipQuery
import io.ktor.network.sockets.*
import kotlinx.coroutines.*
import net.origind.destinybot.api.command.*
import java.util.concurrent.ConcurrentHashMap

val profileQuerys = ConcurrentHashMap<Long, List<DestinyMembershipQuery>>()

object SearchCommand : AbstractCommand("命运2开盒") {
    init {
        arguments += ArgumentContext("criteria", StringArgument)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val criteria = argument.getArgument<String>("criteria")
        profileQuerys.remove(context.senderId)
        val result =
            withContext(Dispatchers.Default) { searchUsersInternal(criteria) }
        val profiles =
            withContext(Dispatchers.Default) { searchProfiles(criteria) }
        executor.sendMessage("搜索命运2玩家: $criteria")
        if (result.isEmpty() && profiles.isEmpty()) {
            executor.sendMessage("没有搜索到玩家，请检查你的搜索内容")
            return
        }

        // Filter Destiny 2 players
        val players = mutableSetOf<DestinyMembershipQuery>()
        players.addAll(profiles)
        coroutineScope {
            result.map { profile ->
                launch {
                    try {
                        val destinyMembership = bungieUserToDestinyUser(profile.membershipId)
                        if (destinyMembership != null) {
                            players.add(destinyMembership)
                        }
                    } catch (e: ConnectTimeoutException) {
                        executor.sendMessage("尝试获取玩家 ${profile.steamDisplayName ?: profile.displayName} 信息时超时。")
                    }
                }
            }.joinAll()
        }
        profileQuerys[context.senderId] = players.toList()
        executor.sendMessage(buildString {
            appendLine("搜索到玩家: ")
            players.forEachIndexed { index, profile ->
                appendLine("${index + 1}. ${profile.displayName}: ...${profile.membershipId.takeLast(3)}")
            }
            appendLine("请直接回复前面的序号（是1 2 3 不是375 668 451等等等）来获取详细信息。")
            appendLine("或者，回复 绑定 [序号] 来将该用户绑定到你的 QQ 上。")
        })
    }
}