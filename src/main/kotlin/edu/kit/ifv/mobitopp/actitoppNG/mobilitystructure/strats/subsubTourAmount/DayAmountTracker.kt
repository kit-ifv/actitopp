package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.subsubTourAmount

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.TourStructure
import edu.kit.ifv.mobitopp.actitoppNG.utils.BidirectionalIndexedValue
import kotlin.random.Random

/**
 * Keep track of the amount of "activities" placed within the day, to avoid the side effect shenanigans of the legacy
 * code. We want to determine the amount of activities in bulk, before even considering spawning a single activity.
 */
class DayAmountTracker(val day: DayStructure, val personWithRoutine: PersonWithRoutine) {

    private var counter = 2 * day.amountOfElements()
    private var remainingPlacements = day.minimumAmountOfActivitiesByJointActions - day.amountOfActivities()

    private val successorGenerator = FollowingSpawns()
    private val predecessorGenerator = PrecedingSpawns()

    /**
     * This map is required to fetch the calculation of the predecessor amount for each tour, since the predecessors
     * need to be calculated as a block, before the successors are calculated. Blame the original implementation for
     * that, because it makes no sense.
     */
    private val calculatedPredecessorAmount: MutableMap<BidirectionalIndexedValue<TourStructure>, Int> = mutableMapOf()
    context(rng: Random)
    fun generatePredecessors(indexedTours: Collection<BidirectionalIndexedValue<TourStructure>>): Map<BidirectionalIndexedValue<TourStructure>, Int> {
        val predecessorActivityAmounts = indexedTours.associateWith {
            generatePredecessor(it)
        }

        return predecessorActivityAmounts
    }
    context(rng: Random)
    fun generatePredecessor(indexedTour: BidirectionalIndexedValue<TourStructure>): Int {
        return generatePredecessorActivityAmounts(indexedTour, calculateMinimumAmount()).also { amount ->
            remainingPlacements -= amount
            counter--
            calculatedPredecessorAmount[indexedTour] = amount
        }
    }
    context(rng: Random)
    fun generateSuccessors(indexedTours: Collection<BidirectionalIndexedValue<TourStructure>>): Map<BidirectionalIndexedValue<TourStructure>, Int> {
        val successorActivityAmounts = indexedTours.associateWith {
            generateSuccessor(it)
        }
        return successorActivityAmounts
    }
    context(rng: Random)
    fun generateSuccessor(indexedTour: BidirectionalIndexedValue<TourStructure>): Int {
        return generateSuccessorActivityAmounts(indexedTour, calculateMinimumAmount()).also { amount ->
            remainingPlacements -= amount
            counter--
        }
    }

    private fun calculateMinimumAmount(): Int {
        return (remainingPlacements.toDouble() / counter).toInt()
    }
    context(rng: Random)
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
                0
            )
        )
    }
    context(rng: Random)
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
                calculatedPredecessorAmount[input] ?: 0
            )
        )
    }
}