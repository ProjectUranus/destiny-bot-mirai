package net.origind.destinybot.core.command

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.announcement.OfflineAnnouncement
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
                val announcements = Json.decodeFromString(ListSerializer(OfflineAnnouncement.serializer()), data)
                announcements.forEach { group.announcements.publish(it) }
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
                val data = Json.encodeToString(ListSerializer(OfflineAnnouncement.serializer()), group.announcements.asFlow().map {
                    OfflineAnnouncement.create(
                        it.content,
                        it.parameters
                    )
                }.toList())
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
