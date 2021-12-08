package net.origind.destinybot.features.destiny

import io.ktor.client.features.*
import net.origind.destinybot.api.command.AbstractCustomCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor

object SearchChooseResultCommand : AbstractCustomCommand("选择查询结果") {
    override suspend fun execute(
        main: String,
        argument: ArgumentContainer,
        executor: CommandExecutor,
        context: CommandContext
    ) {
        if (profileQuerys[context.senderId].isNullOrEmpty())
            return

        val choice = main.toIntOrNull() ?: return

        val result = profileQuerys[context.senderId]!!
        val index = choice - 1
        if (result.size < index + 1) return
        val destinyMembership = result[index]
        try {
            PlayerProfileCommand.replyProfile(
                destinyMembership.membershipType,
                destinyMembership.membershipId,
                executor
            )
        } catch (e: ServerResponseException) {
            executor.sendMessage("获取详细信息时失败，请重试。\n${e.localizedMessage}")
        }

    }
}
