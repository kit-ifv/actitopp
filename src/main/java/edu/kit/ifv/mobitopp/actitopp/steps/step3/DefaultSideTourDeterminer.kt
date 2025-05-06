package edu.kit.ifv.mobitopp.actitopp.steps.step3

import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HTour
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ParametrizedDiscreteChoiceModel
import kotlin.math.max

abstract class DefaultSideTourDeterminer<P>(
    val rngHelper: RNGHelper,
    val choiceModel: ParametrizedDiscreteChoiceModel<Int, PreviousDaySituation, P>,
) : GenerateSideTours {
    override fun generate(precedingInput: PrecedingInput): List<Int> {

        var previousResult: Int? = null
        return precedingInput.run {

            days.map { day ->

                val availableOptions = determineAvailableOptions(day, precedingInput.input.routine)

                val rnd = rngHelper.randomValue
                val converter: (Int) -> PreviousDaySituation = {
                    createChoiceSituation(it, day, previousResult, precedingInput.input)

                }
                choiceModel.select(availableOptions, rnd, converter).also {
                    previousResult = it

                }


            }
        }
    }


    fun determineAvailableOptions(day: DayWithBounds, routine: WeekRoutine): Set<Int> {
        // If the main activity is staying home, there should not be the option to choose any other subtour
        if(day.day.mainTourType == ActivityType.HOME) return setOf(0)

        val availableOptions = choiceModel.registeredOptions().toMutableSet()
        val amountOfTours = day.amountOfTours
        // Nothing breaks if we omit the check whether amountOfTours is larger, because we cannot remove negative numbers
        // From the choice set.
        val remainingNumberOfTours = day.lowerBoundFromJointAction - amountOfTours
        val minimumNumberOfTours = determineMinimumAmountOfTours(remainingNumberOfTours)
        availableOptions.removeIf {
            // Integer division is fine: The options are integers, and apparently we spread about the half of the tours
            // before the main activity, and the other half after the remaining tour.
            it < minimumNumberOfTours
        }
        // TODO this block only triggers in the coordinated modelling phase, otherwise it can be omitted
        val avgAmountTours = routine.averageAmountOfTours

        // I have no clue why the original code only limits the available options if the number of tours is 1, 2 (but not 3 or any other option)
        if (avgAmountTours in 1..2) {
            availableOptions.removeIf {
                it > max(avgAmountTours, minimumNumberOfTours)
            }
        }
        return availableOptions
    }

    abstract fun spawnTour(day: HDay): HTour
    abstract fun determineMinimumAmountOfTours(remainingNumberOfTours: Int): Int
    abstract fun createChoiceSituation(
        choice: Int,
        day: DayWithBounds,
        previousResult: Int?,
        personWithRoutine: PersonWithRoutine,
    ): PreviousDaySituation
}