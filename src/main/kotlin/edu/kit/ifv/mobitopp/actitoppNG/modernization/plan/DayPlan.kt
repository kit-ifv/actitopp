package edu.kit.ifv.mobitopp.actitoppNG.modernization.plan

import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.modernization.DurationDay
import edu.kit.ifv.mobitopp.actitoppNG.modernization.LinkedActivity
import edu.kit.ifv.mobitopp.actitoppNG.modernization.MutableTourStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.linkByHomeActivity
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.TimeBudgets
import edu.kit.ifv.mobitopp.actitoppNG.utils.BidirectionalIndexedValue
import edu.kit.ifv.mobitopp.actitoppNG.utils.Position
import edu.kit.ifv.mobitopp.actitoppNG.utils.foldUntil
import edu.kit.ifv.mobitopp.actitoppNG.utils.rem
import edu.kit.ifv.mobitopp.actitoppNG.utils.sumOf
import edu.kit.ifv.mobitopp.actitoppNG.utils.takeUntil
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


interface DayPlan : List<LinkedActivity> {
    val firstActivity: LinkedActivity
    val lastActivity: LinkedActivity
    val tourPlans: List<TourPlan>
    val durationDay: DurationDay
    val durationOfMainActivities: Duration
    val amountOfActivities: Int
    val mainTour: TourPlan

    fun mainActivityType() = mainTour.mainActivity.activityType
    fun numberOfActivities(activityType: ActivityType): Int
    fun hasActivity(activityType: ActivityType): Boolean
    fun mainActivities(): List<LinkedActivity>
    fun durationOfActivities(): Duration
    val activityBudget: Map<ActivityType, Duration>
    fun previousTourPlan(tourPlan: TourPlan): TourPlan? {
        val index = tourPlans.indexOf(tourPlan)
        if (index >= 1) return tourPlans[index - 1]
        return null

    }

    fun endOfPreviousTour(tourPlan: TourPlan): Duration {
        val endOfPrevious = tourPlan.first().previousActivity?.startTime ?: Duration.ZERO
        return endOfPrevious % 1.days

    }

    fun getBudget(activityType: ActivityType): Duration =
        activityBudget[activityType] ?: throw NoSuchElementException("No budget found for activity $activityType")

    fun activityTimeBounds(linkedActivity: LinkedActivity, disregardDayEnd: Boolean = false): ClosedRange<Duration>
    fun activityDurationBounds(linkedActivity: LinkedActivity): ClosedRange<Duration> {
        val absoluteBounds = activityTimeBounds(linkedActivity)
        val maximumDuration = absoluteBounds.endInclusive - absoluteBounds.start
// TODO the check for duration should not throw, the function above should
//        require(maximumDuration >= 1.minutes) {
//            "This duration is not reasonable. $this"
//        }
        return 1.minutes..maximumDuration


    }

    fun startTimeBoundsFor(tourPlan: TourPlan, disregardDayEnd: Boolean = false): ClosedRange<Duration> {
        val firstAct = tourPlan.first()
        val beforeTripDuration = firstAct.previousTrip?.duration ?: Duration.ZERO
        val absoluteBounds = activityTimeBounds(firstAct, disregardDayEnd)
        require(!absoluteBounds.isEmpty()) {
            "Generating an empty range will never allow proper times to be selected, this should not occur"
        }
        val earliestTourStartRelative = absoluteBounds.start - durationDay.startOfDay - beforeTripDuration
        val latestTourStartRelative = absoluteBounds.endInclusive - durationDay.startOfDay - (firstAct.duration
            ?: throw IllegalStateException("At this point the duration should be set")) - beforeTripDuration
        return earliestTourStartRelative..latestTourStartRelative
    }
}

interface MutableDayPlan : DayPlan {
    override var firstActivity: LinkedActivity
    override var lastActivity: LinkedActivity
}

data class MovingDayPlanInput(
    val personWithRoutine: PersonWithRoutine,
    val tripDuration: DetermineTripDuration,
    val timeBudgets: TimeBudgets,
    val durationDay: DurationDay,
)

/**
 * Note timeBudgets is specific for the day, the mobility plan shares the budget between the days
 */
