package net.origind.destinybot.core.command

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.announcement.OnlineAnnouncement
import net.origind.destinybot.api.command.*
import net.origind.destinybot.core.util.ContactAdapter
import net.origind.destinybot.core.util.decodeGZIPBase64
import net.origind.destinybot.core.util.toGZIPCompressedBase64Encoded

object AnnouncementCommand: AbstractCommand("/announcement") {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).add(ContactAdapter).build()

    init {
        permission = "admin.announcement"
        registerSubcommand(Restore)
        registerSubcommand(Backup)
    }

    object Restore: AbstractCommand("restore") {
        init {
            permission = "admin.announcement.restore"
            arguments += ArgumentContext("data", StringArgument)
        }

        override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
            val data = argument.getArgument<String>("data").trim().decodeGZIPBase64()
            if (executor is MiraiUserCommandExecutor && executor.user is Member) {
                val group = executor.user.group
                if (group.botPermission.level < 1) {
                    executor.sendMessage("不是群管理无法恢复。")
                    return
                }
                val announcements = moshi.adapter<List<OnlineAnnouncement>>(Types.newParameterizedType(List::class.java, OnlineAnnouncement::class.java)).fromJson(data)
                announcements?.forEach { group.announcements.publish(it) }
            } else {
                executor.sendMessage("不是群管理无法恢复。")
            }
        }
    }

    object Backup: AbstractCommand("backup") {
        init {
            permission = "admin.announcement.backup"
        }

        override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
            if (executor is MiraiUserCommandExecutor && executor.user is Member) {
                val group = executor.user.group
                val data = moshi.adapter<List<OnlineAnnouncement>>(Types.newParameterizedType(List::class.java, OnlineAnnouncement::class.java)).toJson(group.announcements.toList())
                executor.sendMessage(data.toGZIPCompressedBase64Encoded())
            } else {
                executor.sendMessage("请在群聊中调用。")
            }
        }
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        executor.sendMessage(getHelp())
    }
}
