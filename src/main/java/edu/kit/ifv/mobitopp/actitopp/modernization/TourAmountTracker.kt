package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step3.DayWithBounds
import edu.kit.ifv.mobitopp.actitopp.steps.step3.GenerateSideToursFollowing
import edu.kit.ifv.mobitopp.actitopp.steps.step3.GenerateSideToursPreceeding
import edu.kit.ifv.mobitopp.actitopp.steps.step3.PlannedTourMap
import edu.kit.ifv.mobitopp.actitopp.steps.step3.PrecedingInput
import edu.kit.ifv.mobitopp.actitopp.steps.step3.PreviousDaySituation
import edu.kit.ifv.mobitopp.actitopp.steps.step3.step3AWithParams
import edu.kit.ifv.mobitopp.actitopp.steps.step3.step3BWithParams


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

data class ModifiablePlannedTourAmounts(
    override var precursorAmount: Int = 0,
    override var successorAmount: Int = 0,
): PlannedTourAmounts


data class ModifiableStructureWithPreviousDay(
    val currentDay: ModifiableDayStructure,
    val previousDay: DayStructure?,
)
/**
 * For each day we will generate the amount of precursor and successor tours. This is equivalent to the legacy steps
 * 3A and 3B. Since the information of the previous day is passed on towards the calculation of the next day, we need
 * to track the calculation progress and pass it towards the calculation function.
 *
 * TODO define an interface to allow for different strategies to determine the planned tour amounts, because some maniac
 *   may use completely new attributes to determine the number of tours.
 */
class TourAmountTracker(initialDayStructures: Collection<DayStructure>, val person: PersonWithRoutine, val rngHelper: RNGHelper = person.person.personalRNG) {
    // Irealy wish that I could find a better solution than to allow every field to be modifiable, because home activities
    // should not have a modifiable field for tour amounts, but since it is private whatever
    private val map: PlannedTourMap = PlannedTourMap(initialDayStructures)


    fun output(): Map<DurationDay, PlannedTourAmounts> {
        return map.readOnly()
    }

    fun generateSideTours(targets: List<ModifiableDayStructure>): Map<DurationDay, PlannedTourAmounts> {
        generatePredecessorTourAmounts(targets)
        generateSuccessorTourAmounts(targets)
        return map.readOnly()
    }
    /** step 3A
     *
     */
    private fun generatePredecessorTourAmounts(targets: List<ModifiableDayStructure>): List<Int> {
        val generator = GenerateSideToursPreceeding(rngHelper)
        return targets.map {
            val currentPlan = map.getModifiablePlannedTourAmounts(it)
            val previousDayPlan = map.getPreviousPlannedTourAmounts(it)
            val result = generator.generate(PrecedingInput(person,
                it,
                currentPlan,
                previousDayPlan))
            generator.update(currentPlan, result)
            result
        }
    }

    /**
     * Step 3B
     */
    private fun generateSuccessorTourAmounts(targets: List<ModifiableDayStructure>): List<Int> {
        val generator = GenerateSideToursFollowing(rngHelper)
        return targets.map {
            val currentPlan = map.getModifiablePlannedTourAmounts(it)
            val previousDayPlan = map.getPreviousPlannedTourAmounts(it)
            val result = generator.generate(PrecedingInput(person,
                it,
                currentPlan,
                previousDayPlan))
            generator.update(currentPlan, result)
            result
        }
    }

}

fun PatternStructure.calculateTourAmounts(person: PersonWithRoutine, rngHelper: RNGHelper = person.person.personalRNG): Map<DurationDay, PlannedTourAmounts> {
    val tracker = TourAmountTracker(allDays(), person = person, rngHelper = rngHelper)
    tracker.generateSideTours(mobileDays())
    return tracker.output()

}

fun interface CalculatePlannedTourAmounts {
    fun plannedTourAmounts(
        person: PersonWithRoutine,
        day: DayStructure,
        previousPlannedAmounts: PlannedTourAmounts?,
        randomNumber: Double,
        randomNumber2: Double,
    ): PlannedTourAmounts
}

object UtilityFunctionCalculator : CalculatePlannedTourAmounts {
    override fun plannedTourAmounts(
        person: PersonWithRoutine,
        day: DayStructure,
        previousPlannedAmounts: PlannedTourAmounts?,
        randomNumber: Double,
        randomNumber2: Double,
    ): PlannedTourAmounts {
        println("$randomNumber $randomNumber2")
        println(step3AWithParams.registeredOptions())
        val plannedPrecursorTours =
            step3AWithParams.select(randomNumber) { PreviousDaySituation(it, day, previousPlannedAmounts, person, 0) }
        val plannedSuccessorTours = step3BWithParams.select(randomNumber2) {
            PreviousDaySituation(
                it,
                day,
                previousPlannedAmounts,
                person,
                plannedPrecursorTours
            )
        }
        return ModifiablePlannedTourAmounts(plannedPrecursorTours, plannedSuccessorTours)
    }
}