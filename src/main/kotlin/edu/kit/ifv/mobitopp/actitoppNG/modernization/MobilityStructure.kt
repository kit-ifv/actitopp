package edu.kit.ifv.mobitopp.actitoppNG.modernization

import edu.kit.ifv.mobitopp.actitoppNG.Person
import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.ActualMobilityPlan
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.DetermineTripDuration
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MobilityPlan
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.StayAtHomePlan
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.TimeBudgets
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.WeekRoutine

/**
 * Pattern keeps track of which days have been made working days, and education days and so on, as such, it will know
 * the exact structure of the days eventually.
 */
class MobilityStructure(
    val weekRoutine: PersonWithRoutine,
) {

    constructor(person: Person, weekRoutine: WeekRoutine) : this(PersonWithRoutine(person, weekRoutine))

    private val activeDays: MutableList<ModifiableDayStructure> = mutableListOf()
    private val days: MutableCollection<DurationDay> = mutableListOf()
    private val dayStructure: MutableMap<DurationDay, DayStructure> = mutableMapOf()
    private val activityTracker: ActivityDayTrackerImpl = ActivityDayTrackerImpl(weekRoutine.routine)

    fun getTracker(): ActivityDayTracker = activityTracker

    fun allDays(): Collection<DayStructure> {
        return dayStructure.values
    }

    fun mobileDays(): List<ModifiableDayStructure> {
        return activeDays
    }

    fun homeDays(): Set<DurationDay> {
        return days.toSet() - activeDays.map { it.startTimeDay }.toSet()
    }

    fun activityTypes(): List<ActivityType> {
        return activeDays.flatMap { it.elements().flatMap { it.elements() } }
    }

    fun nextDay(): DurationDay {
        return days.lastOrNull()?.next() ?: DurationDay.FIRST
    }

    /**
     * The spawnage of the daystructure should happen class internal, so that the daystructure can only be exposed in an appropriate wrapper.
     */
    fun add(day: DurationDay, activityType: ActivityType) {
        dayStructure[day] = when (activityType) {
            ActivityType.HOME -> HomeDay(day)
            else -> day.spawnDayStructure(activityType).also { activeDays.add(it) }
        }
        days.add(day)
        activityTracker.add(activityType, day)
    }

    fun elements(): Collection<TrackedDayStructure> {
        return activeDays.map { TrackedDayStructure(activityTracker, it) }
    }

    fun generateTrackedActivity(
        day: DurationDay,
        lambda: MobilityStructure.(DurationDay) -> ActivityType,
    ): ActivityType {
        val activityType = lambda(day)
        activityTracker.add(
            activityType,
            day = day
        )
        return activityType
    }

    fun DurationDay.shouldNotBeEducationDay(weekRoutine: WeekRoutine): Boolean {
        return activityTracker.amountOfDaysWithActivity(ActivityType.EDUCATION) >= weekRoutine.amountOfEducationDays && this !in activityTracker.daysWithActivity(
            ActivityType.EDUCATION
        )
    }

    fun DurationDay.shouldNotBeWorkDay(weekRoutine: WeekRoutine): Boolean {
        return activityTracker.amountOfDaysWithActivity(ActivityType.WORK) >= weekRoutine.amountOfWorkingDays && this !in activityTracker.daysWithActivity(
            ActivityType.WORK
        )
    }


    fun amountOfDaysWith(activityType: ActivityType): Int {
        return activityTracker.amountOfDaysWithActivity(activityType)
    }


    /**
     * Since absolutely no decision in Step 8+ checks whether the previous day is a Home Day and the home day just
     * complicates the legacy counting, we can drop these days, since these days will be modelled exclusively by the
     * spawned Home Activity spanning between the previous day and the next day.
     */
    fun toPlan(
        tripDuration: DetermineTripDuration,
        timeBudgets: TimeBudgets,
    ): MobilityPlan {
        if (mobileDays().isEmpty()) {
            return StayAtHomePlan.create(homeDays(), weekRoutine)
        }
        return ActualMobilityPlan.create(mobileDays(), homeDays(), timeBudgets, weekRoutine, tripDuration)
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
        if (tracker.isSaturated(ActivityType.WORK) && person.isAnywayEmployed()) availableOptions.remove(ActivityType.WORK)
        if (tracker.isSaturated(ActivityType.EDUCATION) && person.isinEducation()) availableOptions.remove(ActivityType.EDUCATION)
        return availableOptions
    }
}

interface ActivityDayTracker {
    fun isSaturated(activityType: ActivityType): Boolean
}

/**
 * Track the days that have one or more of a planned activity with a given type, so that the activity determiner can
 * check how many days are already assigned to say work: (Like 5 Work days for a regular week)
 */
class ActivityDayTrackerImpl(val weekRoutine: WeekRoutine) : ActivityDayTracker {
    private val daysWithActivities: MutableMap<ActivityType, MutableSet<DurationDay>> =
        mutableMapOf()

    fun add(activityType: ActivityType, day: DurationDay) {
        daysWithActivities.getOrPut(activityType) {
            mutableSetOf()
        }.add(day)
    }

    fun add(activityTypes: Collection<ActivityType>, day: DurationDay) {
        activityTypes.toSet().forEach {
            add(it, day)
        }
    }


    override fun isSaturated(activityType: ActivityType): Boolean {
        return amountOfDaysWithActivity(activityType) >= weekRoutine[activityType]
    }

    fun amountOfDaysWithActivity(activityType: ActivityType): Int = daysWithActivities[activityType]?.size ?: 0
    fun daysWithActivity(activityType: ActivityType): Set<DurationDay> = daysWithActivities[activityType] ?: emptySet()
}


