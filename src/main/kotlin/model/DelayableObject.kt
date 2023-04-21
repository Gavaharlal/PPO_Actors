package model

import java.util.*

class DelayableObject : Delayable {

    private val delaysQueue: Queue<Int> = ArrayDeque()

    override fun nextDelay(): Int {
        return if (delaysQueue.isNotEmpty()) {
            delaysQueue.poll()
        } else {
            0
        }
    }

    override fun setDelays(vararg delays: Int) {
        delaysQueue.addAll(delays.asList())
    }
}