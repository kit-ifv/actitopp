package edu.kit.ifv.mobitopp.actitopp.steps.step5

import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.steps.step3.TourSituationInt
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ParametrizedDiscreteChoiceModel

abstract class SideTourActivityGenerator<P>(
    val rngHelper: RNGHelper,
    val choiceModel: ParametrizedDiscreteChoiceModel<Int, TourSituationInt, P>,
) : GenerateSideTourActivities {
    override fun generateActivityAmount(input: SideTourActivityInput): Int {

        val options = choiceModel.registeredOptions().toMutableSet()
        options.removeIf { it < input.minimumAmountOfActivities }

        val converter: (Int) -> TourSituationInt = {
            TourSituationInt(it, input.person, input.routine, input.currentDay, input.tour, input.amountOfActivitiesBeforeMainAct)
        }
        return choiceModel.select(options, rngHelper.randomValue, converter)
    }


}

class PrecedingSpawns(rngHelper: RNGHelper) : SideTourActivityGenerator<ParameterCollectionStep5A>(
    rngHelper, step5AWithParams,
)

class FollowingSpawns(rngHelper: RNGHelper) : SideTourActivityGenerator<ParameterCollectionStep5B>(
    rngHelper, step5BWithParams,
)