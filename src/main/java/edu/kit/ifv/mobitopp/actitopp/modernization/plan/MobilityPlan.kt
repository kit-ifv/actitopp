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
    tripDuration: DetermineTripDuration = StandardCommuteDurations(),
) {
    // Assume that the agent starts their plan at home.
    val startHomeAnchor = LinkedActivity.homeDay().apply {
        startTime = 0.minutes
        duration = 1.minutes
    }

    // And ends their mobility pattern at home.
    val endHomeAnchor = LinkedActivity.homeDay().apply {
        startTime = dayPlans.size.days - 1.minutes
        duration = 1.minutes
    }

    init {
        startHomeAnchor.link(
            activities.first(),
            duration = tripDuration.firstTourTrip(person, activities.first().activityType)
        )
        activities.last()
            .link(endHomeAnchor, duration = tripDuration.lastTourTrip(person, activities.last().activityType))
    }

    fun amountOfDaysWithActivity(activityType: ActivityType): Int {
        TODO()
    }

    val activityMap: Map<ActivityType, List<LinkedActivity>> = activities.groupBy { it.activityType }

    /**
     * An activity is regular, if the amount of activities per week is equal to the number of days with said activity
     */
    val regularActivities: Map<ActivityType, Boolean> by lazy {
        val dayMap = ActivityType.OUTOFHOMEACTIVITY.associateWith { actType ->
            dayPlans.count { it.hasActivity(actType) }
        }
        activities.groupBy { it.activityType }.mapValues { (key, value) ->
            value.size == dayMap[key]
        }
    }

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

