package net.origind.destinybot.api.command

class CommandParser(val command: String) {
    var internal = command

    init {
        trim() // TODO Config section
        
    }

    fun trim() {
        internal = internal.trim()
    }

    fun hasMore() =
        internal.trim().isNotBlank()

    /**
     * Take a string argument and jump
     */
    fun take(move: Boolean = true): String {
        val index = internal.indexOf(' ')
        if (index == -1) {
            if (internal.isBlank()) throw IndexOutOfBoundsException("Command parser is complete")
            val temp = internal.trim()
            if (move)
                internal = ""
            return temp
        }
        val temp = internal.substring(0, index)
        if (move)
            internal = internal.substring(index + 1).trim()
        return temp
    }

    fun take(n: Int): Array<String> {
        val arr = Array(n) { "" }
        for (i in 0 until n)
            arr[i] = take()
        return arr
    }
}
