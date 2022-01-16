package net.origind.destinybot.features.instatus.response

data class Component(val id: String, val name: String, val description: String, val status: String, val order: Int) : Comparable<Component> {
    override fun compareTo(other: Component): Int = order.compareTo(other.order)
}
