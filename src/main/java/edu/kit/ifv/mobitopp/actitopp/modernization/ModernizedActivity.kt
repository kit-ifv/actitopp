package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import java.util.NavigableSet
import java.util.TreeSet
import kotlin.time.Duration


interface Activity {
    val startTime: Duration
    val duration: Duration
    val endTime: Duration
    val activityType: ActivityType
}
/**
 *
 */
class ModernizedActivity(
    override val startTime: Duration,
    override val duration: Duration,
    override val activityType: ActivityType = ActivityType.UNKNOWN,
) : Activity, Comparable<ModernizedActivity> {

    init {
        // TODO maybe guard duration via a class wrapper that only allows positive durations, if we later want to make duration "var"
        require(duration >= Duration.ZERO) {
            "duration must be positive"
        }
    }

    override val endTime get() = startTime + duration

    /**
     * We can use interval comparisons to determine whether an interval is before, overlapping with, or after another
     * activity. This is also the encoding that we will use for comparisons:
     * -1
     */
    override fun compareTo(other: ModernizedActivity): Int {
        if (this.endTime < other.startTime) return -1
        if (this.startTime > other.endTime) return 1
        return 0 // We can assume that if neither interval is before nor after the other, that they must overlap.
    }
}
class LinkedActivity(val original: ModernizedActivity, var previousTrip: ModernizedTrip? = null, var nextTrip: ModernizedTrip? = null): Activity by original {

    fun linkAfter(other: ModernizedActivity): LinkedActivity {
        val next = LinkedActivity(other)
        TODO()
    }
}
class ModernizedTrip(
    val duration: Duration,
    var previousActivity: ModernizedActivity,
    val nextActivity: ModernizedActivity,
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