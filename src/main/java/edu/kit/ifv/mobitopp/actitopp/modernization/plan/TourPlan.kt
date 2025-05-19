package edu.kit.ifv.mobitopp.actitopp.modernization.plan

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.LinkedActivity
import edu.kit.ifv.mobitopp.actitopp.modernization.ModernizedActivity
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import java.time.Duration

/**
 * A tour plan has a fixed ordering of activities, which may never change, also there is a requirement of having a home
 * activity at the start and end of a tour.
 */
class TourPlan(activityTypes: Collection<ActivityType>, person: PersonWithRoutine) {
    private val linkedActivities: List<LinkedActivity>

    val startHomeActivity: LinkedActivity = LinkedActivity(ActivityType.HOME)
    val endHomeActivity: LinkedActivity = LinkedActivity(ActivityType.HOME)
    init {
        require(activityTypes.isNotEmpty()) {
            "A tour requires at least one activity, but constructor invoked with activityTypes=$activityTypes"
        }

        linkedActivities = activityTypes.map { LinkedActivity(ModernizedActivity(activityType = it))}
        linkedActivities.zipWithNext().forEach { (first, second) ->
            first.link(second)
        }
    }
}

/**
 * This interface is used to determine the trip duration between two activities
 */
interface DetermineTripDuration {
    fun firstTourTrip(person: ActitoppPerson, activityType: ActivityType): Duration
    fun lastTourTrip(person: ActitoppPerson, activityType: ActivityType): Duration
    fun everyOtherTourTrip(person: ActitoppPerson, activityType: ActivityType): Duration
}

object StandardCommuteDurations: DetermineTripDuration {


    override fun firstTourTrip(person: ActitoppPerson, activityType: ActivityType): Duration {
TODO()
    }

    override fun lastTourTrip(person: ActitoppPerson, activityType: ActivityType): Duration {
        TODO("Not yet implemented")
    }

    override fun everyOtherTourTrip(person: ActitoppPerson, activityType: ActivityType): Duration {
        TODO("Not yet implemented")
    }
}