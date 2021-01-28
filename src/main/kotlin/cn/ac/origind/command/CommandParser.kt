package cn.ac.origind.command

class CommandParser(val command: String) {
    var index = 0
    var internal = command

    init {
        trim() // TODO Config section
        
    }

    fun trim() {
        internal = internal.trim()
    }

    /**
     * Take a string argument
     */
    fun take(): String {
        for (i in internal.indices) {
            if (internal[i] == ' ') {
                val temp = internal.substring(i)
            }
        }
    }

}