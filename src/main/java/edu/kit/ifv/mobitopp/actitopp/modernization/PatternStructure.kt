package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.steps.step2.DaySituation
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step2.coordinatedStep2AWithParams
import java.time.DayOfWeek
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * Pattern keeps track of which days have been made working days, and education days and so on, as such, it will know
 * the exact structure of the days eventually.
 */
class PatternStructure(
    val weekRoutine: PersonWithRoutine,
) {
    private val days: MutableCollection<DurationDay> = mutableListOf()
    private val dayStructure: MutableList<DayStructure> = mutableListOf()
    private val activityTracker: ActivityDayTracker = ActivityDayTracker()

    fun mobileDays(): List<DayStructure> {
        return dayStructure
    }

    fun determineNextMainActivity(
        activityTypeFilter: ActivityTypeFilter = Step2Tracking,
        rngHelper: RNGHelper? = null,
    ): ActivityType {
        val currentDay = days.lastOrNull()?.next() ?: DurationDay.FIRST
        days.add(currentDay)
        val activeOptions =
            activityTypeFilter.determineAvailableOptions(
                activityTracker,
                weekRoutine,
                coordinatedStep2AWithParams.registeredOptions()
            )
        val rng = rngHelper ?: weekRoutine.person.personalRNG
        return determineActivityFor(activeOptions, currentDay, rng)
    }

    private fun determineActivityFor(
        availableOptions: Set<ActivityType>,
        day: DurationDay,
        rngHelper: RNGHelper,
    ): ActivityType {

        val randomNumber = rngHelper.randomValue

        val activityType = coordinatedStep2AWithParams.select(options = availableOptions, randomNumber = randomNumber) {
            DaySituation(
                it,
                weekRoutine,
                day.weekday
            )
        }
        activityTracker.add(activityType, day)
        if (activityType != ActivityType.HOME) {
            dayStructure.add(day.spawnDayStructure(activityType))
        }
        return activityType
    }

    fun determineAmountOfSideTours(previousDayStructure: DayStructure, dayStructure: DayStructure) {

    }
}

fun interface ActivityTypeFilter {
    fun determineAvailableOptions(
        tracker: ActivityDayTracker,
        personWithRoutine: PersonWithRoutine,
        initialOptions: Set<ActivityType>,
    ): Set<ActivityType>
}

object Step2Tracking : ActivityTypeFilter {
    override fun determineAvailableOptions(
        tracker: ActivityDayTracker,
        personWithRoutine: PersonWithRoutine,
        initialOptions: Set<ActivityType>,
    ): Set<ActivityType> {
        val (person, routine) = personWithRoutine
        val availableOptions = initialOptions.toMutableSet()
        if (tracker.plannedDaysFor(ActivityType.WORK) >= routine.amountOfWorkingDays && person.isAnywayEmployed()) availableOptions.remove(
            ActivityType.WORK
        )
        if (tracker.plannedDaysFor(ActivityType.EDUCATION) >= routine.amountOfEducationDays && person.isinEducation()) availableOptions.remove(
            ActivityType.EDUCATION
        )
        return availableOptions
    }
}

class ActivityDayTracker {
    private val daysWithActivities: MutableMap<ActivityType, MutableSet<DurationDay>> =
        mutableMapOf<ActivityType, MutableSet<DurationDay>>().withDefault { mutableSetOf() }

    fun add(activityType: ActivityType, day: DurationDay) {
        daysWithActivities.getValue(activityType).add(day)
    }

    fun plannedDaysFor(activityType: ActivityType): Int = daysWithActivities[activityType]?.size ?: 0

}

/**
 * A wrapper class around duration that holds both the exact duration start point when a day starts and the information
 * which weekday is represented. Once sufficient refactors have been done, this class could probably be removed and simply
 * replaced with an extension function that determines the weekday from a duration.
 */
class DurationDay private constructor(
    val timePoint: Duration,
) {
    constructor(dayIndex: Int) : this(dayIndex.days)

    val weekday: DayOfWeek = DayOfWeek.of(timePoint.inWholeDays.toInt() % 7 + 1)

    fun next(): DurationDay {
        return DurationDay(timePoint + 1.days)
    }

    fun spawnDayStructure(mainActivityType: ActivityType): DayStructure {
        return DayStructure(this, TourStructure(mainActivityType))
    }

    companion object {
        val FIRST: DurationDay = DurationDay(0)
    }
}

/**
 * Keep track of side tour results, but do not immediately spawn anything to be used in later calculations.
 */
class SideTourResults(
    var amountOfPrecursorTours: Int = 0,
    var amountOfSuccessorTours: Int = 0,
) {

}

