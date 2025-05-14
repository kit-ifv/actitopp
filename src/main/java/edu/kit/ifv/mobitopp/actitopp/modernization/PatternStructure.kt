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
    private val activeDays: MutableList<ModifiableDayStructure> = mutableListOf()
    private val days: MutableCollection<DurationDay> = mutableListOf()
    private val dayStructure: MutableMap<DurationDay, DayStructure> = mutableMapOf()
    private val activityTracker: ActivityDayTracker = ActivityDayTracker()

    fun allDays(): Collection<DayStructure> {
        return dayStructure.values
    }
    fun mobileDays(): List<ModifiableDayStructure> {
        return activeDays
    }
    fun generateTrackedActivity(day: DurationDay lambda: PatternStructure.() -> ActivityType): ActivityType {
        val activityType = lambda()
        activityTracker.add(activityType)
    }

    fun amountOfDaysWith(activityType: ActivityType): Int {
        return activityTracker.plannedDaysFor(activityType)
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

        val converter: (ActivityType) -> DaySituation = {
            DaySituation(
                it,
                weekRoutine,
                day.weekday
            )
        }
        val activityType = coordinatedStep2AWithParams.select(options = availableOptions, randomNumber = randomNumber,
            converter = converter
        )
        activityTracker.add(activityType, day)
        dayStructure[day] = when (activityType) {
            ActivityType.HOME -> HomeDay(day)
            else -> day.spawnDayStructure(activityType).also { activeDays.add(it) }
        }
        return activityType
    }

    fun determineSideTourActivities() {

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

/**
 * Track the days that have one or more of a planned activity with a given type, so that the activity determiner can
 * check how many days are already assigned to say work: (Like 5 Work days for a regular week)
 */
class ActivityDayTracker {
    private val daysWithActivities: MutableMap<ActivityType, MutableSet<DurationDay>> =
        mutableMapOf<ActivityType, MutableSet<DurationDay>>()

    fun add(activityType: ActivityType, day: DurationDay) {
        daysWithActivities.getOrPut(activityType){
          mutableSetOf()
        }.add(day)
    }

    fun plannedDaysFor(activityType: ActivityType): Int = daysWithActivities[activityType]?.size ?: 0

}
fun Int.positiveModulus(modulo: Int): Int {
    val result  = this % modulo
    return if (result < 0) result + modulo else result
}
/**
 * A wrapper class around duration that holds both the exact duration start point when a day starts and the information
 * which weekday is represented. Once sufficient refactors have been done, this class could probably be removed and simply
 * replaced with an extension function that determines the weekday from a duration.
 */
class DurationDay private constructor(
    val timePoint: Duration,
    var lowerBoundJointTours: Int = 0,
    var lowerBoundJointActivities: Int = 0,
) {
    constructor(dayIndex: Int) : this(dayIndex.days)

    val weekday: DayOfWeek = DayOfWeek.of(timePoint.inWholeDays.toInt().positiveModulus(7) + 1)

    fun next(): DurationDay {
        return DurationDay(timePoint + 1.days)
    }

    fun previous(): DurationDay {
        return DurationDay(timePoint - 1.days)
    }

    fun spawnDayStructure(mainActivityType: ActivityType): ModifiableDayStructure {
        return ModifiableDayStructure(this, TourStructure(mainActivityType))
    }

    companion object {
        val FIRST: DurationDay = DurationDay(0)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DurationDay) return false
        return timePoint == other.timePoint
    }

    override fun hashCode(): Int {
        return timePoint.hashCode()
    }
}
