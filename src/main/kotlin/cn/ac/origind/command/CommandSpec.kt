package cn.ac.origind.command

open class CommandSpec(val name: String) {
}

fun command(name: String, init: CommandSpec.() -> Unit): CommandSpec {
    val spec = CommandSpec(name)
    spec.init()
    CommandManager.register(spec)
    return spec
}
