package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.IPerson
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.DetermineTripDuration
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import java.util.NavigableSet
import java.util.TreeSet
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes


interface Activity {
    val startTime: Duration?
    val duration: Duration?
    val endTime: Duration?
    val activityType: ActivityType
}
/**
 *
 */

operator fun Duration.plus(nullable: Duration?): Duration {
    return nullable?.let { it + this } ?: this
}
class ModernizedActivity(
    override val activityType: ActivityType,
    override var startTime: Duration? = null,
    override var duration: Duration? = null,
) : Activity {



    override val endTime get() = startTime?.let { it + duration }


}
class LinkedActivity(val original: ModernizedActivity, var previousTrip: ModernizedTrip? = null, var nextTrip: ModernizedTrip? = null): Activity by original {
    constructor(activityType: ActivityType) : this(ModernizedActivity(activityType = activityType))


    fun link(other: LinkedActivity, duration: Duration = 15.minutes) {
        val trip = ModernizedTrip(
            duration = duration,
            previousActivity = this,
            nextActivity = other
        )

        this.nextTrip = trip
        other.previousTrip = trip
    }

    override fun toString(): String {
        return "$activityType start=($startTime) duration=($duration)"
    }
}

fun Collection<LinkedActivity>.linkByHomeActivity(other: Collection<LinkedActivity>, person: PersonWithRoutine, tripDuration: DetermineTripDuration): List<LinkedActivity> {
    val homeActivity = LinkedActivity(ActivityType.HOME)
    val lastElement = this.last()
    val nextElement = other.first()
    lastElement.link(homeActivity, tripDuration.lastTourTrip(
        person = person,
        activityType = lastElement.activityType
    ))
    homeActivity.link(nextElement, tripDuration.firstTourTrip(person, nextElement.activityType))
    return this + homeActivity
}
class ModernizedTrip(
    val duration: Duration,
    var previousActivity: LinkedActivity,
    var nextActivity: LinkedActivity,
)
