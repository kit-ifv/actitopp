package edu.kit.ifv.mobitopp.actitopp.modernization.plan

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.DurationDay
import edu.kit.ifv.mobitopp.actitopp.modernization.LinkedActivity
import edu.kit.ifv.mobitopp.actitopp.modernization.TourStructure
import edu.kit.ifv.mobitopp.actitopp.modernization.linkByHomeActivity
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step7.TimeBudgets
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes


interface DayPlan: List<LinkedActivity> {
    val firstHomeActivity: LinkedActivity
    val lastHomeActivity: LinkedActivity

    fun numberOfActivities(activityType: ActivityType): Int

    fun mainActivities(): List<LinkedActivity>

    val activityBudget: Map<ActivityType, Duration>
}

interface MutableDayPlan : DayPlan {
    override var firstHomeActivity: LinkedActivity
    override var lastHomeActivity: LinkedActivity
}
data class MovingDayPlanInput(
    val personWithRoutine: PersonWithRoutine,
    val tripDuration: DetermineTripDuration,
    val timeBudgets: TimeBudgets,
    val durationDay: DurationDay,
)
class MovingDayPlan(
    val activities: List<LinkedActivity>,
    val tourPlans: List<TourPlan>,
    timeBudgets: TimeBudgets,
    val durationDay: DurationDay,
): MutableDayPlan, List<LinkedActivity> by activities {

    override fun mainActivities(): List<LinkedActivity> {
        return tourPlans.map { it.mainActivity }
    }
    override var firstHomeActivity: LinkedActivity = activities.first()
    override var lastHomeActivity: LinkedActivity = activities.last()

    override fun numberOfActivities(activityType: ActivityType): Int {
        TODO("Not yet implemented")
    }
    val amountOfActivities: Int = activities.filter { it.activityType != ActivityType.HOME }.size

    // Amount of tours in plan, since amount of activities is handled above
    override val size: Int = tourPlans.size
    override val activityBudget: Map<ActivityType, Duration> = activities.
    groupBy { it.activityType }.mapValues { (timeBudgets[it.key] / it.value.size).coerceIn(1.minutes, 1440.minutes) }



    companion object {
        fun create( tourStructures: Collection<TourStructure>,
                   movingDayInput: MovingDayPlanInput): MovingDayPlan {
            return movingDayInput.run {
                val tourPlans: List<TourPlan> = tourStructures.map { it.toPlan(personWithRoutine) }
                val activities: List<LinkedActivity> = tourPlans.zipWithNext().flatMap { (tourA, tourB) ->
                    tourA.linkByHomeActivity(tourB, personWithRoutine, tripDuration)
                } + tourPlans.last()

                MovingDayPlan(activities, tourPlans, timeBudgets, durationDay)
            }

        }
    }
}

class HomeDayPlan: MutableDayPlan, List<LinkedActivity> by emptyList() {
    override var firstHomeActivity: LinkedActivity = LinkedActivity(activityType = ActivityType.HOME)
    override var lastHomeActivity: LinkedActivity = firstHomeActivity
    override fun numberOfActivities(activityType: ActivityType): Int = 0
    override fun mainActivities(): List<LinkedActivity> {
        return emptyList()
    }

    override val activityBudget: Map<ActivityType, Duration> = emptyMap<ActivityType, Duration>().withDefault { 0.minutes }
}