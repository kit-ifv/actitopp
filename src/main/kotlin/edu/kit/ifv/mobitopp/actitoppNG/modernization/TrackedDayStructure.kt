package edu.kit.ifv.mobitopp.actitoppNG.modernization

import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.utils.BidirectionalIndexedValue

/**
 * This class is tasked with one job, to keep track of the activity types added to a day structure and update the
 * activityDayTracker accordingly, so that every step knows which activities take place during previous days, while
 * protecting direct manipulation access. It is deliberate that the bulk operations do not consider their internal
 * changes, since once the activity is allowed to be added to a day, additional activities of the same type would
 * be allowed anyways, since the day is classified as valid target for these activities now anyways.
 */
class TrackedDayStructure(
    private val activityDayTracker: ActivityDayTrackerImpl,
    private val dayStructure: ModifiableDayStructure,
) : DayStructure by dayStructure {


    fun loadSuccessors(activityTypes: Collection<ActivityType>) {
        dayStructure.loadSuccessors(activityTypes)
        activityDayTracker.add(activityTypes, day = dayStructure.startTimeDay)
    }

    fun loadPrecursors(activityTypes: Collection<ActivityType>) {
        dayStructure.loadPrecursors(activityTypes)
        activityDayTracker.add(activityTypes, day = dayStructure.startTimeDay)
    }

    fun trackedElements(): Collection<BidirectionalIndexedValue<TrackedTourStructure>> {
        return dayStructure.indexedElements().map {
            val original = it.element
            val absoluteIndex = it.absoluteIndex
            val offset = it.offset
            val tracked = TrackedTourStructure(dayStructure.startTimeDay, activityDayTracker, original)
            BidirectionalIndexedValue(absoluteIndex, offset, tracked)
        }
    }

    override fun equals(other: Any?): Boolean {
        return dayStructure == other
    }

    override fun hashCode(): Int {
        return dayStructure.hashCode()
    }
}

class TrackedTourStructure(
    private val durationDay: DurationDay,
    private val activityDayTracker: ActivityDayTrackerImpl,
    val original: MutableTourStructure,
) : TourStructure by original {

    fun loadPrecursors(activityTypes: Collection<ActivityType>) {
        original.loadPrecursors(activityTypes)
        activityDayTracker.add(activityTypes, day = durationDay)
    }

    fun loadSuccessors(activityTypes: Collection<ActivityType>) {
        original.loadSuccessors(activityTypes)
        activityDayTracker.add(activityTypes, day = durationDay)
    }

    override fun equals(other: Any?): Boolean {
        return original == other
    }

    override fun hashCode(): Int {
        return original.hashCode()
    }
}