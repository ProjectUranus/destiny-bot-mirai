package cn.ac.origind.destinybot.command

import cn.ac.origind.destinybot.DestinyBot
import cn.ac.origind.destinybot.reply
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.mamoe.mirai.event.MessageEventSubscribersBuilder
import net.mamoe.mirai.event.events.MessageEvent

object CommandManager {
    val dispatcher: CommandDispatcher<MessageEvent> = CommandDispatcher()
    val prefixes = mutableListOf<String>()

    fun init(subscriber: MessageEventSubscribersBuilder) {
        subscriber.content { it.substringBefore(' ') in prefixes }.invoke {
            handleCommand(this, it)
        }
        destinyBrigadierCommands(dispatcher)
    }

    suspend fun handleCommand(source: MessageEvent, command: String): Int {
        val reader = StringReader(command.trim())
        if (reader.canRead() && reader.peek() == '/') {
            reader.skip()
        }
        val result = try {
            val parse = dispatcher.parse(reader, source)
            dispatcher.execute(parse)
        } catch (e: CommandSyntaxException) {
            source.reply(e.rawMessage.string)
            if (e.input != null && e.cursor >= 0) {
                val k = e.input.length.coerceAtMost(e.cursor)
                val result = StringBuilder()
                if (k > 10) result.append("...")
                result.append(e.input.substring(0.coerceAtLeast(k - 10), k))
                if (k < e.input.length) {
                    result.append(e.input.substring(k))
                }
                result.append("<--HERE")
                source.reply(result.toString())
            }

            0
        } catch (e: Exception) {
            e.printStackTrace()
            source.reply("执行命令时失败: ${e.stackTraceToString()}")
            0
        }
        DestinyBot.logger.debug(
            "{} issued command '{}' with result {}",
            source.senderName,
            command,
            result
        )
        return result
    }
}
