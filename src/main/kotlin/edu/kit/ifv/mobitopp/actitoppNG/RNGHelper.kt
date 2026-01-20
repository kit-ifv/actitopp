package edu.kit.ifv.mobitopp.actitoppNG

import kotlin.random.Random

abstract class RNGHelper : Random() {
    abstract val randomValue: Double

    companion object {
        operator fun invoke(seed: Long) = RNGHelperImpl(seed)
    }
}


class RNGHelperImpl private constructor(

    private val seed: Long,
    private val rng: Random,
) : RNGHelper() {

    constructor(seed: Long) : this(seed = seed, rng = Random(seed))


    override val randomValue: Double get() = rng.nextDouble()

    /**
     * Gets the next random [bitCount] number of bits.
     *
     * Generates an `Int` whose lower [bitCount] bits are filled with random values and the remaining upper bits are zero.
     *
     * @param bitCount number of bits to generate, must be in range 0..32, otherwise the behavior is unspecified.
     *
     * @sample samples.random.Randoms.nextBits
     */
    override fun nextBits(bitCount: Int): Int {
        return rng.nextBits(bitCount)
    }


    override fun toString(): String {
        return "Rand($seed)"
    }
}



