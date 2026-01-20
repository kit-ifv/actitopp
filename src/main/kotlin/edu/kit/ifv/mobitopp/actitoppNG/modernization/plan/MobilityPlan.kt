package edu.kit.ifv.mobitopp.actitoppNG.modernization.plan

import edu.kit.ifv.mobitopp.actitoppNG.Person
import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.DurationDay
import edu.kit.ifv.mobitopp.actitoppNG.modernization.LinkedActivity
import edu.kit.ifv.mobitopp.actitoppNG.modernization.ModernizedActivity
import edu.kit.ifv.mobitopp.actitoppNG.modernization.linkByHomeActivity
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.TimeBudgets
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.times

interface MobilityPlan {
    val startHomeAnchor: LinkedActivity
    val endHomeAnchor: LinkedActivity
    val dayPlans: Collection<MutableDayPlan>
    val homePlans: Collection<HomeDayPlan>
    val activities: Collection<LinkedActivity>
    val timeBudgets: TimeBudgets
    val person: Person

    val mainActivityMap: Map<ActivityType, List<LinkedActivity>>
    val regularActivities: Map<ActivityType, Boolean>
    fun finish(): List<ModernizedActivity>
    fun isConsistent() = true
    fun extrudeHomeActivities() {

    }

}

class StayAtHomePlan(override val homePlans: Collection<HomeDayPlan>, override val person: Person) : MobilityPlan {

    private val singularHomeActivity = LinkedActivity.homeDay().apply {
        startTime = Duration.ZERO
        duration = homePlans.size * 1.days
    }
    override val startHomeAnchor: LinkedActivity = singularHomeActivity
    override val endHomeAnchor: LinkedActivity = singularHomeActivity
    override val dayPlans: Collection<MutableDayPlan> = emptyList()

    override val activities: Collection<LinkedActivity> = emptyList()
    override val timeBudgets: TimeBudgets = TimeBudgets.NONE
    override fun finish(): List<ModernizedActivity> {
        return listOf(singularHomeActivity.original)
    }

    override val mainActivityMap: Map<ActivityType, List<LinkedActivity>> = emptyMap()
    override val regularActivities: Map<ActivityType, Boolean> = emptyMap()

    companion object {
        fun create(days: Collection<DurationDay>, person: Person) =
            StayAtHomePlan(days.map { HomeDayPlan(it) }, person)
    }
}

class ActualMobilityPlan(
    override val dayPlans: Collection<MutableDayPlan>,
    override val homePlans: Collection<HomeDayPlan>,
    override val activities: Collection<LinkedActivity>,
    override val timeBudgets: TimeBudgets,
    override val person: Person,
    tripDuration: DetermineTripDuration = StandardCommuteDurations(),
) : MobilityPlan {
    // Assume that the agent starts their plan at home.
    override val startHomeAnchor = LinkedActivity.homeDay().apply {
        startTime = 0.minutes
        duration = 1.minutes
    }

    // And ends their mobility pattern at home.
    override val endHomeAnchor = LinkedActivity.homeDay().apply {
        startTime = (dayPlans.size + homePlans.size).days - 1.minutes + 3.hours
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

    override fun finish(): List<ModernizedActivity> = startHomeAnchor.activityIterator().map { it.original }.toList()

    override val mainActivityMap: Map<ActivityType, List<LinkedActivity>>
        get() = dayPlans.flatMap { it.tourPlans.map { it.mainActivity } }.groupBy { it.activityType }

    /**
     * An activity is regular, if the amount of activities per week is equal to the number of days with said activity
     */
    override val regularActivities: Map<ActivityType, Boolean> by lazy {
        val dayMap = ActivityType.OUTOFHOMEACTIVITY.associateWith { actType ->
            dayPlans.count { it.hasActivity(actType) }
        }
        activities.groupBy { it.activityType }.mapValues { (key, value) ->
            value.size == dayMap[key]
        }
    }


    override fun isConsistent(): Boolean {
        return startHomeAnchor.iterator().all {
            it.isConsistent()
        }
    }


    override fun extrudeHomeActivities() {
        startHomeAnchor.startTime = Duration.ZERO
        startHomeAnchor.activityIterator().filter { it.activityType == ActivityType.HOME }.forEach { act ->
            val previousAct = act.previous?.previousActivity
            previousAct?.let {

                val duration = it.endTime!! + act.previous!!.duration
                act.startTime = duration

            }

            val nextAct = act.next?.nextActivity
            nextAct?.let {
                val duration = it.startTime!! - act.next!!.duration - act.startTime!!
                require(duration >= Duration.ZERO) {
                    "The duration should never be negative, some other calculation messed up"
                }
                act.duration = duration
            }
        }
        endHomeAnchor.duration = endHomeAnchor.startTime!! - endHomeAnchor.previousTrip!!.endTime!! + 1.minutes
    }

    override fun toString(): String {
        return startHomeAnchor.activityIterator().joinToString(separator = "\n") { it.shortString() }
    }


    companion object {

        fun create(
            dayStructures: Collection<DayStructure>,
            homeDays: Collection<DurationDay>,
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


            val homePlans = homeDays.map {
                HomeDayPlan(it)
            }

            val activities = dayPlans.zipWithNext().flatMap { (firstDay, secondDay) ->
                firstDay.linkByHomeActivity(secondDay, personWithRoutine, tripDuration)
            } + dayPlans.last()
            return ActualMobilityPlan(
                dayPlans, homePlans, activities, timeBudgets, personWithRoutine
            )
        }
    }

}

