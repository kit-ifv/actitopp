package edu.kit.ifv.mobitopp.actitoppNG.utils

/**
 * The [Position] enum should encapsulate whether an event such as a tour or activity takes place before/after the main
 * element. This behaves similar to the original index implementation which compared against the sign of the index integer
 * to determine the position.
 */
enum class Position {
    BEFORE, MAIN, AFTER;

    companion object {
        fun fromRelativeIndex(index: Int) = when (index) {
            in Int.MIN_VALUE..<0 -> BEFORE
            0 -> MAIN
            in 1..Int.MAX_VALUE -> AFTER
            else -> throw NoSuchElementException("The branches above should be exhaustive")
        }


    }
}