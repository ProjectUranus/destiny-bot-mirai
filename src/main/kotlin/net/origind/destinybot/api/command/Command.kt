package net.origind.destinybot.api.command

interface Command {
    val baseName: String
    val containerType: Class<in ArgumentContainer>?
        get() = null

    fun getPossibleNames(): Iterable<String> {
        return emptyList()
    }
}
