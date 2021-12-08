package net.origind.destinybot.features.destiny

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.origind.destinybot.api.command.*
import net.origind.destinybot.features.destiny.image.toByteArray
import net.origind.destinybot.features.destiny.image.toImage
import net.origind.destinybot.features.destiny.response.lightgg.ItemDefinition
import net.origind.destinybot.features.destiny.response.lightgg.ItemPerks

object PerkCommand : AbstractCommand("perk") {
    init {
        arguments += ArgumentContext("item", StringArgument)
    }

    override suspend fun init() {
        coroutineScope {
            launch {
                fetchWishlist()
            }
        }
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        val it = argument.getArgument<String>("item")
        if (it.isBlank()) return
        for (item in searchItemDefinitions(it)) {
            try {
                val perks = getItemPerks(item._id!!)
                replyPerks(item, perks, executor)
            } catch (e: WeaponNotFoundException) {
                executor.sendMessage(e.localizedMessage ?: "")
            } catch (e: ItemNotFoundException) {
                executor.sendMessage("搜索失败: ${e.localizedMessage}, 正在尝试其他方式")
            } catch (e: Exception) {
                executor.sendMessage("搜索失败：${e.localizedMessage}, 正在尝试其他方式")
            }
        }
    }

    suspend fun replyPerks(item: ItemDefinition, perks: ItemPerks, executor: CommandExecutor) {

        if (executor is UserCommandExecutor)
            executor.sendImage(item.toImage(perks).toByteArray())
        executor.sendMessage(buildString {
            appendLine("信息来自 Little Light 愿望单")
            appendLine(item.displayProperties?.name + " " + item.itemTypeAndTierDisplayName)
            appendLine(item.displayProperties?.description)
            appendLine()
            append("官Roll(可能不掉落): ")
            appendLine(perks.curated.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
            if (perks.favorite.isNotEmpty()) {
                append("社区精选 Perk: ")
                appendLine(perks.favorite.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
            }
            if (perks.pvp.isNotEmpty()) {
                append("PvP Perk: ")
                appendLine(perks.pvp.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
            }
            if (perks.pve.isNotEmpty()) {
                append("PvE Perk: ")
                appendLine(perks.pve.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
            }
            if (perks.normal.isNotEmpty()) {
                append("其他 Perk: ")
                append(perks.normal.joinToString(separator = ", ") { it.displayProperties?.name.toString() })
            }
        })
    }
}
