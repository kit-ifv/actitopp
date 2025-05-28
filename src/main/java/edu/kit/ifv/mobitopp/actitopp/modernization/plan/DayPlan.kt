package edu.kit.ifv.mobitopp.actitopp.modernization.plan

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.BidirectionalIndexedValue
import edu.kit.ifv.mobitopp.actitopp.modernization.DurationDay
import edu.kit.ifv.mobitopp.actitopp.modernization.LinkedActivity
import edu.kit.ifv.mobitopp.actitopp.modernization.TourStructure
import edu.kit.ifv.mobitopp.actitopp.modernization.foldUntil
import edu.kit.ifv.mobitopp.actitopp.modernization.linkByHomeActivity
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step7.TimeBudgets
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit


interface DayPlan : List<LinkedActivity> {
    val firstActivity: LinkedActivity
    val lastActivity: LinkedActivity
    val tourPlans: List<TourPlan>
    val durationDay: DurationDay
    val durationOfMainActivities: Duration
    val amountOfActivities: Int
    fun numberOfActivities(activityType: ActivityType): Int
    fun hasActivity(activityType: ActivityType): Boolean
    fun mainActivities(): List<LinkedActivity>

    val activityBudget: Map<ActivityType, Duration>

    fun getBudget(activityType: ActivityType): Duration =
        activityBudget[activityType] ?: throw NoSuchElementException("No budget found for activity $activityType")

    fun boundsFor(linkedActivity: LinkedActivity): ClosedRange<Duration>
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
 * Note [timeBudgets] is specific for the day, the mobility plan shares the budget between the days
 */
class MovingDayPlan(
    val activities: List<LinkedActivity>,
    override val tourPlans: List<TourPlan>,
    timeBudgets: TimeBudgets,
    override val durationDay: DurationDay,
) : MutableDayPlan, List<LinkedActivity> by activities {
    private val _mainActivities by lazy {tourPlans.map { it.mainActivity }}


    init {
        println("Creating Day with $activities")
    }
    override fun mainActivities(): List<LinkedActivity> {
        return _mainActivities
    }
    override val durationOfMainActivities: Duration by lazy {
        mainActivities().sumOf { it.duration?.toDouble(DurationUnit.MINUTES)?: throw IllegalArgumentException(
            "Somehow a main activity has not yet received a duration, so the sum over the main activities cannot be calculated."
        ) }.minutes
    }
    override var firstActivity: LinkedActivity = activities.first()
    override var lastActivity: LinkedActivity = activities.last()

    val startHomeActivityDay by lazy { firstActivity.previousTrip?.previousActivity }
    val endHomeActivityDay by lazy { lastActivity.nextTrip?.nextActivity}

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
    val amountOfTours = tourPlans.size

    // Amount of tours in plan, since amount of activities is handled above
    override val activityBudget: Map<ActivityType, Duration> = activities.groupBy { it.activityType }
        .mapValues { (timeBudgets[it.key] / it.value.size).coerceIn(1.minutes, 1440.minutes) }


    override fun boundsFor(linkedActivity: LinkedActivity): ClosedRange<Duration> {
        require(linkedActivity in activities) {
            "This check is relevant, but sadly O(n). It could be improved by checking against the start and end time" +
                    "of the day, which soft implies that the target activity lies within this day"
        }
        // Potentially locate a successor activity with a fixed start time in this day, and track the sum of
        // durations until either a successor is found or the end of the day is reached (checked by comparing against end
        // home activity)
        val (fixedSuccessor, durationToSuccessor) = linkedActivity.iterator().drop(1).takeWhile { it != endHomeActivityDay }
            .foldUntil({ it.startTime != null }, Duration.ZERO) { acc, action ->
                acc + (action.estimatedDuration(estimatedActivityDurations))
            }
        /*
            Similarly, locate a precursor activity with a fixed end time. And track the sum of durations either to the
            fixec element or the sum of durations up to the start of the day.
         */
        val (fixedPrecursor, durationToPrecursor) = linkedActivity.backwardIterator().drop(1)
            .takeWhile { it != startHomeActivityDay }.foldUntil({ it.endTime != null }, Duration.ZERO) { acc, action ->
            acc + (action.estimatedDuration(estimatedActivityDurations))
        }
        // If a precursor is found, that start time is a better bound for the current element, if not use 0 as relative start time of the day
        val earliestStartTime = (fixedPrecursor?.endTime ?: durationDay.startOfDay) + durationToPrecursor

        // Similarly, a successor with a fixed time is a better bound for the potential end time of this element, but if nothing
        // has a fixed time, the end of the day is the fallback.
        val latestEndTime = (fixedSuccessor?.startTime ?: (durationDay.startOfDay + 1.days)) - durationToSuccessor
        val maximumDuration = latestEndTime - earliestStartTime

        val successorElements = linkedActivity.iterator().drop(1).takeWhile { it != endHomeActivityDay }.count()
        val predecessorElements =  linkedActivity.backwardIterator().drop(1)
            .takeWhile { it != startHomeActivityDay }.count()

        require(maximumDuration in 1.minutes..1.days) {
            "This duration is not reasonable."
        }
        return 1.minutes..maximumDuration

    }

    companion object {
        fun create(
            tourStructures: Collection<BidirectionalIndexedValue<TourStructure>>,
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

    override val activityBudget: Map<ActivityType, Duration> =
        emptyMap<ActivityType, Duration>().withDefault { 0.minutes }

    override fun boundsFor(linkedActivity: LinkedActivity): ClosedRange<Duration> {
        throw UnsupportedOperationException("A home day has no bounds for other activities")
    }
}