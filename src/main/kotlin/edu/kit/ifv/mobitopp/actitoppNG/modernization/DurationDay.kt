package edu.kit.ifv.mobitopp.actitoppNG.modernization

import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.utils.positiveModulus
import java.time.DayOfWeek
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * A wrapper class around duration that holds both the exact duration start point when a day starts and the information
 * which weekday is represented. Once sufficient refactors have been done, this class could probably be removed and simply
 * replaced with an extension function that determines the weekday from a duration.
 * TODO note that [startOfDay] is absolute.
 */
class DurationDay private constructor(
    val startOfDay: Duration,
) : Comparable<DurationDay> {
    constructor(dayIndex: Int) : this(dayIndex.days)

    val weekday: DayOfWeek = DayOfWeek.of(startOfDay.inWholeDays.toInt().positiveModulus(7) + 1)

    fun next(): DurationDay {
        return DurationDay(startOfDay + 1.days)
    }

    fun previous(): DurationDay {
        return DurationDay(startOfDay - 1.days)
    }

    fun spawnDayStructure(mainActivityType: ActivityType): ModifiableDayStructure {
        return ModifiableDayStructure(this, MutableTourStructure(mainActivityType))
    }

    companion object {
        val FIRST: DurationDay = DurationDay(0)
    }

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: DurationDay): Int {
        return startOfDay.compareTo(other.startOfDay)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DurationDay) return false
        return startOfDay == other.startOfDay
    }

    override fun hashCode(): Int {
        return startOfDay.hashCode()
    }
}