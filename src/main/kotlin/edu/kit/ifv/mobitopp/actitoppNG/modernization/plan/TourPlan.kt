package edu.kit.ifv.mobitopp.actitoppNG.modernization.plan

import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.modernization.LinkedActivity
import edu.kit.ifv.mobitopp.actitoppNG.modernization.ModernizedActivity
import edu.kit.ifv.mobitopp.actitoppNG.modernization.MutableTourStructure
import edu.kit.ifv.mobitopp.actitoppNG.utils.Position
import edu.kit.ifv.mobitopp.actitoppNG.utils.ceil
import edu.kit.ifv.mobitopp.actitoppNG.utils.takeUntil
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

/**
 * A tour plan has a fixed ordering of activities, which may never change, also there is a requirement of having a home
 * activity at the start and end of a tour.
 */
class TourPlan private constructor(
    private val linkedActivities: List<LinkedActivity>,
    val mainActivity: LinkedActivity,
    val position: Position,

    ) : List<LinkedActivity> by linkedActivities {
    val minorActivities = linkedActivities - mainActivity
    override fun toString(): String {
        return "$linkedActivities"
    }

    val nextHomeActivity get() = linkedActivities.last().next?.nextActivity
    val previousHomeActivity
        get() = linkedActivities.first().previousTrip?.previousActivity
            ?: throw IllegalStateException("A tour plan should always have a preceeding home activity")
    val activityDurations by lazy {
        linkedActivities.sumOf {
            it.duration?.toDouble(DurationUnit.MINUTES)
                ?: throw IllegalStateException("Some Activities have no duration yet set.")
        }.minutes
    }
    val activityDurationsWithTrips by lazy {
        (activityDurations + (first().previousTrip?.duration ?: ZERO) + (last().nextTrip?.duration
            ?: ZERO)).ceil(DurationUnit.MINUTES)
    }

    fun setStartTime(duration: Duration) {
        // Remember that the start time of a tour is not the start time of the first activity but the first trip, and thus the end time of the activity before the first activity (which is hopefully a home activity)
        val firstAct = first()
        firstAct.startTime = duration + (firstAct.previousTrip?.duration?.ceil(DurationUnit.MINUTES) ?: ZERO)
        // The previous home activity now has a fixed start time. TODO set the end time instead, that way we dont need to subtract the duration
        previousHomeActivity.endTime = duration
        previousHomeActivity.mutableIterator().takeUntil { it == nextHomeActivity }.forEach {
            if (it.startTime == null) {
                it.startTime = it.previous?.endTime ?: throw NoSuchElementException("Should not be null")
            }
        }
    }

    companion object {
        fun create(
            tourStructure: MutableTourStructure,
            person: PersonWithRoutine,
            tourPosition: Position,
            tripDuration: DetermineTripDuration,
        ): TourPlan {
            val activityTypes = tourStructure.indexedElements()
            require(activityTypes.isNotEmpty()) {
                "A tour requires at least one activity, but constructor invoked with activityTypes=$activityTypes"
            }

            val linkedActivities = activityTypes.groupBy { it.position }.mapValues { (key, value) ->
                value.map {
                    LinkedActivity(
                        ModernizedActivity(
                            activityType = it.element,
                            position = key
                        )
                    )
                }
            }
            linkedActivities.values.flatten().zipWithNext().forEach { (first, second) ->
                first.link(
                    second, duration = tripDuration.everyOtherTourTrip(
                        person = person,
                        activityType = first.activityType

                    )
                )
            }
            return TourPlan(
                linkedActivities.values.flatten(), linkedActivities.getValue(Position.MAIN).first(), tourPosition
            )
        }
    }
}


