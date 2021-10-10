package net.origind.destinybot.features.destiny

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.origind.destinybot.api.command.*
import net.origind.destinybot.core.data.Database
import net.origind.destinybot.core.moshi
import net.origind.destinybot.features.destiny.data.Lore
import org.bson.Document
import org.litote.kmongo.findOne

object LoreCommand: AbstractCommand("传奇故事") {
    val lores = hashMapOf<String, String>()

    init {
        arguments += ArgumentContext("lore", StringArgument, true)
    }

    override suspend fun init() {
        coroutineScope {
            launch {
                val loreCollection = Database.db.getCollection("DestinyLoreDefinition_chs")
                lores.putAll(loreCollection.find().map { it.get("displayProperties", Document::class.java)?.getString("name")!! to it.getString("_id") })
            }
        }
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val lore = if (argument.hasArgument("lore")) {
            val collection = Database.db.getCollection("DestinyLoreDefinition_chs")
            val doc = collection.findOne("""{"_id": "${lores[argument.getArgument("lore")]}"}""")
            val displayProperties = doc?.get("displayProperties", Document::class.java)
            displayProperties!!.let {
                moshi.adapter(Lore::class.java).fromJson(it.toJson())!!
            }
        } else {
            getRandomLore()
        }

        executor.sendMessage("传奇故事：" + lore.name + '\n' + lore.lore)
    }
}
