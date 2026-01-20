package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.choicemodels


import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.DefaultPrecursorTourParameters
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.PrecursorTourAmountParameters
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.PrecursorTourAmountSet
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans.PreviousDayAlternative
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit

val precursorAmountChoiceModel =
    DiscreteStructure<Int, PreviousDayAlternative, PrecursorTourAmountSet> {
        option(0) { 0.0 }
        option(1, parameters = { one }, {
            val util = standardUtilityFunction(this, it.second)
            util
        })
        option(2, parameters = { two }, { standardUtilityFunction(this, it.second) })
        option(3, parameters = { three }, { standardUtilityFunction(this, it.second) })
        option(4, parameters = { four }, { standardUtilityFunction(this, it.second) })
        option(5, parameters = { five }, { standardUtilityFunction(this, it.second) })
    }.multinomialLogit("Amount of precursor tours (tours before main tour) per day")
        .build(DefaultPrecursorTourParameters)
private val standardUtilityFunction: PrecursorTourAmountParameters.(PreviousDayAlternative) -> Double = {
    base +
            (it.isFulltimeEmployee()) * employmentFullTime +
            (it.dayMainActivityIsWork()) * mainActivityIsWork +
            (it.dayMainActivityIsEducation()) * mainActivityIsEducation +
            (it.dayMainActivityIsShopping()) * mainActivityIsShopping +
            (it.isSaturday()) * saturday +
            (it.isSunday()) * sunday +
            (it.isMale()) * male +
            (it.isAged10To17()) * age10To17 +
            (it.isAged26To35()) * aged26To35 +
            (it.isAged36To50()) * aged36To50 +
            (it.isAged51To60()) * aged51To60 +
            (it.commuteIn0To5km()) * commuteIn0To5km +
            (it.averageAmountOfToursIs1()) * averageAmountOfToursIs1 +
            (it.averageAmountOfToursIs2()) * averageAmountOfToursIs2 +
            (it.previousDayHasNoBeforeTour()) * previousDayHas0TourBeforeMainAct +
            (it.previousDayHasOneBeforeTour()) * previousDayHas1TourBeforeMainAct
}