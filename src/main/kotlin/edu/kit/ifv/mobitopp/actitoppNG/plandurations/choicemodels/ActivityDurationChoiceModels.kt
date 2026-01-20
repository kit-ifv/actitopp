package edu.kit.ifv.mobitopp.actitoppNG.plandurations.choicemodels

import edu.kit.ifv.mobitopp.actitoppNG.plandurations.Identifier
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.MainDurationAlternative
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.generateHistogram
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.parameters.ParameterStep8B
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.parameters.ParameterStep8D
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.parameters.ParameterStep8J
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.parameters.ParametersStep8B
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.parameters.ParametersStep8D
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.parameters.ParametersStep8J
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.ArrayHistogram
import edu.kit.ifv.mobitopp.actitoppNG.utils.times


private val minorFunction: ParameterStep8J.(MainDurationAlternative) -> Double = {
    base +
            (it.dauer_hauptakt_tag_4bis6std()) * dauer_hauptakt_tag_4bis6std +
            (it.dauer_hauptakt_tag_6bis8std()) * dauer_hauptakt_tag_6bis8std +
            (it.dauer_hauptakt_tag_8bis10std()) * dauer_hauptakt_tag_8bis10std +
            (it.dauer_hauptakt_tag_10bis12std()) * dauer_hauptakt_tag_10bis12std +
            (it.dauer_hauptakt_tag_12bis14std()) * dauer_hauptakt_tag_12bis14std +
            (it.dauer_hauptakt_tag_ueber14std()) * dauer_hauptakt_tag_ueber14std +
            (it.mittl_zeit_akt_1bis14min()) * mittl_zeit_akt_1bis14min +
            (it.mittl_zeit_akt_15bis29min()) * mittl_zeit_akt_15bis29min +
            (it.mittl_zeit_akt_30bis59min()) * mittl_zeit_akt_30bis59min +
            (it.mittl_zeit_akt_60bis119min()) * mittl_zeit_akt_60bis119min +
            (it.tag_so()) * tag_so +
            (it.aktzweck_work()) * aktzweck_work +
            (it.aktzweck_education()) * aktzweck_education +
            (it.aktzweck_shopping()) * aktzweck_shopping +
            (it.aktzweck_transport()) * aktzweck_transport +
            (it.taghat2akt()) * taghat2akt +
            (it.taghat3akt()) * taghat3akt +
            (it.taghat4akt()) * taghat4akt +
            (it.taghat5akt()) * taghat5akt +
            (it.taghat6akt()) * taghat6akt +
            (it.tourtyp_work()) * tourtyp_work +
            (it.tourtyp_education()) * tourtyp_education +
            (it.tourtyp_shopping()) * tourtyp_shopping +
            (it.tourtyp_transport()) * tourtyp_transport +
            (it.tourhat2akt()) * tourhat2akt +
            (it.taghat1tour()) * taghat1tour +
            (it.taghat2touren()) * taghat2touren +
            (it.aktliegtvorhauptakt()) * aktliegtvorhauptakt +
            (it.tourliegtvorhaupttour()) * tourliegtvorhaupttour +
            (it.tourliegtnachhaupttour()) * tourliegtnachhaupttour

}
private val standardUtilityFunction8D: ParameterStep8D.(MainDurationAlternative) -> Double = {
    base +
            (it.mittl_zeit_akt_1bis14min()) * mittl_zeit_akt_1bis14min +
            (it.mittl_zeit_akt_15bis29min()) * mittl_zeit_akt_15bis29min +
            (it.mittl_zeit_akt_30bis59min()) * mittl_zeit_akt_30bis59min +
            (it.mittl_zeit_akt_60bis119min()) * mittl_zeit_akt_60bis119min +
            (it.mittl_zeit_akt_120bis179min()) * mittl_zeit_akt_120bis179min +
            (it.mittl_zeit_akt_180bis239min()) * mittl_zeit_akt_180bis239min +
            (it.tag_fr()) * tag_fr +
            (it.tag_sa()) * tag_sa +
            (it.tag_so()) * tag_so +
            (it.anzaktwieanztagemitzweck()) * anzaktwieanztagemitzweck +
            (it.aktzweck_work()) * aktzweck_work +
            (it.aktzweck_education()) * aktzweck_education +
            (it.aktzweck_shopping()) * aktzweck_shopping +
            (it.aktzweck_transport()) * aktzweck_transport +
            (it.wochenzbudget_zweck_kat1()) * wochenzbudget_zweck_kat1 +
            (it.wochenzbudget_zweck_kat2()) * wochenzbudget_zweck_kat2 +
            (it.wochenzbudget_zweck_kat3()) * wochenzbudget_zweck_kat3 +
            (it.wochenzbudget_zweck_kat4()) * wochenzbudget_zweck_kat4 +
            (it.wochenzbudget_zweck_kat5()) * wochenzbudget_zweck_kat5 +
            (it.tourhat1akt()) * tourhat1akt +
            (it.tourhat2akt()) * tourhat2akt +
            (it.tourhat3akt()) * tourhat3akt +
            (it.taghat2akt()) * taghat2akt +
            (it.taghat3akt()) * taghat3akt +
            (it.letztetourdestages()) * letztetourdestages +
            (it.taghat2touren()) * taghat2touren +
            (it.beruf_vollzeit()) * beruf_vollzeit +
            (it.beruf_teilzeit()) * beruf_teilzeit +
            (it.beruf_schueler_azubi()) * beruf_schueler_azubi +
            (it.tourliegtvorhaupttour()) * tourliegtvorhaupttour +
            (it.tourliegtnachhaupttour()) * tourliegtnachhaupttour

}
private val standardUtilityFunction8B: ParameterStep8B.(MainDurationAlternative) -> Double = {

    base +
            (it.mittl_zeit_akt_60bis119min()) * mittl_zeit_akt_60bis119min +
            (it.mittl_zeit_akt_120bis179min()) * mittl_zeit_akt_120bis179min +
            (it.mittl_zeit_akt_180bis239min()) * mittl_zeit_akt_180bis239min +
            (it.mittl_zeit_akt_240bis299min()) * mittl_zeit_akt_240bis299min +
            (it.mittl_zeit_akt_300bis359min()) * mittl_zeit_akt_300bis359min +
            (it.mittl_zeit_akt_360bis419min()) * mittl_zeit_akt_360bis419min +
            (it.mittl_zeit_akt_420bis479min()) * mittl_zeit_akt_420bis479min +
            (it.tag_fr()) * tag_fr +
            (it.tag_sa()) * tag_sa +
            (it.tag_so()) * tag_so +
            (it.anzaktwieanztagemitzweck()) * anzaktwieanztagemitzweck +
            (it.aktzweck_work()) * aktzweck_work +
            (it.aktzweck_education()) * aktzweck_education +
            (it.aktzweck_shopping()) * aktzweck_shopping +
            (it.aktzweck_transport()) * aktzweck_transport +
            (it.wochenzbudget_zweck_kat1()) * wochenzbudget_zweck_kat1 +
            (it.wochenzbudget_zweck_kat2()) * wochenzbudget_zweck_kat2 +
            (it.wochenzbudget_zweck_kat3()) * wochenzbudget_zweck_kat3 +
            (it.wochenzbudget_zweck_kat4()) * wochenzbudget_zweck_kat4 +
            (it.wochenzbudget_zweck_kat5()) * wochenzbudget_zweck_kat5 +
            (it.tourhat1akt()) * tourhat1akt +
            (it.tourhat2akt()) * tourhat2akt +
            (it.tourhat3akt()) * tourhat3akt +
            (it.taghat1akt()) * taghat1akt +
            (it.taghat2akt()) * taghat2akt +
            (it.taghat3akt()) * taghat3akt +
            (it.taghat1tour()) * taghat1tour +
            (it.taghat2touren()) * taghat2touren +
            (it.beruf_vollzeit()) * beruf_vollzeit +
            (it.beruf_teilzeit()) * beruf_teilzeit +
            (it.beruf_schueler_azubi()) * beruf_schueler_azubi +
            (it.tourliegtvorhaupttour()) * tourliegtvorhaupttour +
            (it.tourliegtnachhaupttour()) * tourliegtnachhaupttour
}
val MINOR = ParametersStep8J.generateHistogram(
    ArrayHistogram.fromResource(
        identifier = Identifier.MINOR_ACTIVITY_DURATION
    ),
    minorFunction,
)
val MAJOR = ParametersStep8D.generateHistogram(
    ArrayHistogram.fromResource(
        identifier = Identifier.MAJOR_ACTIVITY_DURATION
    ),
    standardUtilityFunction8D,
)
val LEAD = ParametersStep8B.generateHistogram(
    ArrayHistogram.fromResource(
        identifier = Identifier.LEAD_ACTIVITY_DURATION
    ),
    standardUtilityFunction8B,
)