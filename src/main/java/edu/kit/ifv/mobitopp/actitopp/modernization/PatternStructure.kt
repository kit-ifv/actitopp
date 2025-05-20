package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.DetermineTripDuration
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.MobilityPlan
import edu.kit.ifv.mobitopp.actitopp.steps.step2.DaySituation
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step2.coordinatedStep2AWithParams
import edu.kit.ifv.mobitopp.actitopp.steps.step7.TimeBudgets
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
    fun generateTrackedActivity(day: DurationDay, lambda: PatternStructure.(DurationDay) -> ActivityType): ActivityType {
        val activityType = lambda(day)
        activityTracker.add(
            activityType,
            day = day
        )
        return activityType
    }

    fun DurationDay.shouldNotBeEducationDay(weekRoutine: WeekRoutine): Boolean {
        return activityTracker.amountOfDaysWithActivity(ActivityType.EDUCATION) >= weekRoutine.amountOfEducationDays && this !in activityTracker.daysWithActivity(ActivityType.EDUCATION)
    }

    fun DurationDay.shouldNotBeWorkDay(weekRoutine: WeekRoutine): Boolean {
        return activityTracker.amountOfDaysWithActivity(ActivityType.WORK) >= weekRoutine.amountOfWorkingDays && this !in activityTracker.daysWithActivity(ActivityType.WORK)
    }

    fun amountOfDaysWith(activityType: ActivityType): Int {
        return activityTracker.amountOfDaysWithActivity(activityType)
    }

    fun determineNextMainActivity(
        activityTypeFilter: ActivityTypeFilter = Step2Tracking,
        rngHelper: RNGHelper,
    ): ActivityType {
        val currentDay = days.lastOrNull()?.next() ?: DurationDay.FIRST
        days.add(currentDay)
        val activeOptions =
            activityTypeFilter.determineAvailableOptions(
                activityTracker,
                weekRoutine,
                coordinatedStep2AWithParams.registeredOptions()
            )
        val rng = rngHelper
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

    /**
     * Since absolutely no decision in Step 8+ checks whether the previous day is a Home Day and the home day just
     * complicates the legacy counting, we can drop these days, since these days will be modelled exclusively by the
     * spawned Home Activity spanning between the previous day and the next day.
     */
    fun toPlan(personWithRoutine: PersonWithRoutine, tripDuration: DetermineTripDuration, timeBudgets: TimeBudgets) : MobilityPlan? {
        if(mobileDays().isEmpty()) return null // TODO handle pattern with no activity somewhere
        return MobilityPlan.create(mobileDays(), timeBudgets, personWithRoutine, tripDuration)
    }

}

class Generator(private val patternStructure: PatternStructure, private val personWithRoutine: PersonWithRoutine, val rngHelper: RNGHelper) {

    private val mainActivityOfSideTours: AssignMainActivityOfSideTour = AssignByUtilityFunction(patternStructure, rngHelper)
    fun generateSideTours(tourAmounts: Map<DurationDay, PlannedTourAmounts>): Map<ModifiableDayStructure, Pair<List<ActivityType>, List<ActivityType>>> {
        return patternStructure.mobileDays().associateWith {
            val input = DayWithPlans(it, personWithRoutine, tourAmounts[it.startTimeDay] ?: PlannedTourAmounts.NONE)
            mainActivityOfSideTours.generateSideTourActivities(input)
        }
    }

    fun loadSideTours(tourAmounts: Map<DurationDay, PlannedTourAmounts>) {
        val targets = generateSideTours(tourAmounts)
        targets.forEach { dayStructure, (prec, succ) ->
            dayStructure.loadPrecursors(prec)
            dayStructure.loadSuccessors(succ)
        }
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
        if (tracker.amountOfDaysWithActivity(ActivityType.WORK) >= routine.amountOfWorkingDays && person.isAnywayEmployed()) availableOptions.remove(
            ActivityType.WORK
        )
        if (tracker.amountOfDaysWithActivity(ActivityType.EDUCATION) >= routine.amountOfEducationDays && person.isinEducation()) availableOptions.remove(
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

    fun amountOfDaysWithActivity(activityType: ActivityType): Int = daysWithActivities[activityType]?.size ?: 0
    fun daysWithActivity(activityType: ActivityType): Set<DurationDay> = daysWithActivities[activityType] ?: emptySet()
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
