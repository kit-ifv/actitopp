package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.enums.JointStatus
import edu.kit.ifv.mobitopp.actitopp.enums.TripStatus


/**
 * @author Tim Hilgert
 */
class HTrip(parent: HActivity, type: TripStatus, tripduration: Int) {
    /**
     * @return the activity
     */
    // each trip is bound to an activity
    val activity: HActivity = parent

    // indicator if trip is one before or after an activity
    private val status: TripStatus

    var duration: Int = -1


    init {
        assert(tripduration > 0) { "duration is less or equal 0!" }
        this.status = type
        this.duration = tripduration
    }


    val isScheduled: Boolean
        /**
         * @return
         */
        get() = duration != -1

    val startTime: Int
        /**
         * @return
         */
        get() {
            var starttime = -1

            if (status == TripStatus.TRIP_BEFORE_ACT) {
                starttime = activity.startTime - duration
            }
            if (status == TripStatus.TRIP_AFTER_ACT) {
                starttime = activity.endTime
            }

            assert(starttime != -1) { "could not get TripStartTime" }
            return starttime
        }

    val endTime: Int
        /**
         * @return
         */
        get() {
            var endtime = -1

            if (status == TripStatus.TRIP_BEFORE_ACT) {
                endtime = activity.startTime
            }
            if (status == TripStatus.TRIP_AFTER_ACT) {
                endtime = activity.endTime + duration
            }

            assert(endtime != -1) { "could not get TripEndTime" }
            return endtime
        }

    val startTimeWeekContext: Int
        /**
         * @return
         */
        get() {
            var starttime = -1

            if (status == TripStatus.TRIP_BEFORE_ACT) {
                starttime = activity.startTimeWeekContext - duration
            }
            if (status == TripStatus.TRIP_AFTER_ACT) {
                starttime = activity.endTimeWeekContext
            }

            assert(starttime != -1) { "could not get TripStartTime" }
            return starttime
        }

    val endTimeWeekContext: Int
        /**
         * @return
         */
        get() {
            var endtime = -1

            if (status == TripStatus.TRIP_BEFORE_ACT) {
                endtime = activity.startTimeWeekContext
            }
            if (status == TripStatus.TRIP_AFTER_ACT) {
                endtime = activity.endTimeWeekContext + duration
            }

            assert(endtime != -1) { "could not get TripEndTime" }
            return endtime
        }

    val type: ActivityType
        /**
         * @return
         */
        get() {
            var type: ActivityType = ActivityType.UNKNOWN

            if (status == TripStatus.TRIP_BEFORE_ACT) {
                type = activity.activityType
            }

            /*
      * until now, trip after activities only occur after the last activity in a tour, thus they are always trips to home
      */
            if (status == TripStatus.TRIP_AFTER_ACT) {
                type = ActivityType.HOME
            }

            assert(type != ActivityType.UNKNOWN) { "could not get TripType" }
            return type
        }

    val jointStatus: JointStatus?
        /**
         * @return
         */
        get() {
            var jointStatus: JointStatus? = JointStatus.UNKNOWN

            if (status == TripStatus.TRIP_BEFORE_ACT) {
                jointStatus = activity.jointStatus
            }

            /*
      * until now, trip after activities are home trips and joint home trips are not yet supported
      */
            if (status == TripStatus.TRIP_AFTER_ACT) {
                jointStatus = JointStatus.NOJOINTELEMENT
            }

            assert(jointStatus != JointStatus.UNKNOWN) { "could not get jointStatus" }
            return jointStatus
        }


    override fun toString(): String {
        return "trip: start(week): $startTimeWeekContext / end(week): $endTimeWeekContext / duration: $duration"
    }
}
