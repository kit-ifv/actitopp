package edu.kit.ifv.mobitopp.actitopp.steps.step5

import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.modernization.BidirectionalIndexedValue
import edu.kit.ifv.mobitopp.actitopp.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitopp.modernization.TourStructure
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine

/**
 * Keep track of the amount of "activities" placed within the day, to avoid the side effect shenanigans of the legacy
 * code. We want to determine the amount of activities in bulk, before even considering spawning a single activity.
 */
class DayAmountTracker(val day: DayStructure, val rngHelper: RNGHelper, val personWithRoutine: PersonWithRoutine) {

    private var counter = 2 * day.amountOfElements()
    private var remainingPlacements = day.minimumAmountOfActivitiesByJointActions - day.amountOfActivities()

    private val successorGenerator = FollowingSpawns(rngHelper)
    private val predecessorGenerator = PrecedingSpawns(rngHelper)

    private val map: MutableMap<BidirectionalIndexedValue<TourStructure>, Int> = mutableMapOf()
    private val indexedTours = day.indexedElements()

    fun generatePredecessors(): Map<BidirectionalIndexedValue<TourStructure>, Int> {
        val predecessorActivityAmounts = indexedTours.associateWith {
            generatePredecessorActivityAmounts(it, calculateMinimumAmount()).also { amount ->
                remainingPlacements -= amount
                counter--
                map[it] = amount
            }
        }

        return predecessorActivityAmounts
    }

    fun generateSuccessors(): Map<BidirectionalIndexedValue<TourStructure>, Int> {
        val successorActivityAmounts = indexedTours.associateWith {
            generateSuccessorActivityAmounts(it, calculateMinimumAmount()).also { amount ->
                remainingPlacements -= amount
                counter--
            }
        }
        return successorActivityAmounts
    }
    private fun calculateMinimumAmount(): Int {
        return (remainingPlacements.toDouble() / counter).toInt()
    }

    private fun generatePredecessorActivityAmounts(
        input: BidirectionalIndexedValue<TourStructure>,
        minimumAmount: Int,
    ): Int {

        return predecessorGenerator.generateActivityAmount(
            SideTourActivityInput(
                personWithRoutine.person,
                personWithRoutine.routine,
                day,
                input,
                minimumAmount,
                map[input] ?: 0
            )
        )
    }

    private fun generateSuccessorActivityAmounts(
        input: BidirectionalIndexedValue<TourStructure>,
        minimumAmount: Int,
    ): Int {

        return successorGenerator.generateActivityAmount(
            SideTourActivityInput(
                personWithRoutine.person,
                personWithRoutine.routine,
                day,
                input,
                minimumAmount,
                map[input] ?: 0
            )
        )
    }
}

class PlannedActivityMap()