package edu.kit.ifv.mobitopp.actitopp.modernization.plan

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.Activity
import edu.kit.ifv.mobitopp.actitopp.modernization.LinkedActivity
import edu.kit.ifv.mobitopp.actitopp.modernization.ModernizedActivity
import edu.kit.ifv.mobitopp.actitopp.modernization.Position
import edu.kit.ifv.mobitopp.actitopp.modernization.TourStructure
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine

/**
 * A tour plan has a fixed ordering of activities, which may never change, also there is a requirement of having a home
 * activity at the start and end of a tour.
 */
class TourPlan private constructor(
    private val linkedActivities: List<LinkedActivity>,
    val mainActivity: LinkedActivity

): List<LinkedActivity> by linkedActivities {

    override fun toString(): String {
        return "$linkedActivities"
    }

    companion object {
        fun create(    tourStructure: TourStructure,
                       person: PersonWithRoutine,
                       tripDuration: DetermineTripDuration,): TourPlan {
            val activityTypes = tourStructure.indexedElements()
            require(activityTypes.isNotEmpty()) {
                "A tour requires at least one activity, but constructor invoked with activityTypes=$activityTypes"
            }

            val linkedActivities = activityTypes.groupBy{it.position}.mapValues {  it.value.map { LinkedActivity(ModernizedActivity(activityType = it.element)) } }
            linkedActivities.values.flatten().zipWithNext().forEach { (first, second) ->
                first.link(
                    second, duration = tripDuration.everyOtherTourTrip(
                        person = person,
                        activityType = first.activityType

                    )
                )
            }
            return TourPlan(
                linkedActivities.values.flatten(), linkedActivities.getValue(Position.MAIN).first()
            )
        }
    }
}


