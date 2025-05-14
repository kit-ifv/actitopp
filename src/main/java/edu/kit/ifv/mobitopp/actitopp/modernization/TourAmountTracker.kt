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

data class DayPlannedTourAmounts(
    val dayStructure: DayStructure,
    val plannedAmounts: PlannedTourAmounts,
)

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
    // Irealy wish that I could find a better solution than to allow every field to be modifiable
    private val map: PlannedTourMap = PlannedTourMap(initialDayStructures)


    fun output(): Set<Map.Entry<DurationDay, PlannedTourAmounts>> {
        return map.readOnly().entries
    }
    private fun calculateFor(
        person: PersonWithRoutine,
        day: DayStructure,
        calculation: CalculatePlannedTourAmounts,
        randomNumber: Double,
        randomNumber2: Double,
    ) {
        val predecessor = map[day] ?: PlannedTourAmounts.NONE
        GenerateSideToursPreceeding(person.person.personalRNG)
    }
    fun generateSideTours(targets: List<ModifiableStructureWithPreviousDay>): Map<DurationDay, PlannedTourAmounts> {
        generatePredecessorTourAmounts(targets)
        generateSuccessorTourAmounts(targets)
        return map.readOnly()
    }
    /** step 3A
     *
     */
    fun generatePredecessorTourAmounts(targets: List<ModifiableStructureWithPreviousDay>): List<Int> {
        val generator = GenerateSideToursPreceeding(rngHelper)
        return targets.map {
            val currentPlan = map.getModifiablePlannedTourAmounts(it.currentDay)
            val previousDayPlan = map.getPreviousPlannedTourAmounts(it.currentDay)
            val result = generator.generate(PrecedingInput(person,
                it.currentDay,
                currentPlan,
                previousDayPlan))
            generator.update(currentPlan, result)
            result
        }
    }

    /**
     * Step 3B
     */
    fun generateSuccessorTourAmounts(targets: List<ModifiableStructureWithPreviousDay>): List<Int> {
        val generator = GenerateSideToursFollowing(rngHelper)
        return targets.map {
            val currentPlan = map.getModifiablePlannedTourAmounts(it.currentDay)
            val previousDayPlan = map.getPreviousPlannedTourAmounts(it.currentDay)
            val result = generator.generate(PrecedingInput(person,
                it.currentDay,
                currentPlan,
                previousDayPlan))
            generator.update(currentPlan, result)
            result
        }
    }

    fun calculateFor(
        patternStructure: PatternStructure,
        calculation: CalculatePlannedTourAmounts,
        randomValues: List<Double>,
    ) {
        patternStructure.allDays().withIndex().forEach { (index, dayStructure) ->
            calculateFor(
                patternStructure.weekRoutine,
                dayStructure,
                calculation,
                randomValues[index],
                randomValues[7 + index]
            )
        }
    }
}

fun PatternStructure.calculateTourAmounts(person: PersonWithRoutine, rngHelper: RNGHelper = person.person.personalRNG): Set<Map.Entry<DurationDay, PlannedTourAmounts>> {
    val tracker = TourAmountTracker(allDays(), person = person, rngHelper = rngHelper)
    tracker.generateSideTours(mobileDaysWithPredecessor())
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