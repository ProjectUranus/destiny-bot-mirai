package cn.ac.origind.command

data class ArgumentContext<T>(val name: String, val type: ArgumentType<T>, val optional: Boolean, val description: String?) {
}