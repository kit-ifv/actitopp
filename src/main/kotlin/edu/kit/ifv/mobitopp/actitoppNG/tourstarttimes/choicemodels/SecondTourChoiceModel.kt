package edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.choicemodels

import edu.kit.ifv.mobitopp.actitoppNG.plandurations.Identifier
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.MainDurationAlternative
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.generateHistogram
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.ArrayHistogram
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.parameters.ParameterStep10O
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.parameters.ParametersStep10O
import edu.kit.ifv.mobitopp.actitoppNG.utils.times


private val standardUtilityFunction10O: ParameterStep10O.(MainDurationAlternative) -> Double = {
    base +
            (it.beruf_vollzeit()) * beruf_vollzeit +
            (it.beruf_teilzeit()) * beruf_teilzeit +
            (it.isStudent()) * beruf_schueler +
            (it.isVocational()) * beruf_azubi +
            (it.tourtyp_work()) * tourtyp_work +
            (it.tourtyp_education()) * tourtyp_education +
            (it.tourtyp_shopping()) * tourtyp_shopping +
            (it.tourtyp_transport()) * tourtyp_transport +
            (it.tag_fr()) * tag_fr +
            (it.tag_sa()) * tag_sa +
            (it.tag_so()) * tag_so +
            (it.taghat2akt()) * taghat2akt +
            (it.taghat3akt()) * taghat3akt +
            (it.taghat2touren()) * taghat2touren +
            (it.taghat3touren()) * taghat3touren +
            (it.dauer_akt_in_tour_0bis2std()) * dauer_akt_in_tour_0bis2std +
            (it.dauer_akt_in_tour_2bis4std()) * dauer_akt_in_tour_2bis4std +
            (it.dauer_akt_in_tour_4bis6std()) * dauer_akt_in_tour_4bis6std +
            (it.touristhaupttour()) * touristhaupttour +
            (it.endetourvorher_Std_12()) * endetourvorher_Std_12 +
            (it.endetourvorher_Std_13()) * endetourvorher_Std_13 +
            (it.endetourvorher_Std_14()) * endetourvorher_Std_14 +
            (it.endetourvorher_Std_15()) * endetourvorher_Std_15 +
            (it.endetourvorher_Std_16()) * endetourvorher_Std_16 +
            (it.endetourvorher_Std_17()) * endetourvorher_Std_17 +
            (it.endetourvorher_Std_18()) * endetourvorher_Std_18


}


val SECOND_TOUR_HISTOGRAM = ParametersStep10O.generateHistogram(
    ArrayHistogram.fromResource(

        identifier = Identifier.SECOND_TOUR_START_TIME
    ),
    standardUtilityFunction10O
)