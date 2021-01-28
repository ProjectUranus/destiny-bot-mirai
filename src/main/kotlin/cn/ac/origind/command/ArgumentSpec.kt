package cn.ac.origind.command

class ArgumentSpec(val name: String) {

}

fun argument(name: String, init: ArgumentSpec.() -> Unit): ArgumentSpec {
    val argument = ArgumentSpec(name)
    argument.init()
    return argument
}