class MovingDayPlan(
    val activities: List<LinkedActivity>,
    override val tourPlans: List<TourPlan>,
    timeBudgets: TimeBudgets,
    override val durationDay: DurationDay,
    override val mainTour: TourPlan = tourPlans.first { it.position == Position.MAIN },
) : MutableDayPlan, List<LinkedActivity> by activities {
    private val _mainActivities by lazy { tourPlans.map { it.mainActivity } }


    override fun mainActivities(): List<LinkedActivity> {
        return _mainActivities
    }

    override val durationOfMainActivities: Duration by lazy {
        mainActivities().sumOf {
            it.duration
                ?: throw IllegalStateException("Cannot evalutate duration, some activities have not yet received a duration")
        }
    }
    override var firstActivity: LinkedActivity = activities.first()
    override var lastActivity: LinkedActivity = activities.last()

    val startHomeActivityDay by lazy { firstActivity.previousTrip?.previousActivity }
    val endHomeActivityDay by lazy { lastActivity.nextTrip?.nextActivity }

    // Throw an error if the duration of any activity is not set, this scenario cannot be handled
    private val _durationOfActivities by lazy {
        activities.sumOf {
            it.duration
                ?: throw IllegalStateException("Cannot evaluate duration of activities, if some have not yet been assigned a duration")
        }
    }

    override fun durationOfActivities(): Duration {
        return _durationOfActivities
    }

    override fun numberOfActivities(activityType: ActivityType): Int {
        return activities.count { it.activityType == activityType }
    }

    override fun hasActivity(activityType: ActivityType): Boolean {
        return activities.any { it.activityType == activityType }
    }

    override val amountOfActivities: Int = activities.filter { it.activityType != ActivityType.HOME }.size
    val activityGroups = activities.filter { it.activityType != ActivityType.HOME }.groupBy { it.activityType }
    val estimatedActivityDurations =
        activityGroups.mapValues { it.key.defaultActivityTime.minutes / activityGroups.getValue(it.key).size }
            .withDefault {
                ActivityType.HOME.defaultActivityTime.minutes / amountOfActivities
            }

    // Amount of tours in plan, since amount of activities is handled above
    override val activityBudget: Map<ActivityType, Duration> = activities.groupBy { it.activityType }
        .mapValues { (timeBudgets[it.key] / it.value.size).coerceIn(1.minutes, 1440.minutes) }

    override fun activityTimeBounds(linkedActivity: LinkedActivity, disregardDayEnd: Boolean): ClosedRange<Duration> {
        require(linkedActivity in activities) {
            "This check is relevant, but sadly O(n). It could be improved by checking against the start and end time" +
                    "of the day, which soft implies that the target activity lies within this day"
        }
        // Potentially locate a successor activity with a fixed start time in this day, and track the sum of
        // durations until either a successor is found or the end of the day is reached (checked by comparing against end
        // home activity)
        val (fixedSuccessor, durationToSuccessor) = linkedActivity.iterator().drop(1)
            .takeUntil { it == endHomeActivityDay }
            .foldUntil({ it.startTime != null }, Duration.ZERO) { acc, action ->
                acc + (action.estimatedDuration(estimatedActivityDurations))
            }
        /*
            Similarly, locate a precursor activity with a fixed end time. And track the sum of durations either to the
            fixed element or the sum of durations up to the start of the day.
         */
        val (fixedPrecursor, durationToPrecursor) = linkedActivity.backwardIterator().drop(1)
            .takeUntil { it == startHomeActivityDay }.foldUntil({ it.endTime != null }, Duration.ZERO) { acc, action ->
                acc + (action.estimatedDuration(estimatedActivityDurations))
            }
        // If a precursor is found, that start time is a better bound for the current element, if not use 0 as relative start time of the day
        val earliestStartTime = (fixedPrecursor?.endTime ?: durationDay.startOfDay) + durationToPrecursor

        // Similarly, a successor with a fixed time is a better bound for the potential end time of this element, but if nothing
        // has a fixed time, the end of the day is the fallback.
        val endDuration =
            if (disregardDayEnd) (durationDay.startOfDay + 1.days + 3.hours) else (durationDay.startOfDay + 1.days)
        val latestStartTime = (fixedSuccessor?.startTime ?: endDuration) -
                durationToSuccessor



        return earliestStartTime..latestStartTime
    }


    companion object {
        fun create(
            tourStructures: Collection<BidirectionalIndexedValue<MutableTourStructure>>,
            movingDayInput: MovingDayPlanInput,
        ): MovingDayPlan {
            return movingDayInput.run {
                val tourPlans: List<TourPlan> = tourStructures.map { it.element.toPlan(personWithRoutine, it.position) }
                val activities: List<LinkedActivity> = tourPlans.zipWithNext().flatMap { (tourA, tourB) ->
                    tourA.linkByHomeActivity(tourB, personWithRoutine, tripDuration)
                } + tourPlans.last()

                MovingDayPlan(activities, tourPlans, timeBudgets, durationDay)
            }

        }
    }
}

class HomeDayPlan(override val durationDay: DurationDay) : MutableDayPlan, List<LinkedActivity> by emptyList() {
    override var firstActivity: LinkedActivity = LinkedActivity.homeDay()
    override var lastActivity: LinkedActivity = firstActivity
    override val tourPlans: List<TourPlan> = emptyList()
    override val durationOfMainActivities: Duration = Duration.ZERO
    override val amountOfActivities: Int = 0
    override fun numberOfActivities(activityType: ActivityType): Int = 0
    override fun hasActivity(activityType: ActivityType): Boolean = false

    override fun mainActivities(): List<LinkedActivity> {
        return emptyList()
    }

    override val mainTour: TourPlan
        get() = throw NoSuchElementException("A home day cannot have a main tour")
    override val activityBudget: Map<ActivityType, Duration> =
        emptyMap<ActivityType, Duration>().withDefault { 0.minutes }

    override fun activityTimeBounds(linkedActivity: LinkedActivity, disregardDayEnd: Boolean): ClosedRange<Duration> {
        throw UnsupportedOperationException("A home day has no bounds for other activities")
    }

    override fun activityDurationBounds(linkedActivity: LinkedActivity): ClosedRange<Duration> {
        throw UnsupportedOperationException("A home day has no bounds for other activities")
    }

    override fun durationOfActivities(): Duration {
        return Duration.ZERO
    }
}