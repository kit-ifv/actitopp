package edu.kit.ifv.mobitopp.actitopp.steps.step5

import edu.kit.ifv.mobitopp.actitopp.HActivity
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HTour
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.steps.step3.TourSituationInt
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ParametrizedDiscreteChoiceModel
import kotlin.math.roundToInt

abstract class SideTourActivityGenerator<P>(
    val rngHelper: RNGHelper,
    val choiceModel: ParametrizedDiscreteChoiceModel<Int, TourSituationInt, P>,
    val day: HDay,
): GenerateSideTourActivities {
    override fun generateActivitiesFor(input: SideTourActivityInput): List<Pair<HTour, Int>> {
        val day = input.currentDay
        val tours = day.tours

        val assignment = tours.withIndex().map {(i, tour) ->
            val options = choiceModel.registeredOptions().toMutableSet()
            val remainingExecutions = remainingNumberOfSteps(i)
            val remainingNumberToBePlaced = input.expectedLowerboundOfActivities - input.currentDay.totalAmountOfActivitites
            val minAmount = (remainingNumberToBePlaced.toDouble() / remainingExecutions).roundToInt()
            options.removeIf { it < minAmount }
            tour to choiceModel.select(options) {
                TourSituationInt(it, input.person, input.routine, day, tour)
            }

        }
        return assignment
    }

    fun spawnActivitiesFor(input: SideTourActivityInput) {
        val assignment = generateActivitiesFor(input)
        assignment.forEach { (tour, amount) ->
            repeat(amount) {
                spawnActivity(tour)
            }

        }
    }


    abstract fun remainingNumberOfSteps(executedSteps: Int): Int

    abstract fun spawnActivity(tour: HTour): HActivity

}

class PrecedingSpawns(rngHelper: RNGHelper, day: HDay) : SideTourActivityGenerator<ParameterCollectionStep5A>(rngHelper, step5AWithParams,
    day
) {
    override fun remainingNumberOfSteps(executedSteps: Int): Int {
        /* Assuming that the Follow-Activities are generated afterwards, we can assume that in this state the amount
           of remaining steps is 2 * (numTours - currentNum) because each remaining step, including this particular step
           will be executed twice, once for generating activities before the main activity, and once for generating
           activities after the main activity.
        */
        return 2 * (day.tours.size - executedSteps)
    }

    override fun spawnActivity(tour: HTour): HActivity {
        return tour.generatePrecedingActivity()
    }



}

class FollowingSpawns(rngHelper: RNGHelper, day: HDay) : SideTourActivityGenerator<ParameterCollectionStep5A>(rngHelper, step5AWithParams,
    day
) {

    override fun remainingNumberOfSteps(executedSteps: Int): Int {
        /*
        In this instance we assume that following spawns is executed after preceding spawns. Thus the explanation remains
        the same as for preceding spawns, but subtracted by one, since the precedingstep has been executed by now.
         */
        return 2 * (day.tours.size - executedSteps) - 1
    }

    override fun spawnActivity(tour: HTour): HActivity {
        return tour.generateFollowingActivity()
    }


}