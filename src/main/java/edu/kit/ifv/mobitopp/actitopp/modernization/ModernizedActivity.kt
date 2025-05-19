package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
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


}
class ModernizedTrip(
    val duration: Duration,
    var previousActivity: LinkedActivity,
    val nextActivity: LinkedActivity,
)

class ModernizedTour() {
    // Activities within a tour are ordered
    private val activities: NavigableSet<LinkedActivity> = TreeSet()

    fun add(activity: ModernizedActivity): Boolean {

        val element = LinkedActivity(activity)
        if (element in activities) {
            return false // TODO maybe even throw, in regular program flow this should not occur
        }

        activities.add(element)
        val pred = activities.lower(element)
        val succ = activities.higher(element)

//        pred.
        return true
    }
}