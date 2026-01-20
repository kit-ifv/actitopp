package edu.kit.ifv.mobitopp.actitoppNG.modernization.plan

import edu.kit.ifv.mobitopp.actitoppNG.Person
import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.utils.round
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.Speed
import edu.kit.ifv.units.kilometers
import edu.kit.ifv.units.kmh
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

/**
 * This interface is used to determine the trip duration between two activities
 */
interface DetermineTripDuration {
    fun firstTourTrip(person: Person, activityType: ActivityType): Duration
    fun lastTourTrip(person: Person, activityType: ActivityType): Duration
    fun everyOtherTourTrip(person: Person, activityType: ActivityType): Duration
}

class StandardCommuteDurations(private val standardTripDuration: Duration = 15.minutes) :
    DetermineTripDuration {


    override fun firstTourTrip(person: Person, activityType: ActivityType): Duration {
        return commuteBasedDuration(activityType, person)
    }


    override fun lastTourTrip(person: Person, activityType: ActivityType): Duration {
        return commuteBasedDuration(activityType, person)
    }

    override fun everyOtherTourTrip(person: Person, activityType: ActivityType): Duration {
        return standardTripDuration
    }

    private fun commuteBasedDuration(
        activityType: ActivityType,
        person: Person,
    ) = when {
        activityType == ActivityType.WORK && person.hasWorkCommuteInfo() -> {
            person.commutingdistanceWork.kilometers.calculateCommuteDuration(::commuteSpeedWork)
        }

        activityType == ActivityType.EDUCATION && person.hasEducationCommuteInfo()

            -> {
            person.commutingdistanceEducation.kilometers.calculateCommuteDuration(::commuteSpeedEducation)
        }

        else -> everyOtherTourTrip(person, activityType)
    }


    private fun Distance.calculateCommuteDuration(functor: (Distance) -> Speed): Duration {
        val speed = functor(this)
        val duration = this / speed
        return duration.coerceAtLeast(1.minutes).round(DurationUnit.MINUTES)
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

    private fun Person.hasWorkCommuteInfo() = commutingdistanceWork != 0.0

    private fun Person.hasEducationCommuteInfo() = commutingdistanceEducation != 0.0

    companion object {
        val STANDARD_ASSIGNMENT = StandardCommuteDurations()
    }
}