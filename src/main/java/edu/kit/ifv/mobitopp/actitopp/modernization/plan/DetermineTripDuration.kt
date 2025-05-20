package edu.kit.ifv.mobitopp.actitopp.modernization.plan

import edu.kit.ifv.mobitopp.actitopp.Configuration
import edu.kit.ifv.mobitopp.actitopp.IPerson
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import units.Distance
import units.Speed
import units.kilometers
import units.kmh
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * This interface is used to determine the trip duration between two activities
 */
interface DetermineTripDuration {
    fun firstTourTrip(person: IPerson, activityType: ActivityType): Duration
    fun lastTourTrip(person: IPerson, activityType: ActivityType): Duration
    fun everyOtherTourTrip(person: IPerson, activityType: ActivityType): Duration
}

class StandardCommuteDurations(private val standardTripDuration: Duration = Configuration.FIXED_TRIP_TIME_ESTIMATOR) :
    DetermineTripDuration {


    override fun firstTourTrip(person: IPerson, activityType: ActivityType): Duration {
        return commuteBasedDuration(activityType, person)
    }


    override fun lastTourTrip(person: IPerson, activityType: ActivityType): Duration {
        return commuteBasedDuration(activityType, person)
    }

    override fun everyOtherTourTrip(person: IPerson, activityType: ActivityType): Duration {
        return standardTripDuration
    }

    private fun commuteBasedDuration(
        activityType: ActivityType,
        person: IPerson,
    ) = when {
        activityType == ActivityType.WORK && person.hasWorkCommuteInfo() -> {
            person.commutingdistance_work.kilometers.calculateCommuteDuration(::commuteSpeedWork)
        }

        activityType == ActivityType.EDUCATION && person.hasEducationCommuteInfo()

            -> {
            person.commutingdistance_education.kilometers.calculateCommuteDuration(::commuteSpeedEducation)
        }

        else -> everyOtherTourTrip(person, activityType)
    }


    fun Distance.calculateCommuteDuration(functor: (Distance) -> Speed): Duration {
        val speed = functor(this)
        val duration = this / speed
        return duration.coerceAtLeast(1.minutes)
    }

    private fun commuteSpeedWork(distance: Distance): Speed {
        return when (distance) {
            in 0.kilometers..5.kilometers -> 16.kmh
            in 5.kilometers..10.kilometers -> 29.kmh
            in 10.kilometers..20.kilometers -> 38.kmh
            in 20.kilometers..50.kilometers -> 51.kmh
            in 50.kilometers..Distance.MAX -> 67.kmh
            else -> 32.kmh
        }
    }

    private fun commuteSpeedEducation(distance: Distance): Speed {
        return when (distance) {
            in 0.kilometers..5.kilometers -> 12.kmh
            in 5.kilometers..10.kilometers -> 21.kmh
            in 10.kilometers..20.kilometers -> 28.kmh
            in 20.kilometers..50.kilometers -> 40.kmh
            in 50.kilometers..Distance.MAX -> 55.kmh
            else -> 21.kmh
        }
    }


    // TODO commutingdistance_work could be nullable instead of 0.0
    private fun IPerson.hasWorkCommuteInfo() = commutingdistance_work != 0.0

    private fun IPerson.hasEducationCommuteInfo() = commutingdistance_education != 0.0

    companion object {
        val STANDARD_ASSIGNMENT = StandardCommuteDurations()
    }
}