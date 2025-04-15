package edu.kit.ifv.mobitopp.actitopp

import java.util.Random

/**
 * @author Tim Hilgert
 */
class RNGHelper(
    /**
     * @return
     */
    val seed: Long
) {
    private val rng = Random(seed)

    /**
     * @return
     */
    var lastRandomValue: Double = 0.0
        private set


    val randomValue: Double
        /**
         * @return
         */
        get() {
            // create randomValue
            val randomvalue = rng.nextDouble()

            // Save for access possibility
            lastRandomValue = randomvalue

            return randomvalue
        }

    /**
     * creates a random key between 0 and bound
     * used to draw a random person out of a list
     *
     * @param bound
     * @return
     */
    fun getRandomPersonKey(bound: Int): Int {
        return rng.nextInt(bound)
    }


    /**
     * get random from range (from...to) with the specified "size" of the steps
     * uniform distribution!
     *
     * @param from
     * @param to
     * @param stepSize
     * @return
     */
    fun getRandomValueBetween(from: Int, to: Int, stepSize: Int): Int {
        require(from <= to) { "FROM bigger than TO" }
        val steps = (to - from) / stepSize
        val range = IntArray(steps + 1)
        for (i in 0..<steps) {
            range[i] = from + (i * stepSize)
        }
        range[steps] = to

        val rangeSize = range.size - 1
        val result = range[rng.nextInt(rangeSize - 0 + 1) + 0]

        return result
    }
}
