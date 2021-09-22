package cn.ac.origind.destinybot

import cn.ac.origind.command.*

fun main() {
    val c = command("地图轮换") {
        description = "查询 Apex 当前地图轮换"
        argument("player", StringArgument)
        argument("test", BooleanArgument, "", true)
        argument("untest", IntArgument)
        execute = { c, _, _ ->
            println(c.getArgument<String>("player"))
        }
    }
    CommandManager.register(c)
    CommandManager.buildCache()
    CommandManager.parse("地图轮换 123 45S", ConsoleCommandExecutor, CommandContext(0, 0, "地图轮换 123", 0))
}
