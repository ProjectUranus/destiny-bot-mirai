package net.origind.destinybot.api.command

interface CustomCommand {
    /**
     * 可能的前缀，加速匹配速度
     */
    fun getPossibleNames(): Iterable<String>

    fun parse(command: String) {}
}
