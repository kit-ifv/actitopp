package edu.kit.ifv.mobitopp.actitopp.steps.step7

import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.times
import java.nio.file.Path
import kotlin.io.path.Path

class TransportHistograms(
    val histogram1: ArrayHistogram,
    val histogram2: ArrayHistogram,
    val histogram3: ArrayHistogram,
    val histogram4: ArrayHistogram,
) : HistogramSelection {

    override fun select(randomNumber: Double, finalizedActivityPattern: FinalizedActivityPattern): ArrayHistogram {
        return choiceModel.select(randomNumber) { WorkChoiceSituation(it, finalizedActivityPattern) }
    }

    private val choiceModel =
        ModifiableDiscreteChoiceModel<ArrayHistogram, WorkChoiceSituation, ParameterCollectionStep7I>(
            AllocatedLogit.create {
                option(histogram1) { 0.0 }
                option(histogram2, parameters = { category2 }) { standardUtilityFunction(this, it) }
                option(histogram3, parameters = { category3 }) { standardUtilityFunction(this, it) }
                option(histogram4, parameters = { category4 }) { standardUtilityFunction(this, it) }

            }
        ).initializeWithParameters(ParametersStep7I)

    companion object {
        fun fromResourcePath(path: Path = Path("src/main/resources/edu/kit/ifv/mobitopp/actitopp/mopv14_withpkwhh")): TransportHistograms {
            return TransportHistograms(
                histogram1 = ArrayHistogram.fromPath(path.resolve("7J_KAT_0.csv")),
                histogram2 = ArrayHistogram.fromPath(path.resolve("7J_KAT_1.csv")),
                histogram3 = ArrayHistogram.fromPath(path.resolve("7J_KAT_2.csv")),
                histogram4 = ArrayHistogram.fromPath(path.resolve("7J_KAT_3.csv")),
            )
        }
    }
}


private val standardUtilityFunction: ParameterStep7I.(WorkChoiceSituation) -> Double = {
    base +
            (it.amountOfWorkActivitiesInWeek()) * anzakt_woche_w +
            (it.amountOfShoppingActivitiesInWeek()) * anzakt_woche_s +
            (it.amountOfTransportActivitiesInWeek()) * anzakt_woche_t +
            (it.isFulltimeEmployee()) * beruf_vollzeit +
            (it.isParttimeEmployee()) * beruf_teilzeit +
            (it.isStudent()) * beruf_schueler +
            (it.amountOfDaysWithTransportActivityIs1()) * tagemit_takt_1

}
private val ParametersStep7I = ParameterCollectionStep7I(
    category2 = ParameterStep7I(
        base = -1.2365,
        anzakt_woche_w = -0.0387,
        anzakt_woche_s = -0.0486,
        anzakt_woche_t = 0.2417,
        beruf_vollzeit = -0.2497,
        beruf_teilzeit = -0.1264,
        beruf_schueler = -0.7904,
        tagemit_takt_1 = -0.4481,
    ),
    category3 = ParameterStep7I(
        base = -1.7208,
        anzakt_woche_w = -0.0611,
        anzakt_woche_s = -0.0505,
        anzakt_woche_t = 0.3177,
        beruf_vollzeit = -0.1934,
        beruf_teilzeit = -0.0707,
        beruf_schueler = -0.5535,
        tagemit_takt_1 = -0.3096,
    ),
    category4 = ParameterStep7I(
        base = -1.6474,
        anzakt_woche_w = -0.0570,
        anzakt_woche_s = -0.1095,
        anzakt_woche_t = 0.3642,
        beruf_vollzeit = -0.6205,
        beruf_teilzeit = -0.5268,
        beruf_schueler = -0.5862,
        tagemit_takt_1 = -0.8365,
    )
)

private data class ParameterCollectionStep7I(
    val category2: ParameterStep7I,
    val category3: ParameterStep7I,
    val category4: ParameterStep7I,


    )

private data class ParameterStep7I(
    val base: Double,
    val anzakt_woche_w: Double,
    val anzakt_woche_s: Double,
    val anzakt_woche_t: Double,
    val beruf_vollzeit: Double,
    val beruf_teilzeit: Double,
    val beruf_schueler: Double,
    val tagemit_takt_1: Double,


    )