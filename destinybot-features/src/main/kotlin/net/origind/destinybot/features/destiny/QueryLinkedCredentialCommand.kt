package net.origind.destinybot.features.destiny

import net.origind.destinybot.api.command.*

object QueryLinkedCredentialCommand : AbstractCommand("/你给翻译翻译") {
    init {
        arguments += ArgumentContext("steamid", StringArgument)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val id = argument.getArgument<String>("steamid")
        val query = getMembershipFromHardLinkedCredential(id)
        if (query == null) {
            executor.sendMessage("你不叫马邦德，我叫马邦德")
            return
        }
        executor.sendMessage("好嘞。\n你的棒鸡ID：${query.membershipId}")
    }
}
