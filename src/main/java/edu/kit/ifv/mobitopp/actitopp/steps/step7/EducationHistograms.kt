package edu.kit.ifv.mobitopp.actitopp.steps.step7

import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.times
import java.nio.file.Path
import kotlin.io.path.Path

class EducationHistograms (
    val histogram1: ArrayHistogram,
    val histogram2: ArrayHistogram,
    val histogram3: ArrayHistogram,
    val histogram4: ArrayHistogram,
    val histogram5: ArrayHistogram,
    val histogram6: ArrayHistogram,
) : HistogramSelection {

    override fun select(randomNumber: Double, finalizedActivityPattern: FinalizedActivityPattern): ArrayHistogram {
        return choiceModel.select(randomNumber) { WorkChoiceSituation(it, finalizedActivityPattern) }
    }
    private val choiceModel = ModifiableDiscreteChoiceModel<ArrayHistogram, WorkChoiceSituation, ParameterCollectionStep7C>(AllocatedLogit.create {
        option(histogram1, parameters = {category1}) {standardUtilityFunction(this, it)}
        option(histogram2, parameters = {category2}) {standardUtilityFunction(this, it)}
        option(histogram3, parameters = {category3}) {standardUtilityFunction(this, it)}
        option(histogram5, parameters = {category5}) {standardUtilityFunction(this, it)}
        option(histogram6, parameters = {category6}) {standardUtilityFunction(this, it)}
    }).initializeWithParameters(ParametersStep7C)


    companion object {
        fun fromResourcePath(path: Path = Path("src/main/resources/edu/kit/ifv/mobitopp/actitopp/mopv14_withpkwhh")): EducationHistograms {
            return EducationHistograms(
                histogram1 = ArrayHistogram.fromPath(path.resolve("7D_KAT_0.csv")),
                histogram2 = ArrayHistogram.fromPath(path.resolve("7D_KAT_1.csv")),
                histogram3 = ArrayHistogram.fromPath(path.resolve("7D_KAT_2.csv")),
                histogram4 = ArrayHistogram.fromPath(path.resolve("7D_KAT_3.csv")),
                histogram5 = ArrayHistogram.fromPath(path.resolve("7D_KAT_4.csv")),
                histogram6 = ArrayHistogram.fromPath(path.resolve("7D_KAT_5.csv"))
            )
        }
    }
}
private val ParametersStep7C = ParameterCollectionStep7C(
    category1 = ParameterStep7C(
        base = 7.3308,
        anzakt_woche_w = 0.2824,
        anzakt_woche_e = -1.1222,
        alter_10bis17 = -3.0926,
        beruf_azubi = -1.1582,
        tagemit_eakt_2 = 6.7131,
        tagemit_eakt_3 = -0.1972,
        tagemit_eakt_4 = -4.7670,
        tagemit_eakt_5 = -5.3585,
    ),
    category2 = ParameterStep7C(
        base = 1.2732,
        anzakt_woche_w = 0.2963,
        anzakt_woche_e = -0.2518,
        alter_10bis17 = -1.2461,
        beruf_azubi = -0.0487,
        tagemit_eakt_2 = 10.0972,
        tagemit_eakt_3 = 3.9357,
        tagemit_eakt_4 = 0.2901,
        tagemit_eakt_5 = -2.6143,
    ),
    category3 = ParameterStep7C(
        base = -0.6735,
        anzakt_woche_w = 0.1956,
        anzakt_woche_e = -0.0807,
        alter_10bis17 = -0.4415,
        beruf_azubi = -0.0811,
        tagemit_eakt_2 = -0.9664,
        tagemit_eakt_3 = 3.9599,
        tagemit_eakt_4 = 1.7891,
        tagemit_eakt_5 = -0.1693,
    ),
    category5 = ParameterStep7C(
        base = -0.1449,
        anzakt_woche_w = 0.00873,
        anzakt_woche_e = 0.1256,
        alter_10bis17 = -0.0963,
        beruf_azubi = 1.2008,
        tagemit_eakt_2 = -1.5017,
        tagemit_eakt_3 = -1.1565,
        tagemit_eakt_4 = -1.6420,
        tagemit_eakt_5 = -0.7661,
    ),
    category6 = ParameterStep7C(
        base = 1.7090,
        anzakt_woche_w = -0.4791,
        anzakt_woche_e = 0.0202,
        alter_10bis17 = -0.7520,
        beruf_azubi = 2.7480,
        tagemit_eakt_2 = 4.4675,
        tagemit_eakt_3 = -14.3348,
        tagemit_eakt_4 = -3.9791,
        tagemit_eakt_5 = -2.2662,
    )
)
private val standardUtilityFunction: ParameterStep7C.(WorkChoiceSituation) -> Double = {
    base+
            (it.amountOfWorkActivitiesInWeek()) * anzakt_woche_w+
                (it.amountOfEducationActivitiesInWeek()) * anzakt_woche_e+
                (it.isAged10To17()) * alter_10bis17+
                (it.isVocational()) * beruf_azubi+
                (it.amountOfDaysWithEducationActivityIs2()) * tagemit_eakt_2+
                (it.amountOfDaysWithEducationActivityIs3()) * tagemit_eakt_3+
                (it.amountOfDaysWithEducationActivityIs4()) * tagemit_eakt_4+
                (it.amountOfDaysWithEducationActivityIs5()) * tagemit_eakt_5

}

private data class ParameterCollectionStep7C (
    val category1: ParameterStep7C,
    val category2: ParameterStep7C,
    val category3: ParameterStep7C,
    val category5: ParameterStep7C,
    val category6: ParameterStep7C,
    )

private data class ParameterStep7C(
    val base: Double,
    val anzakt_woche_w: Double,
    val anzakt_woche_e: Double,
    val alter_10bis17: Double,
    val beruf_azubi: Double,
    val tagemit_eakt_2: Double,
    val tagemit_eakt_3: Double,
    val tagemit_eakt_4: Double,
    val tagemit_eakt_5: Double,

)