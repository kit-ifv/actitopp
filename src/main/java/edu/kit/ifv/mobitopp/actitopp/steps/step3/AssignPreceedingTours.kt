package edu.kit.ifv.mobitopp.actitopp.steps.step3

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.PersonWeekRoutine
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.steps.scrapPath.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.utils.zipWithPrevious
import kotlin.math.max
import kotlin.math.min

data class PrecedingInput(
    val input: PersonWithRoutine,
    val lowerBoundsFromJointActions: List<Int>,
    val days: List<HDay>,
)

fun interface GeneratePrecedingTours {
    fun generate(precedingInput: PrecedingInput): List<Int>
    fun generate(person: ActitoppPerson, routine: PersonWeekRoutine, intArray: IntArray, days: List<HDay>): List<Int> =
        generate(PrecedingInput(PersonWithRoutine(person, routine), intArray.toList(), days))
}


class GeneratePrecedingToursDefault(val rngHelper: RNGHelper) : GeneratePrecedingTours {
    override fun generate(precedingInput: PrecedingInput): List<Int> {

        var previousDayPrecedingTours: Int? = null
        return precedingInput.run {

            days.zipWithPrevious().map { (previous, day) ->
                val availableOptions = step3AWithParams.registeredOptions().toMutableSet()
                val amountOfTours = 1 // In step 3A we can safely assume that there be only 1 tour, the main tour
                // Nothing breaks if we omit the check whether amountOfTours is larger, because we cannot remove negative numbers
                // From the choice set.
                val remainingNumberOfTours = lowerBoundsFromJointActions[day.index] - amountOfTours
                val minimumNumberOfTours = remainingNumberOfTours / 2
                availableOptions.removeIf {
                    // Integer division is fine: The options are integers, and apparently we spread about the half of the tours
                    // before the main activity, and the other half after the remaining tour.
                    it < minimumNumberOfTours
                }
                // TODO this block only triggers in the coordinated modelling phase, otherwise it can be omitted
                val avgAmountTours = input.routine.averageAmountOfTours

                // I have no clue why the original code only limits the available options if the number of tours is 1, 2 (but not 3 or any other option)
                if (avgAmountTours in 1..2) {
                    availableOptions.removeIf {
                        it > max(avgAmountTours, minimumNumberOfTours)
                    }
                }

                val rnd = rngHelper.randomValue
                val converter: (Int) -> PreviousDaySituation = {
                    PreviousDaySituation(it, day, previousDayPrecedingTours, null, input.person, input.routine)
                }
//            val utilities = step3AWithParams.utilities(availableOptions, converter)
//            println("New: [$rnd] ${step3AWithParams.probabilities(availableOptions, converter)}")
//                println("New: $availableOptions")
//            println("New: [$rnd] $utilities")


                step3AWithParams.select(availableOptions, rnd, converter).also { previousDayPrecedingTours = it }


            }
        }
    }
}