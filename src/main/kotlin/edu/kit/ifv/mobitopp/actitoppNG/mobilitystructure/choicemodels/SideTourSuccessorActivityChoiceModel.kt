package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.choicemodels


import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.DefaultSideTourSuccessorParameters
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.SideTourSuccessorParameters
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.SideTourSuccessorSet
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans.TourAlternativeInt
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit

val step5BWithParams =
    DiscreteStructure<Int, TourAlternativeInt, SideTourSuccessorSet> {
        option(0) { 0.0 }
        option(1, parameters = { one }, {
            val util = standardUtilityFunction(this, it.second)
            util
        })
        option(2, parameters = { two }, { standardUtilityFunction(this, it.second) })
        option(3, parameters = { three }, { standardUtilityFunction(this, it.second) })
        option(4, parameters = { four }, { standardUtilityFunction(this, it.second) })
        option(5, parameters = { five }, { standardUtilityFunction(this, it.second) })
    }.multinomialLogit("Amount of successor activities in side tours").build(DefaultSideTourSuccessorParameters)


private val standardUtilityFunction: SideTourSuccessorParameters.(TourAlternativeInt) -> Double = {
    base +
            (it.isBeforeMainTour()) * tourliegtvorhaupttour +
            (it.isAfterMainTour()) * tourliegtnachhaupttour +
            (it.numActivitiesBeforeMainActivityIs1()) * anzaktvorhauptaktist1 +
            (it.numActivitiesBeforeMainActivityIs2()) * anzaktvorhauptaktist2 +
            (it.numActivitiesBeforeMainActivityIs3()) * anzaktvorhauptaktist3 +
            (it.tourMainActivityIsWork()) * tourtyp_work +
            (it.tourMainActivityIsEducation()) * tourtyp_education +
            (it.isSaturday()) * tag_sa +
            (it.isSunday()) * tag_so +
            (it.isAged18To35()) * alter_18bis35 +
            (it.isAged36To50()) * alter_36bis50 +
            (it.isAged51To60()) * alter_51bis60 +
            (it.amountOfToursIs1()) * taghat1tour +
            (it.amountOfToursIs2()) * taghat2touren +
            (it.averageAmountOfActivitiesIs1()) * mean_1akt +
            (it.averageAmountOfActivitiesIs2()) * mean_2akt +
            (it.averageAmountOfActivitiesIs3()) * mean_3akt

}
