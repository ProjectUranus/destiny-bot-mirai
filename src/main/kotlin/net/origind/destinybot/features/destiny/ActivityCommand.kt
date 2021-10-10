package net.origind.destinybot.features.destiny

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.origind.destinybot.api.command.AbstractCustomCommand
import net.origind.destinybot.api.command.ArgumentContainer
import net.origind.destinybot.api.command.CommandContext
import net.origind.destinybot.api.command.CommandExecutor
import net.origind.destinybot.core.data.Database
import org.bson.Document
import org.litote.kmongo.findOne

object ActivityCommand: AbstractCustomCommand("查询活动") {
    val activities = hashMapOf<String, String>()

    override suspend fun init() {
        coroutineScope {
            launch {
                val collection = Database.db.getCollection("DestinyActivityDefinition_chs")
                activities.putAll(collection.find().map {
                    it.get("displayProperties", Document::class.java)?.getString("name")!! to it.getString("_id")
                })
            }
        }
    }

    override suspend fun execute(
        main: String,
        argument: ArgumentContainer,
        executor: CommandExecutor,
        context: CommandContext
    ) {
        if (!main.startsWith("/")) return
        if (activities.containsKey(main.substring(1))) return

        val collection = Database.db.getCollection("DestinyActivityDefinition_chs")
        val doc = collection.findOne("""{"_id": "${activities[main.substring(1)]}"}""")
        executor.sendMessage(doc?.get("displayProperties", Document::class.java)?.getString("description") ?: "")
    }
}
