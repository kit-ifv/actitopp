package edu.kit.ifv.mobitopp.actitopp.steps.step7

import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.times
import java.nio.file.Path
import kotlin.io.path.Path

class LeisureHistograms(
    val histogram1: ArrayHistogram,
    val histogram2: ArrayHistogram,
    val histogram3: ArrayHistogram,
    val histogram4: ArrayHistogram,
    val histogram5: ArrayHistogram,
    val histogram6: ArrayHistogram,
): HistogramSelection{

    override fun select(randomNumber: Double, finalizedActivityPattern: FinalizedActivityPattern): ArrayHistogram {
        return choiceModel.select(randomNumber) { WorkChoiceSituation(it, finalizedActivityPattern) }
    }


    private val choiceModel = ModifiableDiscreteChoiceModel<ArrayHistogram, WorkChoiceSituation, ParameterCollectionStep7E>(
        AllocatedLogit.create {
            option(histogram1, parameters = {category1}) {standardUtilityFunction(this, it)}
            option(histogram2, parameters = {category2}) {standardUtilityFunction(this, it)}
            option(histogram3, parameters = {category3}) {standardUtilityFunction(this, it)}
            option(histogram4) {0.0}
            option(histogram5, parameters = {category5}) {standardUtilityFunction(this, it)}
            option(histogram6, parameters = {category6}) {standardUtilityFunction(this, it)}

        }
    ).initializeWithParameters(ParametersStep7E)

    companion object {
        fun fromResourcePath(path: Path = Path("src/main/resources/edu/kit/ifv/mobitopp/actitopp/mopv14_withpkwhh")): LeisureHistograms {
            return LeisureHistograms(
                histogram1 = ArrayHistogram.fromPath(path.resolve("7F_KAT_0.csv")),
                histogram2 = ArrayHistogram.fromPath(path.resolve("7F_KAT_1.csv")),
                histogram3 = ArrayHistogram.fromPath(path.resolve("7F_KAT_2.csv")),
                histogram4 = ArrayHistogram.fromPath(path.resolve("7F_KAT_3.csv")),
                histogram5 = ArrayHistogram.fromPath(path.resolve("7F_KAT_4.csv")),
                histogram6 = ArrayHistogram.fromPath(path.resolve("7F_KAT_5.csv"))
            )
        }
    }

}
private val standardUtilityFunction: ParameterStep7E.(WorkChoiceSituation) -> Double = {
    base +
    (it.amountOfWorkActivitiesInWeek()) * anzakt_woche_w +
        (it.amountOfLeisureActivitiesInWeek()) * anzakt_woche_l +
        (it.amountOfShoppingActivitiesInWeek()) * anzakt_woche_s +
        (it.commuteIn0To5km()) * pendeln_0bis5km +
        (it.isRetired()) * beruf_rentner +
        (it.amountOfDaysWithLeisureActivityIs1()) * tagemit_lakt_1 +
        (it.amountOfDaysWithLeisureActivityIs2()) * tagemit_lakt_2 +
        (it.amountOfDaysWithLeisureActivityIs3()) * tagemit_lakt_3 +
        (it.amountOfDaysWithLeisureActivityIs4()) * tagemit_lakt_4 +
        (it.amountOfDaysWithLeisureActivityIs5()) * tagemit_lakt_5
}
private val ParametersStep7E = ParameterCollectionStep7E(
    category1 = ParameterStep7E(
        base = 1.0576,
        anzakt_woche_w = 0.0143,
        anzakt_woche_l = -0.7111,
        anzakt_woche_s = 0.1308,
        pendeln_0bis5km = -0.4133,
        beruf_rentner = 0.1009,
        tagemit_lakt_1 = 3.2007,
        tagemit_lakt_2 = 1.0523,
        tagemit_lakt_3 = -0.2179,
        tagemit_lakt_4 = -0.9268,
        tagemit_lakt_5 = -1.0579,
    ),
    category2 = ParameterStep7E(
        base = 0.1193,
        anzakt_woche_w = 0.0122,
        anzakt_woche_l = -0.3675,
        anzakt_woche_s = 0.0821,
        pendeln_0bis5km = -0.2179,
        beruf_rentner = 0.0133,
        tagemit_lakt_1 = 3.0153,
        tagemit_lakt_2 = 1.9184,
        tagemit_lakt_3 = 1.0253,
        tagemit_lakt_4 = 0.2947,
        tagemit_lakt_5 = -0.2229,
    ),
    category3 = ParameterStep7E(
        base = -0.1239,
        anzakt_woche_w = 0.0129,
        anzakt_woche_l = -0.1611,
        anzakt_woche_s = 0.0409,
        pendeln_0bis5km = -0.2390,
        beruf_rentner = 0.0311,
        tagemit_lakt_1 = 1.7189,
        tagemit_lakt_2 = 1.3350,
        tagemit_lakt_3 = 0.8949,
        tagemit_lakt_4 = 0.5184,
        tagemit_lakt_5 = 0.0611,
    ),
    category5 = ParameterStep7E(
        base = -1.5743,
        anzakt_woche_w = -0.0263,
        anzakt_woche_l = 0.1213,
        anzakt_woche_s = -0.0629,
        pendeln_0bis5km = -0.0422,
        beruf_rentner = 0.0500,
        tagemit_lakt_1 = 0.6724,
        tagemit_lakt_2 = -1.0757,
        tagemit_lakt_3 = -1.0992,
        tagemit_lakt_4 = -0.7921,
        tagemit_lakt_5 = -0.2934,
    ),
    category6 = ParameterStep7E(
        base = -3.1398,
        anzakt_woche_w = -0.1197,
        anzakt_woche_l = 0.2145,
        anzakt_woche_s = -0.0863,
        pendeln_0bis5km = -0.2943,
        beruf_rentner = -0.3829,
        tagemit_lakt_1 = 2.9839,
        tagemit_lakt_2 = 0.7981,
        tagemit_lakt_3 = -0.2040,
        tagemit_lakt_4 = -0.3433,
        tagemit_lakt_5 = -0.5545,
    )
)

private data class ParameterCollectionStep7E(
    val category1: ParameterStep7E,
    val category2: ParameterStep7E,
    val category3: ParameterStep7E,
    val category5: ParameterStep7E,
    val category6: ParameterStep7E,
)

private data class ParameterStep7E(
    val base: Double,
    val anzakt_woche_w: Double,
    val anzakt_woche_l: Double,
    val anzakt_woche_s: Double,
    val pendeln_0bis5km: Double,
    val beruf_rentner: Double,
    val tagemit_lakt_1: Double,
    val tagemit_lakt_2: Double,
    val tagemit_lakt_3: Double,
    val tagemit_lakt_4: Double,
    val tagemit_lakt_5: Double,

)