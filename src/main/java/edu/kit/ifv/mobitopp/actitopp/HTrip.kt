package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.enums.JointStatus
import edu.kit.ifv.mobitopp.actitopp.enums.TripStatus

/**
 * A Trip in actitopp has:
 * @property activity an activity from which the trip is spawned
 * @property duration a duration encoded in a naked int. Presumed unit is minutes. TODO make duration instead.
 */
interface Trip {
    val activity: HActivity
    var duration: Int
}
/**
 * @author Tim Hilgert
 */
class HTrip(parent: HActivity, type: TripStatus, tripduration: Int) {

    init {
        require(tripduration != -1) {
            "This require block tries to locate scenarios where a trip is set to -1 duration, which has a specific encoding, should this happen you know" +
                    "that somewhere in actiTopp an unspecified duration is required, If you never see this message, you can kill the [isScheduled] field"
        }
    }

    var duration: Int = tripduration
        set(value) {
            require(value != -1) {
                "This require block tries to locate scenarios where a trip is set to -1 duration, which has a specific encoding, should this happen you know" +
                        "that somewhere in actiTopp an unspecified duration is required, If you never see this message, you can kill the [isScheduled] field"
            }
            field = value
        }
    /**
     * @return the activity
     */
    // each trip is bound to an activity
    val activity: HActivity = parent

    // indicator if trip is one before or after an activity
    private val status: TripStatus = type
    // The setter is only called in HActivity.createTripsforActivity()


    val isScheduled: Boolean get()  {

        return duration != -1
    }

    val startTime: Int get() {
        return when(status) {
            TripStatus.TRIP_BEFORE_ACT -> activity.startTime - duration
            TripStatus.TRIP_AFTER_ACT -> activity.endTime
        }
    }

    val endTime: Int get() {
        return when(status) {
            TripStatus.TRIP_BEFORE_ACT -> activity.startTime
            TripStatus.TRIP_AFTER_ACT -> activity.endTime + duration
        }
    }
    val startTimeWeekContext: Int get()  {
        return when(status) {
            TripStatus.TRIP_BEFORE_ACT -> activity.startTimeWeekContext - duration
             TripStatus.TRIP_AFTER_ACT -> activity.endTimeWeekContext
        }
    }
    val endTimeWeekContext: Int get() {
        return when(status) {
            TripStatus.TRIP_BEFORE_ACT -> activity.startTimeWeekContext
            TripStatus.TRIP_AFTER_ACT -> activity.endTimeWeekContext + duration
        }
    }
    // Apparently TRIP_AFTER_ACT means going Home  - Robin, 25
    val type: ActivityType get() {
        return when(status) {
            TripStatus.TRIP_BEFORE_ACT -> activity.activityType
            TripStatus.TRIP_AFTER_ACT -> ActivityType.HOME
        }
    }
    val jointStatus: JointStatus get() {
        return when(status) {
            TripStatus.TRIP_BEFORE_ACT -> activity.jointStatus
            TripStatus.TRIP_AFTER_ACT -> JointStatus.NOJOINTELEMENT // Apparently the original implementation does not support home trips, that cannot be joint home trips, for some reason
            //TODO debug and find out why in the legacy code the comment said "Home trips cannot be joint trips YET" - Robin, 25
        }
    }


    override fun toString(): String {
        return "trip: start(week): $startTimeWeekContext / end(week): $endTimeWeekContext / duration: $duration"
    }
}
