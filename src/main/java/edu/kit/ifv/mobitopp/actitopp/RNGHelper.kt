package edu.kit.ifv.mobitopp.actitopp

import kotlin.random.Random

/**
 * @author Tim Hilgert
 */
class RNGHelper private constructor(

    val seed: Long,
    private val rng : Random
) {

    constructor(seed: Long): this(seed = seed, rng = Random(seed))
    /**
     * @return
     */
    @Deprecated("Seems to be only used for debug printing")
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
     * Nowhere in the codebase is the stepSize set to anything other than 1, so we can kill that procedure.
     * Also random generation can be done without allocating an array and then picking randomly from it
     *
     * If in the future anyone requires stepSize -> In kotlin you can pass the stepSize to the range.
     */
    fun getRandomValueBetween(from: Int, to: Int): Int {

        require(from <= to) { "FROM bigger than TO $from $to" }
        return (from..to).random(rng)
    }

    fun copy(): RNGHelper {
        return RNGHelper(seed)
    }
}
