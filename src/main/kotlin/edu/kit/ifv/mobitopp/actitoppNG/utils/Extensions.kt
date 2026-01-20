package edu.kit.ifv.mobitopp.actitoppNG.utils


import kotlin.math.ceil
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Similar behaviour to [Duration.inWholeMinutes] but it always rounds up instead of down.
 */
val Duration.ceilWholeMinutes: Int
    get() {
        val double = this.toDouble(DurationUnit.MINUTES)
        return ceil(double).toInt()
    }

operator fun Duration.rem(that: Duration): Duration {
    return (this.toDouble(DurationUnit.MINUTES) % that.toDouble(DurationUnit.MINUTES)).minutes
}

fun Duration.ceil(unit: DurationUnit): Duration {
    return ceil(this.toDouble(unit)).toDuration(unit)
}

fun Duration.round(unit: DurationUnit): Duration {
    return this.toDouble(unit).roundToLong().toDuration(unit)
}

operator fun ClosedRange<Duration>.minus(duration: Duration): ClosedRange<Duration> {
    return (start - duration)..(endInclusive - duration)
}

inline fun <T> Iterable<T>.sumOf(selector: (T) -> Duration): Duration {
    var sum: Duration = Duration.ZERO
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline operator fun Boolean.times(other: Double): Double = this.D * other
inline operator fun Boolean.times(other: Int): Int = this.I * other
inline val Boolean.D get() = if (this) 1.0 else 0.0
inline val Boolean.I get() = if (this) 1 else 0
fun Int.positiveModulus(modulo: Int): Int {
    val result = this % modulo
    return if (result < 0) result + modulo else result
}


fun Double.affineTransform(lower: Double, upper: Double): Double {
    return (upper - lower) * this + lower
}

/**
 * In case we don't need the insertion, but are just interested in the position, we can invert the index of binary search
 */
fun DoubleArray.indexBinarySearch(element: Double, fromIndex: Int = 0, toIndex: Int = size): Int {
    val binarySearch = binarySearch(element, fromIndex, toIndex)
    return binarySearch.indexOfSearch()
}

fun <T> Array<T>.indexBinarySearch(element: T, fromIndex: Int = 0, toIndex: Int = size): Int {
    val binarySearch = binarySearch(element, fromIndex, toIndex)
    return binarySearch.indexOfSearch()
}

fun Int.indexOfSearch(): Int {
    return if (this < 0) -this - 1 else this
}


