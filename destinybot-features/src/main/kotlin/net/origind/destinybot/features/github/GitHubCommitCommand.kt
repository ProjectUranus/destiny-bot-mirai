package net.origind.destinybot.features.github

import net.origind.destinybot.api.command.*
import net.origind.destinybot.features.getJson

object GitHubCommitCommand : AbstractCommand("commit") {
    val regex = Regex("\\w+/\\w+")

    init {
        arguments += ArgumentContext("repo", StringArgument)
        arguments += ArgumentContext("count", IntArgument, true)
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val repo = argument.getArgument<String>("repo")
        if (!repo.matches(regex)) {
            executor.sendMessage("Repo name invalid")
            return
        }

        val count = argument.getArgument("count") ?: 5
        val infos = getJson<List<CommitInfo>>("https://api.github.com/repos/$repo/commits?per_page=$count")
        executor.sendMessage("GitHub: ${repo}\n展示最后${count}条提交：\n" + infos.joinToString("\n", transform = this::formatInfo))
    }

    fun formatInfo(info: CommitInfo) =
        buildString {
            appendLine(info.commit.author.name + ": " + info.commit.message)
        }

}
