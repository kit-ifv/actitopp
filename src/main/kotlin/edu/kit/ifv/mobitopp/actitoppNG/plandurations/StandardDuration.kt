package edu.kit.ifv.mobitopp.actitoppNG.plandurations

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.modernization.durations.MobilityPlanInputs
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.choicemodels.firstActivityUsesStandardDuration
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import kotlin.random.Random


fun interface StandardDuration {
    context(rng: Random)
    fun getAssignedStandardDuration(input: MobilityPlanInputs): Boolean
}

class UtilityFunctionAssignment(

    private val choiceModel: FixedChoiceModel<Boolean, BooleanDecisionAlternative> = firstActivityUsesStandardDuration,
) : StandardDuration {
    context(rng: Random)
    override fun getAssignedStandardDuration(input: MobilityPlanInputs): Boolean {

        val converter: (Boolean) -> BooleanDecisionAlternative = {
            BooleanDecisionAlternative(input)
        }
        return context(BooleanDecisionAlternative(input)) {
            choiceModel.select()
        }
    }

}

