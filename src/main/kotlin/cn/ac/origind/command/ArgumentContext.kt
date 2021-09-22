package cn.ac.origind.command

data class ArgumentContext<T>(val name: String, val type: ArgumentType<T>, val optional: Boolean = false, val description: String? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ArgumentContext<*>) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
