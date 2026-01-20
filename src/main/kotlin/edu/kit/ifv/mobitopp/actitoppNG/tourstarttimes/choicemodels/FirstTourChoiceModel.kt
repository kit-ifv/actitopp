package edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.choicemodels

import edu.kit.ifv.mobitopp.actitoppNG.plandurations.ActivityDurationHistograms
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.Identifier
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.MainDurationAlternative
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.generateHistogram
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.ArrayHistogram
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.parameters.ParameterCollectionStep10M
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.parameters.ParameterStep10M
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.parameters.ParametersStep10M

import edu.kit.ifv.mobitopp.actitoppNG.utils.times

private val firstTourStartUtility: ParameterStep10M.(MainDurationAlternative) -> Double = {
    base +
            (it.beruf_vollzeit()) * beruf_vollzeit +
            (it.beruf_teilzeit()) * beruf_teilzeit +
            (it.isStudent()) * beruf_schueler +
            (it.isVocational()) * beruf_azubi +
            (it.tourtyp_work()) * tourtyp_work +
            (it.tourtyp_education()) * tourtyp_education +
            (it.tourtyp_shopping()) * tourtyp_shopping +
            (it.tourtyp_transport()) * tourtyp_transport +
            (it.tag_sa()) * tag_sa +
            (it.tag_so()) * tag_so +
            (it.dauer_akt_tag_4bis6std()) * dauer_akt_tag_4bis6std +
            (it.dauer_akt_tag_6bis8std()) * dauer_akt_tag_6bis8std +
            (it.dauer_akt_tag_8bis10std()) * dauer_akt_tag_8bis10std +
            (it.dauer_akt_tag_10bis12std()) * dauer_akt_tag_10bis12std +
            (it.dauer_akt_tag_12bis14std()) * dauer_akt_tag_12bis14std +
            (it.taghat1akt()) * taghat1akt +
            (it.taghat2akt()) * taghat2akt +
            (it.taghat3akt()) * taghat3akt +
            (it.taghat1tour()) * taghat1tour +
            (it.taghat2touren()) * taghat2touren +
            (it.haupttour_work()) * haupttour_work +
            (it.haupttour_education()) * haupttour_education +
            (it.touristhaupttour()) * touristhaupttour


}
val FIRST_TOUR_HISTOGRAM: ActivityDurationHistograms<ParameterCollectionStep10M> = ParametersStep10M.generateHistogram(
    ArrayHistogram.fromResource(
        identifier = Identifier.FIRST_TOUR_START_TIME
    ),
    firstTourStartUtility
)