package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType

/**
 * A tour structure is only the ordering of activity types taken during a tour, but not fully qualified activities, as
 * none of the duration flags such as start-time or duration are known yet. Later this tour structure should be
 * translated to a fully qualified tour. We try to honor the legacy approach of scaling along the main activity type
 * from index 0 and adding precursor and successor activity types by appending on both sides of the indices.
 *
 * We only generate a tour if there is a known main activity, thus we can require the first activity to be known before
 * instantiation.
 */
class TourStructure(
    mainActivityType: ActivityType,
) {
    private val queue : ArrayDeque<ActivityType> = ArrayDeque()
    private var offset = 0
    init {
        queue.add(mainActivityType)
    }

    fun addPrecursor(activityType: ActivityType) {
        queue.addFirst(activityType)
        offset++
    }
    fun addSuccessor(activityType: ActivityType) {
        queue.addLast(activityType)
    }
    fun mainActivityType(): ActivityType {
        return queue[offset]
    }

    fun withIndex() = queue.withIndex()
    /**
     * Access the activity type by index relative to the position of the main activity
     */
    operator fun get(index: Int): ActivityType {
        return queue[offset + index]
    }

    fun toTour() {

    }

}