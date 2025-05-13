package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step3.PreviousDaySituation
import edu.kit.ifv.mobitopp.actitopp.steps.step3.step3AWithParams
import edu.kit.ifv.mobitopp.actitopp.steps.step3.step3BWithParams

data class DayPlannedTourAmounts(
    val dayStructure: DayStructure,
    val plannedAmounts: PlannedTourAmounts,
)

data class PlannedTourAmounts(
    val precursorAmount: Int,
    val successorAmount: Int,
)

/**
 * For each day we will generate the amount of precursor and successor tours. This is equivalent to the legacy steps
 * 3A and 3B. Since the information of the previous day is passed on towards the calculation of the next day, we need
 * to track the calculation progress and pass it towards the calculation function.
 *
 * TODO define an interface to allow for different strategies to determine the planned tour amounts, because some maniac
 *   may use completely new attributes to determine the number of tours.
 */
class TourAmountTracker {
    private val establishedTours: MutableList<DayPlannedTourAmounts> = mutableListOf()

    private fun calculateFor(
        person: PersonWithRoutine,
        day: DayStructure,
        calculation: CalculatePlannedTourAmounts,
        randomNumber: Double,
        randomNumber2: Double,
    ) {
        val lastElement = establishedTours.lastOrNull()?.plannedAmounts
        establishedTours.add(
            DayPlannedTourAmounts(
                day,
                calculation.plannedTourAmounts(person, day, lastElement, randomNumber, randomNumber2)
            )
        )
    }
    fun output(): List<DayPlannedTourAmounts> = establishedTours
    fun calculateFor(
        patternStructure: PatternStructure,
        calculation: CalculatePlannedTourAmounts,
        randomValues: List<Double>,
    ) {
        patternStructure.mobileDays().withIndex().forEach { (index, dayStructure) ->
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

fun PatternStructure.calculateTourAmounts(
    calculation: CalculatePlannedTourAmounts,
    randomValues: List<Double>,
): TourAmountTracker {
    val tracker = TourAmountTracker()
    tracker.calculateFor(this, calculation, randomValues)
    return tracker
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
        return PlannedTourAmounts(plannedPrecursorTours, plannedSuccessorTours)
    }
}