package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.sideTourAmounts

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans.PreviousDayAlternative
import edu.kit.ifv.mobitopp.actitoppNG.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.ModifiablePlannedTourAmounts
import edu.kit.ifv.mobitopp.actitoppNG.modernization.PlannedTourAmounts
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.WeekRoutine
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import kotlin.math.max
import kotlin.random.Random

abstract class DefaultSideTourDeterminer<P>(
    val choiceModel: FixedChoiceModel<Int, PreviousDayAlternative>,
) : GenerateSideTours {
    context(rng: Random)
    override fun generate(precedingInput: PrecedingInput): Int {

        return precedingInput.run {


            val availableOptions = determineAvailableOptions(day, precedingInput.personInfo.routine)
            context(
                createChoiceSituation(
                    dayTourPlan = currentPlannedTourAmounts,
                    previousTourPlan = previousPlannedTourAmounts,
                    day = day,
                    personWithRoutine = personInfo
                )
            ) {
                choiceModel.select(availableOptions)
            }



        }
    }

    abstract fun update(day: ModifiablePlannedTourAmounts, result: Int)
    private fun determineAvailableOptions(day: DayStructure, routine: WeekRoutine): Set<Int> {
        // If the main activity is staying home, there should not be the option to choose any other subtour
        if (day.isHomeDay()) return setOf(0)

        val availableOptions = choiceModel.choices.toMutableSet()
        val amountOfTours = day.amountOfPrecursorElements()
        // Nothing breaks if we omit the check whether amountOfTours is larger, because we cannot remove negative numbers
        // From the choice set.
        val remainingNumberOfTours = day.minimumAmountOfToursByJointActions - amountOfTours
        val minimumNumberOfTours = determineMinimumAmountOfTours(remainingNumberOfTours)
        availableOptions.removeIf {
            // Integer division is fine: The options are integers, and apparently we spread about the half of the tours
            // before the main activity, and the other half after the remaining tour.
            it < minimumNumberOfTours
        }
        val avgAmountTours = routine.averageAmountOfTours

        // I have no clue why the original code only limits the available options if the number of tours is 1, 2 (but not 3 or any other option)
        if (avgAmountTours in 1..2) {
            availableOptions.removeIf {
                it > max(avgAmountTours, minimumNumberOfTours)
            }
        }
        return availableOptions
    }

    abstract fun determineMinimumAmountOfTours(remainingNumberOfTours: Int): Int
    open fun createChoiceSituation(
        dayTourPlan: PlannedTourAmounts,
        previousTourPlan: PlannedTourAmounts?,
        day: DayStructure,
        personWithRoutine: PersonWithRoutine,
    ): PreviousDayAlternative {
        return PreviousDayAlternative(day, previousTourPlan, personWithRoutine, dayTourPlan.precursorAmount)
    }
}