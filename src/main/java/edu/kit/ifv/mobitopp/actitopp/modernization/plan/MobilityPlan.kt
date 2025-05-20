package edu.kit.ifv.mobitopp.actitopp.modernization.plan

import edu.kit.ifv.mobitopp.actitopp.IPerson
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitopp.modernization.LinkedActivity
import edu.kit.ifv.mobitopp.actitopp.modernization.linkByHomeActivity
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step7.TimeBudgets
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class MobilityPlan(
    val dayPlans: Collection<MutableDayPlan>,
    val activities: Collection<LinkedActivity>,
    val timeBudgets: TimeBudgets,
    val person: IPerson,
) {

    val startHomeAnchor = LinkedActivity(ActivityType.HOME).apply {
        startTime = 0.minutes
        duration = 1.minutes
    }

    val endHomeAnchor = LinkedActivity(ActivityType.HOME).apply {
        startTime = dayPlans.size.days - 1.minutes
        duration = 1.minutes
    }

    fun amountOfDaysWithActivity(activityType: ActivityType): Int {
        TODO()
    }

    val activityMap: Map<ActivityType, List<LinkedActivity>> = activities.groupBy { it.activityType }


    fun calculateMeanTime(dayPlan: MutableDayPlan, activityType: ActivityType): Duration {
        TODO()
    }

    companion object {
        fun create(
            dayStructures: Collection<DayStructure>,
            timeBudgets: TimeBudgets,
            personWithRoutine: PersonWithRoutine,
            tripDuration: DetermineTripDuration,
        ): MobilityPlan {
            val counts =
                ActivityType.FULLSET.associateWith { activityType -> dayStructures.filter { day -> activityType in day }.size }
            val dayTimeBudgets = timeBudgets.toDayTimeBudget(counts)

            val dayPlans = dayStructures.map {
                it.toDayPlan(
                    MovingDayPlanInput(
                        personWithRoutine = personWithRoutine,
                        tripDuration = tripDuration,
                        timeBudgets = dayTimeBudgets,
                        durationDay = it.startTimeDay
                    )
                )
            }
            val activities = dayPlans.zipWithNext().flatMap { (firstDay, secondDay) ->
                firstDay.linkByHomeActivity(secondDay, personWithRoutine, tripDuration)
            } + dayPlans.last()
            return MobilityPlan(
                dayPlans, activities, timeBudgets, personWithRoutine
            )
        }
    }

}

