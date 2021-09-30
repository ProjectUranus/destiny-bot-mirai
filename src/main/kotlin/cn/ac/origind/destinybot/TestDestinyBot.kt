package cn.ac.origind.destinybot

import me.xdrop.fuzzywuzzy.FuzzySearch
import net.origind.destinybot.api.command.*

fun main() {
    val c = command {
        name = "地图轮换"
        description = "查询 Apex 当前地图轮换"
        argument("player", StringArgument)
        argument("test", BooleanArgument, "", true)
        argument("untest", IntArgument)
        executor = { c, _, _ ->
            println(c.getArgument<String>("player"))
        }
    }
    CommandManager.register(c)
    CommandManager.buildCache()
    CommandManager.parse("地图轮换 123 45", ConsoleCommandExecutor, CommandContext(0, 0, "地图轮换 123", 0))
    println(c.getHelp())

    println(FuzzySearch.extractTop("地图轮h", listOf("地图轮换", "apex开盒"), 1))
}
