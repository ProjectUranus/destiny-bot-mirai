package net.origind.destinybot.features.destiny

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.origind.destinybot.api.command.*

object TrackerCommand : AbstractCommand("/tracker") {
    init {
        arguments += ArgumentContext("criteria", StringArgument)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val criteria = argument.getArgument<String>("criteria")
        val result = withContext(Dispatchers.Default) {
            searchTrackerProfiles(criteria)
        }
        executor.sendMessage("搜索Tracker上的命运2玩家: $criteria")
        if (result.isEmpty()) {
            executor.sendMessage("没有搜索到玩家，请检查你的搜索内容")
            return
        }
        executor.sendMessage(buildString {
            appendLine("搜索到玩家: ")
            result.forEachIndexed { index, profile ->
                appendLine("${index + 1}. ${profile.platformUserHandle}: https://destinytracker.com/destiny-2/profile/${profile.platformSlug}/${profile.platformUserIdentifier}/overview")
            }
        })
    }
}
