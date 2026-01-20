package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.sideTourAmounts

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.choicemodels.precursorAmountChoiceModel
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.PrecursorTourAmountSet
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans.PreviousDayAlternative
import edu.kit.ifv.mobitopp.actitoppNG.modernization.ModifiablePlannedTourAmounts
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel


class GenerateSideToursPreceeding(
    choiceModel: FixedChoiceModel<Int, PreviousDayAlternative> = precursorAmountChoiceModel,
) : DefaultSideTourDeterminer<PrecursorTourAmountSet>( choiceModel) {


    override fun determineMinimumAmountOfTours(remainingNumberOfTours: Int): Int {
        return remainingNumberOfTours / 2
    }


    override fun update(day: ModifiablePlannedTourAmounts, result: Int) {
        day.precursorAmount = result
    }
}