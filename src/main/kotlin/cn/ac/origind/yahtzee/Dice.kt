package cn.ac.origind.yahtzee

import kotlin.random.Random

data class Dice(var value: Int = 0, var fixed: Boolean = false) : Comparable<Dice> {
    init {
        roll()
    }

    fun roll() {
        if (!fixed)
            value = Random.nextInt(1, 7)
    }

    override fun compareTo(other: Dice): Int {
        return value.compareTo(other.value)
    }

    override fun toString() = value.toString()
}