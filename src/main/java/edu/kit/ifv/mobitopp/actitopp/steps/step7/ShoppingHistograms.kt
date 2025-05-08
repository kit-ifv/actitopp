package edu.kit.ifv.mobitopp.actitopp.steps.step7

import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.times
import java.nio.file.Path
import kotlin.io.path.Path

class ShoppingHistograms(
    val histogram1: ArrayHistogram,
    val histogram2: ArrayHistogram,
    val histogram3: ArrayHistogram,
    val histogram4: ArrayHistogram,
    val histogram5: ArrayHistogram,

): HistogramSelection {

    override fun select(randomNumber: Double, finalizedActivityPattern: FinalizedActivityPattern): ArrayHistogram {
        return choiceModel.select(randomNumber) { WorkChoiceSituation(it, finalizedActivityPattern) }
    }
    private val choiceModel = ModifiableDiscreteChoiceModel<ArrayHistogram, WorkChoiceSituation, ParameterCollectionStep7G>(
        AllocatedLogit.create {
            option(histogram1, parameters = {category1}) {standardUtilityFunction(this, it)}
            option(histogram2, parameters = {category2}) {standardUtilityFunction(this, it)}
            option(histogram3) {0.0}
            option(histogram4, parameters = {category4}) {standardUtilityFunction(this, it)}
            option(histogram5, parameters = {category5}) {standardUtilityFunction(this, it)}

        }
    ).initializeWithParameters(ParametersStep7G)

    companion object {
        fun fromResourcePath(path: Path = Path("src/main/resources/edu/kit/ifv/mobitopp/actitopp/mopv14_withpkwhh")): ShoppingHistograms {
            return ShoppingHistograms(
                histogram1 = ArrayHistogram.fromPath(path.resolve("7H_KAT_0.csv")),
                histogram2 = ArrayHistogram.fromPath(path.resolve("7H_KAT_1.csv")),
                histogram3 = ArrayHistogram.fromPath(path.resolve("7H_KAT_2.csv")),
                histogram4 = ArrayHistogram.fromPath(path.resolve("7H_KAT_3.csv")),
                histogram5 = ArrayHistogram.fromPath(path.resolve("7H_KAT_4.csv")),
            )
        }
    }
}
private val standardUtilityFunction: ParameterStep7G.(WorkChoiceSituation) -> Double = {
    base +
    (it.amountOfWorkActivitiesInWeek()) * anzakt_woche_w +
        (it.amountOfLeisureActivitiesInWeek()) * anzakt_woche_l +
        (it.amountOfShoppingActivitiesInWeek()) * anzakt_woche_s +
        (it.isFulltimeEmployee()) * beruf_vollzeit +
        (it.isParttimeEmployee()) * beruf_teilzeit +
        (it.isStudent()) * beruf_schueler +
        (it.amountOfDaysWithShoppingActivityIs1()) * tagemit_sakt_1 +
        (it.amountOfDaysWithShoppingActivityIs2()) * tagemit_sakt_2 +
        (it.amountOfDaysWithShoppingActivityIs3()) * tagemit_sakt_3 +
        (it.amountOfDaysWithShoppingActivityIs4()) * tagemit_sakt_4

}
private val ParametersStep7G = ParameterCollectionStep7G(
    category1 = ParameterStep7G(
        base = 0.0826,
        anzakt_woche_w = 0.0619,
        anzakt_woche_l = 0.0566,
        anzakt_woche_s = -0.5868,
        beruf_vollzeit = 0.2800,
        beruf_teilzeit = -0.0544,
        beruf_schueler = 0.5929,
        tagemit_sakt_1 = 1.5346,
        tagemit_sakt_2 = 0.5645,
        tagemit_sakt_3 = -0.0589,
        tagemit_sakt_4 = -0.1618,
    ),
    category2 = ParameterStep7G(
        base = -0.3110,
        anzakt_woche_w = 0.0342,
        anzakt_woche_l = 0.0127,
        anzakt_woche_s = -0.1860,
        beruf_vollzeit = 0.1946,
        beruf_teilzeit = 0.1101,
        beruf_schueler = 0.2652,
        tagemit_sakt_1 = 0.9219,
        tagemit_sakt_2 = 0.6691,
        tagemit_sakt_3 = 0.2181,
        tagemit_sakt_4 = 0.1537,
    ),
    category4 = ParameterStep7G(
        base = -0.5612,
        anzakt_woche_w = -0.0342,
        anzakt_woche_l = -0.0107,
        anzakt_woche_s = 0.0769,
        beruf_vollzeit = -0.2313,
        beruf_teilzeit = -0.1265,
        beruf_schueler = -0.3019,
        tagemit_sakt_1 = -0.8605,
        tagemit_sakt_2 = -0.4907,
        tagemit_sakt_3 = -0.4661,
        tagemit_sakt_4 = -0.1199,
    ),
    category5 = ParameterStep7G(
        base = -0.3159,
        anzakt_woche_w = -0.0597,
        anzakt_woche_l = -0.0474,
        anzakt_woche_s = 0.1253,
        beruf_vollzeit = -0.4267,
        beruf_teilzeit = -0.3545,
        beruf_schueler = -0.7126,
        tagemit_sakt_1 = -1.2950,
        tagemit_sakt_2 = -1.2527,
        tagemit_sakt_3 = -0.9871,
        tagemit_sakt_4 = -0.5124,
    )
)

private data class ParameterCollectionStep7G(
    val category1: ParameterStep7G,
    val category2: ParameterStep7G,
    val category4: ParameterStep7G,
    val category5: ParameterStep7G,

)

private data class ParameterStep7G(
    val base: Double,
    val anzakt_woche_w: Double,
    val anzakt_woche_l: Double,
    val anzakt_woche_s: Double,
    val beruf_vollzeit: Double,
    val beruf_teilzeit: Double,
    val beruf_schueler: Double,
    val tagemit_sakt_1: Double,
    val tagemit_sakt_2: Double,
    val tagemit_sakt_3: Double,
    val tagemit_sakt_4: Double,

)