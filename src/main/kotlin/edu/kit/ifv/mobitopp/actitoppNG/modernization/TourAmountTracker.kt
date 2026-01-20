package edu.kit.ifv.mobitopp.actitoppNG.modernization

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans.PlannedTourMap
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.sideTourAmounts.DefaultSideTourDeterminer
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.sideTourAmounts.GenerateSideToursFollowing
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.sideTourAmounts.GenerateSideToursPreceeding
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.sideTourAmounts.PrecedingInput
import kotlin.random.Random


interface PlannedTourAmounts {
    val precursorAmount: Int
    val successorAmount: Int

    companion object {
        val NONE: PlannedTourAmounts = ModifiablePlannedTourAmounts(0, 0)

        operator fun invoke(precursorAmount: Int, successorAmount: Int): PlannedTourAmounts {
            return object : PlannedTourAmounts {
                override val precursorAmount = precursorAmount
                override val successorAmount = successorAmount
            }
        }
    }
}

// TODO rename, since this is now used in both day -> tour prec/succ and in tour -> act prec&succ
data class ModifiablePlannedTourAmounts(
    override var precursorAmount: Int = 0,
    override var successorAmount: Int = 0,
) : PlannedTourAmounts


/**
 * For each day we will generate the amount of precursor and successor tours. This is equivalent to the legacy steps
 * 3A and 3B. Since the information of the previous day is passed on towards the calculation of the next day, we need
 * to track the calculation progress and pass it towards the calculation function.
 *
 * TODO define an interface to allow for different strategies to determine the planned tour amounts, because some maniac
 *   may use completely new attributes to determine the number of tours.
 */
class TourAmountTracker(
    initialDayStructures: Collection<DayStructure>,
    val person: PersonWithRoutine,
) {
    // Irealy wish that I could find a better solution than to allow every field to be modifiable, because home activities
    // should not have a modifiable field for tour amounts, but since it is private whatever
    private val map: PlannedTourMap = PlannedTourMap(initialDayStructures)


    fun output(): Map<DurationDay, PlannedTourAmounts> {
        return map.readOnly()
    }

    /**
     * Use ModifiableDayStructure as input to prevent home days to sneak into this calculation.
     */
    context(rng: Random)
    fun generateSideTours(targets: List<ModifiableDayStructure>): Map<DurationDay, PlannedTourAmounts> {
        generatePredecessorTourAmounts(targets)
        generateSuccessorTourAmounts(targets)
        return map.readOnly()
    }

    /** step 3A
     *
     */
    context(rng: Random)
    private fun generatePredecessorTourAmounts(targets: List<DayStructure>): List<Int> {
        val generator = GenerateSideToursPreceeding()
        return generateTourAmounts(targets, generator)
    }

    /**
     * Step 3B
     */
    context(rng: Random)
    private fun generateSuccessorTourAmounts(targets: List<DayStructure>): List<Int> {
        val generator = GenerateSideToursFollowing()
        return generateTourAmounts(targets, generator)
    }
    context(rng: Random)
    private fun <P> generateTourAmounts(
        targets: List<DayStructure>,
        strategy: DefaultSideTourDeterminer<P>,
    ): List<Int> {
        return targets.map {
            val currentPlan = map.getModifiablePlannedTourAmounts(it)
            val previousDayPlan = map.getPreviousPlannedTourAmounts(it)
            val result = strategy.generate(
                PrecedingInput(
                    person,
                    it,
                    currentPlan,
                    previousDayPlan
                )
            )
            strategy.update(currentPlan, result)
            result
        }
    }

}
context(rng: Random)
fun MobilityStructure.calculateTourAmounts(): Map<DurationDay, PlannedTourAmounts> {
    val tracker = TourAmountTracker(allDays(), person = this.weekRoutine)
    tracker.generateSideTours(mobileDays())
    return tracker.output()

}

