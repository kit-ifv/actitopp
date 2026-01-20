package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.choicemodels

import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.DefaultSuccessorTourParameters
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.SuccessorTourAmountParameters
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.SuccessorTourAmountSet
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans.PreviousDayAlternative
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit


val successorAmountChoiceModel =
    DiscreteStructure<Int, PreviousDayAlternative, SuccessorTourAmountSet> {
        option(0) { 0.0 }
        option(1, parameters = { one }, {
            val util = standardUtilityFunction(this, it.second)
            util
        })
        option(2, parameters = { two }, { standardUtilityFunction(this, it.second) })
        option(3, parameters = { three }, { standardUtilityFunction(this, it.second) })
        option(4, parameters = { four }, { standardUtilityFunction(this, it.second) })
        option(5, parameters = { five }, { standardUtilityFunction(this, it.second) })
    }.multinomialLogit("Amount of successor tours (tours after main tour)").build(DefaultSuccessorTourParameters)


private val standardUtilityFunction: SuccessorTourAmountParameters.(PreviousDayAlternative) -> Double = {
    base +
            (it.isParttimeEmployee()) * employmentPartTime +
            (it.dayMainActivityIsWork()) * mainActivityIsWork +
            (it.dayMainActivityIsEducation()) * mainActivityIsEducation +
            (it.dayMainActivityIsShopping()) * mainActivityIsShopping +
            (it.isFriday()) * friday +
            (it.isSaturday()) * saturday +
            (it.isSunday()) * sunday +
            (it.isAged26To35()) * aged26To35 +
            (it.isAged36To50()) * aged36To50 +
            (it.isAged51To60()) * aged51To60 +
            (it.areaTypeConurbation()) * areaTypeConurbation +
            (it.areaTypeRural()) * areaTypeRural +
            (it.amountOfPKW()) * amountOfPKW +
            (it.amountOfBeforeTours()) * amountOfToursBeforeMainAct +
            (it.commuteOver50km()) * commuteOver50km +
            (it.commuteIn0To5km()) * commuteIn0To5km +
            (it.averageAmountOfToursIs1()) * averageAmountOfToursIs1 +
            (it.averageAmountOfToursIs2()) * averageAmountOfToursIs2 +
            (it.previousDayHasNoAfterTour()) * previousDayHas0TourAfterMainAct +
            (it.previousDayHasOneAfterTour()) * previousDayHas1TourAfterMainAct
}
