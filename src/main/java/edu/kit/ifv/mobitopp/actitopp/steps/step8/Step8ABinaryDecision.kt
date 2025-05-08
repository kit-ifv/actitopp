package edu.kit.ifv.mobitopp.actitopp.steps.step8

import edu.kit.ifv.mobitopp.actitopp.Configuration
import edu.kit.ifv.mobitopp.actitopp.HActivity
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.KnownDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters


fun interface MainActivity {
    fun getAssignedStandardDuration(day: HDay): List<HActivity>
}

class UtilityFunctionAssignment(): MainActivity {
    override fun getAssignedStandardDuration(day: HDay): List<HActivity> {
        val potentialActivities = day.tours.mapNotNull { it.mainActivity() }.filter { it.activityType == ActivityType.WORK || it.activityType == ActivityType.EDUCATION }
        if(!Configuration.coordinated_modelling) return emptyList() // Don't need to bother with anything if this is flag aint there, for some reason ? :/ ?
//        day.tours.
        return TODO()
    }

    private val choiceModel = ModifiableDiscreteChoiceModel<Boolean, BooleanSituation, ParameterCollectionStep8A>(
        AllocatedLogit.create {
            option(true) {0.0}
            option(false, parameters = {no}) {
                TODO()
//                base +
//                (it.aktzweck_work()) * aktzweck_work +
//                    (it.aktzweck_education()) * aktzweck_education +
//                    (it.anzaktwieanztagemitzweck()) * anzaktwieanztagemitzweck +
//                    (it.beruf_vollzeit()) * beruf_vollzeit +
//                    (it.beruf_teilzeit()) * beruf_teilzeit +
//                    (it.taghat1akt()) * taghat1akt +
//                    (it.taghat2akt()) * taghat2akt +
//                    (it.taghat3akt()) * taghat3akt +
//                    (it.taghat1tour()) * taghat1tour +
//                    (it.taghat2touren()) * taghat2touren +
//                    (it.tag_sa()) * tag_sa +
//                    (it.tag_so()) * tag_so +
//                    (it.mittl_zeit_akt_1bis14min()) * mittl_zeit_akt_1bis14min +
//                    (it.mittl_zeit_akt_15bis29min()) * mittl_zeit_akt_15bis29min +
//                    (it.mittl_zeit_akt_30bis59min()) * mittl_zeit_akt_30bis59min +
//                    (it.mittl_zeit_akt_60bis119min()) * mittl_zeit_akt_60bis119min +
//                    (it.mittl_zeit_akt_120bis179min()) * mittl_zeit_akt_120bis179min +
//                    (it.mittl_zeit_akt_180bis239min()) * mittl_zeit_akt_180bis239min +
//                    (it.mittl_zeit_akt_240bis299min()) * mittl_zeit_akt_240bis299min +
//                    (it.mittl_zeit_akt_300bis359min()) * mittl_zeit_akt_300bis359min +
//                    (it.mittl_zeit_akt_360bis419min()) * mittl_zeit_akt_360bis419min +
//                    (it.mittl_zeit_akt_420bis479min()) * mittl_zeit_akt_420bis479min +
//                    (it.tourhat1akt()) * tourhat1akt +
//                    (it.tourhat2akt()) * tourhat2akt +
//                    (it.tourhat3akt()) * tourhat3akt
            }
        }
    ).initializeWithParameters(ParametersStep8A)
}
private class BooleanSituation: ChoiceSituation<Boolean>() {
    override val choice: Boolean
        get() = TODO("Not yet implemented")

}
private val ParametersStep8A = ParameterCollectionStep8A(
    no = ParameterStep8A(
        base = 0.3709,
        aktzweck_work = -0.5610,
        aktzweck_education = -0.7771,
        anzaktwieanztagemitzweck = -0.8515,
        beruf_vollzeit = 0.0500,
        beruf_teilzeit = -0.1152,
        taghat1akt = -0.3783,
        taghat2akt = -0.2701,
        taghat3akt = -0.1396,
        taghat1tour = 0.3030,
        taghat2touren = 0.0732,
        tag_sa = 0.3699,
        tag_so = 0.3027,
        mittl_zeit_akt_1bis14min = -1.5198,
        mittl_zeit_akt_15bis29min = -1.3527,
        mittl_zeit_akt_30bis59min = -0.4782,
        mittl_zeit_akt_60bis119min = -0.3813,
        mittl_zeit_akt_120bis179min = 0.0175,
        mittl_zeit_akt_180bis239min = 0.6227,
        mittl_zeit_akt_240bis299min = 0.5606,
        mittl_zeit_akt_300bis359min = 0.6856,
        mittl_zeit_akt_360bis419min = 0.7753,
        mittl_zeit_akt_420bis479min = 0.7963,
        tourhat1akt = -0.7774,
        tourhat2akt = -0.5889,
        tourhat3akt = -0.3665,
    )
)

private data class ParameterCollectionStep8A(
    val no: ParameterStep8A
)

private data class ParameterStep8A(
    val base: Double,
    val aktzweck_work: Double,
    val aktzweck_education: Double,
    val anzaktwieanztagemitzweck: Double,
    val beruf_vollzeit: Double,
    val beruf_teilzeit: Double,
    val taghat1akt: Double,
    val taghat2akt: Double,
    val taghat3akt: Double,
    val taghat1tour: Double,
    val taghat2touren: Double,
    val tag_sa: Double,
    val tag_so: Double,
    val mittl_zeit_akt_1bis14min: Double,
    val mittl_zeit_akt_15bis29min: Double,
    val mittl_zeit_akt_30bis59min: Double,
    val mittl_zeit_akt_60bis119min: Double,
    val mittl_zeit_akt_120bis179min: Double,
    val mittl_zeit_akt_180bis239min: Double,
    val mittl_zeit_akt_240bis299min: Double,
    val mittl_zeit_akt_300bis359min: Double,
    val mittl_zeit_akt_360bis419min: Double,
    val mittl_zeit_akt_420bis479min: Double,
    val tourhat1akt: Double,
    val tourhat2akt: Double,
    val tourhat3akt: Double,

)