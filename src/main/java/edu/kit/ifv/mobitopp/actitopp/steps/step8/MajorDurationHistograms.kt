package edu.kit.ifv.mobitopp.actitopp.steps.step8

import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.steps.step7.ArrayHistogram
import edu.kit.ifv.mobitopp.actitopp.steps.step7.indexOfSearch
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.UtilityFunction
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.selectNew
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.time.Duration


/**
 * Contains the histograms originally found in 8C
 */
 class MajorDurationHistograms(
    val histograms: List<ArrayHistogram>,

) {
    init {
        require(histograms.size == 15) {

        }
    }
    /**
     * Find the proper histogram, select between histogram and neighbors, where main histogram gets a 1.1 boost.
     * Select duration from selected histogram.
     */
    fun chooseWithinNeighbors(rngHelper: RNGHelper, duration: Duration, converter:  (ArrayHistogram) -> MainDurationSituation): Duration {
        val index =  histograms.binarySearch { it.compareTo(duration.inWholeMinutes.toInt()) }.indexOfSearch()
        val mainHistogram = histograms[index]
        val previousHistogram = histograms.getOrNull(index - 1)
        val nextHistogram = histograms.getOrNull(index + 1)

        val selectedHistogram = choiceModel.selectNew(rngHelper.randomValue, converter) {
            previousHistogram?.let {
                option(it)
            }
            option(mainHistogram)  { original ->
                UtilityFunction { a, b ->
                    1.1 * original.calculateUtility(a, b)
                }
            }
            nextHistogram?.let {
                option(it)
            }
        }
        TODO("Modify target distribution will be required here, and each person requires a copy of the relevant histograms to deface")
        return selectedHistogram.select(rngHelper.randomValue)
    }

    fun select(rngHelper: RNGHelper, converter: (ArrayHistogram) -> MainDurationSituation): Duration {
        return choiceModel.select(rngHelper.randomValue, converter).select(rngHelper.randomValue)
    }

    val choiceModel = ModifiableDiscreteChoiceModel<ArrayHistogram, MainDurationSituation, ParameterCollectionStep8B>(
        AllocatedLogit.create {
            bulk(histograms, parameterConversions = collections()) {
                standardUtilityFunction(this, it)
            }
//            option(histogram1, parameters = {first}) { standardUtilityFunction(this, it) }
//            option(histogram2, parameters = {second}) { standardUtilityFunction(this, it) }
//            option(histogram3, parameters = {third}) { standardUtilityFunction(this, it) }
//            option(histogram4, parameters = {fourth}) { standardUtilityFunction(this, it) }
//            option(histogram5, parameters = {fifth}) { standardUtilityFunction(this, it) }
//            option(histogram6, parameters = {sixth}) { standardUtilityFunction(this, it) }
//            option(histogram7, parameters = {seventh}) { standardUtilityFunction(this, it) }
//            option(histogram8, parameters = {eighth}) { standardUtilityFunction(this, it) }
//            option(histogram9, parameters = {ninth}) { standardUtilityFunction(this, it) }
//            option(histogram10, parameters = {tenth}) { standardUtilityFunction(this, it) }
//            option(histogram11, parameters = {eleventh}) { standardUtilityFunction(this, it) }
//            option(histogram12, parameters = {twelfth}) { standardUtilityFunction(this, it) }
//            option(histogram13, parameters = {thirteenth}) { standardUtilityFunction(this, it) }
//            option(histogram14, parameters = {fourteenth}) { standardUtilityFunction(this, it) }
//            option(histogram15, parameters = {fifteenth}) { standardUtilityFunction(this, it) }



        }
    ).initializeWithParameters(ParametersStep8B)


    companion object {
        val DEFAULT = fromResourcePath()
        fun fromResourcePath(path: Path = Path("src/main/resources/edu/kit/ifv/mobitopp/actitopp/mopv14_withpkwhh")) : MajorDurationHistograms {
            return MajorDurationHistograms(
                histograms = (0..14).map { ArrayHistogram.fromPath(path.resolve("8C_KAT_$it.csv")) }
            )
        }
    }
}

class MainDurationSituation(override val choice: ArrayHistogram) : ChoiceSituation<ArrayHistogram>()

private val standardUtilityFunction: ParameterStep8B.(MainDurationSituation) -> Double = {
    TODO()
//    base +
//            (it.mittl_zeit_akt_60bis119min) * mittl_zeit_akt_60bis119min +
//                (it.mittl_zeit_akt_120bis179min) * mittl_zeit_akt_120bis179min +
//                (it.mittl_zeit_akt_180bis239min) * mittl_zeit_akt_180bis239min +
//                (it.mittl_zeit_akt_240bis299min) * mittl_zeit_akt_240bis299min +
//                (it.mittl_zeit_akt_300bis359min) * mittl_zeit_akt_300bis359min +
//                (it.mittl_zeit_akt_360bis419min) * mittl_zeit_akt_360bis419min +
//                (it.mittl_zeit_akt_420bis479min) * mittl_zeit_akt_420bis479min +
//                (it.tag_fr) * tag_fr +
//                (it.tag_sa) * tag_sa +
//                (it.tag_so) * tag_so +
//                (it.anzaktwieanztagemitzweck) * anzaktwieanztagemitzweck +
//                (it.aktzweck_work) * aktzweck_work +
//                (it.aktzweck_education) * aktzweck_education +
//                (it.aktzweck_shopping) * aktzweck_shopping +
//                (it.aktzweck_transport) * aktzweck_transport +
//                (it.wochenzbudget_zweck_kat1) * wochenzbudget_zweck_kat1 +
//                (it.wochenzbudget_zweck_kat2) * wochenzbudget_zweck_kat2 +
//                (it.wochenzbudget_zweck_kat3) * wochenzbudget_zweck_kat3 +
//                (it.wochenzbudget_zweck_kat4) * wochenzbudget_zweck_kat4 +
//                (it.wochenzbudget_zweck_kat5) * wochenzbudget_zweck_kat5 +
//                (it.tourhat1akt) * tourhat1akt +
//                (it.tourhat2akt) * tourhat2akt +
//                (it.tourhat3akt) * tourhat3akt +
//                (it.taghat1akt) * taghat1akt +
//                (it.taghat2akt) * taghat2akt +
//                (it.taghat3akt) * taghat3akt +
//                (it.taghat1tour) * taghat1tour +
//                (it.taghat2touren) * taghat2touren +
//                (it.beruf_vollzeit) * beruf_vollzeit +
//                (it.beruf_teilzeit) * beruf_teilzeit +
//                (it.beruf_schueler_azubi) * beruf_schueler_azubi +
//                (it.tourliegtvorhaupttour) * tourliegtvorhaupttour +
//                (it.tourliegtnachhaupttour) * tourliegtnachhaupttour



}
