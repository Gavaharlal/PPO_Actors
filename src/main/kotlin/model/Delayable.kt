package model

interface Delayable {

    fun setDelays(vararg delays: Int)

    fun nextDelay(): Int
}