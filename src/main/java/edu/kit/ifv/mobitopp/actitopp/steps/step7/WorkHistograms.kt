package edu.kit.ifv.mobitopp.actitopp.steps.step7

import edu.kit.ifv.mobitopp.actitopp.steps.PersonAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.PersonAttributesFromElement
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.times
import java.nio.file.Path
import kotlin.io.path.Path

class WorkHistograms(
    val histogram1: ArrayHistogram,
    val histogram2: ArrayHistogram,
    val histogram3: ArrayHistogram,
    val histogram4: ArrayHistogram,
    val histogram5: ArrayHistogram,
    val histogram6: ArrayHistogram,
    val histogram7: ArrayHistogram,
    val histogram8: ArrayHistogram,
    val histogram9: ArrayHistogram,
    
): HistogramSelection {

    override fun select(randomNumber: Double, finalizedActivityPattern: FinalizedActivityPattern): ArrayHistogram {
        return choiceModel.select(randomNumber) { WorkChoiceSituation(it, finalizedActivityPattern) }
    }

    private val choiceModel = ModifiableDiscreteChoiceModel<ArrayHistogram, WorkChoiceSituation, ParameterCollectionStep7A>(
        AllocatedLogit.create { 
            option(histogram1, parameters = {category1}) {standardUtilityFunction(this, it)}
            option(histogram2, parameters = {category2}) {standardUtilityFunction(this, it)}
            option(histogram3, parameters = {category3}) {standardUtilityFunction(this, it)}
            option(histogram4, parameters = {category4}) {standardUtilityFunction(this, it)}
            option(histogram5, parameters = {category5}) {standardUtilityFunction(this, it)}
            option(histogram6, parameters = {category6}) {standardUtilityFunction(this, it)}
            option(histogram7) {0.0}
            option(histogram8, parameters = {category8}) {standardUtilityFunction(this, it)}
            option(histogram9, parameters = {category9}) {standardUtilityFunction(this, it)}

        }
    ).initializeWithParameters(ParametersStep7A)

    companion object {
        fun fromResourcePath(path: Path = Path("src/main/resources/edu/kit/ifv/mobitopp/actitopp/mopv14_withpkwhh")): WorkHistograms {
            return WorkHistograms(
                histogram1 = ArrayHistogram.fromPath(path.resolve("7B_KAT_0.csv")),
                histogram2 = ArrayHistogram.fromPath(path.resolve("7B_KAT_1.csv")),
                histogram3 = ArrayHistogram.fromPath(path.resolve("7B_KAT_2.csv")),
                histogram4 = ArrayHistogram.fromPath(path.resolve("7B_KAT_3.csv")),
                histogram5 = ArrayHistogram.fromPath(path.resolve("7B_KAT_4.csv")),
                histogram6 = ArrayHistogram.fromPath(path.resolve("7B_KAT_5.csv")),
                histogram7 = ArrayHistogram.fromPath(path.resolve("7B_KAT_6.csv")),
                histogram8 = ArrayHistogram.fromPath(path.resolve("7B_KAT_7.csv")),
                histogram9 = ArrayHistogram.fromPath(path.resolve("7B_KAT_8.csv"))
            )
        }
    }
}
private val standardUtilityFunction: ParameterStep7A.(WorkChoiceSituation) -> Double = {
    base+
            (it.amountOfWorkActivitiesInWeek()) * anzakt_woche_w+
            (it.amountOfEducationActivitiesInWeek()) * anzakt_woche_e+
            (it.isFulltimeEmployee()) * beruf_vollzeit+
            (it.isParttimeEmployee()) * beruf_teilzeit+
            (it.isStudent()) * beruf_schueler+
            (it.isVocational()) * beruf_azubi+
            (it.isMale()) * male+
            (it.isAged18To25()) * alter_18bis25+
            (it.isAged26To35()) * alter_26bis35+
            (it.isAged36To50()) * alter_36bis50+
            (it.isAged51To60()) * alter_51bis60+
            (it.amountOfDaysWithWorkActivityIs1()) * tagemit_wakt_1+
            (it.amountOfDaysWithWorkActivityIs2()) * tagemit_wakt_2+
            (it.amountOfDaysWithWorkActivityIs3()) * tagemit_wakt_3+
            (it.amountOfDaysWithWorkActivityIs4()) * tagemit_wakt_4+
            (it.amountOfDaysWithWorkActivityIs5()) * tagemit_wakt_5+
            (it.amountOfDaysWithWorkActivityIs6()) * tagemit_wakt_6
}
private val ParametersStep7A = ParameterCollectionStep7A(
    category1 = ParameterStep7A(
        base = 1.6917,
        anzakt_woche_w = 0.0280,
        anzakt_woche_e = 0.5101,
        beruf_vollzeit = -4.1322,
        beruf_teilzeit = -1.0048,
        beruf_schueler = -1.7583,
        beruf_azubi = -5.1228,
        male = -0.1487,
        alter_18bis25 = -2.2042,
        alter_26bis35 = -2.7129,
        alter_36bis50 = -2.2585,
        alter_51bis60 = -1.8666,
        tagemit_wakt_1 = 20.0279,
        tagemit_wakt_2 = 16.5940,
        tagemit_wakt_3 = 5.9771,
        tagemit_wakt_4 = 1.7452,
        tagemit_wakt_5 = 0.3102,
        tagemit_wakt_6 = 1.0982,
    ),
    category2 = ParameterStep7A(
        base = -0.3145,
        anzakt_woche_w = 0.0574,
        anzakt_woche_e = 0.4112,
        beruf_vollzeit = -3.4684,
        beruf_teilzeit = -0.1724,
        beruf_schueler = -1.4337,
        beruf_azubi = -4.0171,
        male = -0.2486,
        alter_18bis25 = -1.8277,
        alter_26bis35 = -1.8315,
        alter_36bis50 = -1.7146,
        alter_51bis60 = -1.4355,
        tagemit_wakt_1 = 17.7290,
        tagemit_wakt_2 = 16.9682,
        tagemit_wakt_3 = 6.9188,
        tagemit_wakt_4 = 2.9442,
        tagemit_wakt_5 = 1.3567,
        tagemit_wakt_6 = 1.6344,
    ),
    category3 = ParameterStep7A(
        base = 1.4785,
        anzakt_woche_w = 0.0212,
        anzakt_woche_e = 0.2866,
        beruf_vollzeit = -2.3744,
        beruf_teilzeit = 0.3210,
        beruf_schueler = -0.5554,
        beruf_azubi = -2.4069,
        male = -0.4255,
        alter_18bis25 = -1.5836,
        alter_26bis35 = -1.5112,
        alter_36bis50 = -1.5544,
        alter_51bis60 = -1.4254,
        tagemit_wakt_1 = 12.7148,
        tagemit_wakt_2 = 14.3076,
        tagemit_wakt_3 = 4.6474,
        tagemit_wakt_4 = 1.2165,
        tagemit_wakt_5 = -0.0940,
        tagemit_wakt_6 = -0.6928,
    ),
    category4 = ParameterStep7A(
        base = -0.3925,
        anzakt_woche_w = 0.0189,
        anzakt_woche_e = 0.2611,
        beruf_vollzeit = -1.9970,
        beruf_teilzeit = 0.5013,
        beruf_schueler = -0.0131,
        beruf_azubi = -2.1086,
        male = -0.1425,
        alter_18bis25 = -0.9291,
        alter_26bis35 = -0.9172,
        alter_36bis50 = -0.9391,
        alter_51bis60 = -0.5696,
        tagemit_wakt_1 = 14.2895,
        tagemit_wakt_2 = 13.3451,
        tagemit_wakt_3 = 5.5677,
        tagemit_wakt_4 = 2.5104,
        tagemit_wakt_5 = 1.8641,
        tagemit_wakt_6 = 1.5049,
    ),
    category5 = ParameterStep7A(
        base = 1.5406,
        anzakt_woche_w = -0.00773,
        anzakt_woche_e = 0.1705,
        beruf_vollzeit = -1.1243,
        beruf_teilzeit = 0.3681,
        beruf_schueler = -0.2072,
        beruf_azubi = -1.8661,
        male = -0.2656,
        alter_18bis25 = -1.0425,
        alter_26bis35 = -0.6534,
        alter_36bis50 = -0.7849,
        alter_51bis60 = -0.6941,
        tagemit_wakt_1 = 11.6515,
        tagemit_wakt_2 = 9.1610,
        tagemit_wakt_3 = 3.5086,
        tagemit_wakt_4 = 0.3374,
        tagemit_wakt_5 = 0.1542,
        tagemit_wakt_6 = 0.0584,
    ),
    category6 = ParameterStep7A(
        base = 1.3177,
        anzakt_woche_w = -0.0169,
        anzakt_woche_e = -0.0178,
        beruf_vollzeit = -0.8526,
        beruf_teilzeit = 0.0577,
        beruf_schueler = -0.1949,
        beruf_azubi = -0.9263,
        male = -0.1242,
        alter_18bis25 = -0.5091,
        alter_26bis35 = -0.1742,
        alter_36bis50 = -0.1869,
        alter_51bis60 = 0.0135,
        tagemit_wakt_1 = -0.0674,
        tagemit_wakt_2 = 9.0156,
        tagemit_wakt_3 = 1.3145,
        tagemit_wakt_4 = 0.3853,
        tagemit_wakt_5 = 0.1320,
        tagemit_wakt_6 = -0.0217,
    ),
    category8 = ParameterStep7A(
        base = 1.3474,
        anzakt_woche_w = -0.0695,
        anzakt_woche_e = 0.1176,
        beruf_vollzeit = 0.3177,
        beruf_teilzeit = -0.7687,
        beruf_schueler = -0.4789,
        beruf_azubi = -0.8894,
        male = 0.1727,
        alter_18bis25 = 0.7620,
        alter_26bis35 = 0.2809,
        alter_36bis50 = 0.0511,
        alter_51bis60 = 0.1023,
        tagemit_wakt_1 = 9.1882,
        tagemit_wakt_2 = 7.1875,
        tagemit_wakt_3 = -2.2518,
        tagemit_wakt_4 = -1.6536,
        tagemit_wakt_5 = 0.4347,
        tagemit_wakt_6 = 0.3006,
    ),
    category9 = ParameterStep7A(
        base = 3.7034,
        anzakt_woche_w = -0.1441,
        anzakt_woche_e = 0.0589,
        beruf_vollzeit = 0.4104,
        beruf_teilzeit = -1.2180,
        beruf_schueler = -1.0637,
        beruf_azubi = -1.1621,
        male = 0.5458,
        alter_18bis25 = 1.2577,
        alter_26bis35 = 0.5091,
        alter_36bis50 = 0.3553,
        alter_51bis60 = 0.2784,
        tagemit_wakt_1 = 7.2528,
        tagemit_wakt_2 = 3.6982,
        tagemit_wakt_3 = -3.8389,
        tagemit_wakt_4 = -5.1337,
        tagemit_wakt_5 = -2.4324,
        tagemit_wakt_6 = -0.9668,
    )
)

/**
 * Note that there is no category7 parameter set, because 7 is the category assumed to be the default.
 */
private data class ParameterCollectionStep7A(
    val category1: ParameterStep7A,
    val category2: ParameterStep7A,
    val category3: ParameterStep7A,
    val category4: ParameterStep7A,
    val category5: ParameterStep7A,
    val category6: ParameterStep7A,
    //val category7: ParameterStep7A,
    val category8: ParameterStep7A,
    val category9: ParameterStep7A,

    )

private data class ParameterStep7A(
    val base: Double,
    val anzakt_woche_w: Double,
    val anzakt_woche_e: Double,
    val beruf_vollzeit: Double,
    val beruf_teilzeit: Double,
    val beruf_schueler: Double,
    val beruf_azubi: Double,
    val male: Double,
    val alter_18bis25: Double,
    val alter_26bis35: Double,
    val alter_36bis50: Double,
    val alter_51bis60: Double,
    val tagemit_wakt_1: Double,
    val tagemit_wakt_2: Double,
    val tagemit_wakt_3: Double,
    val tagemit_wakt_4: Double,
    val tagemit_wakt_5: Double,
    val tagemit_wakt_6: Double,

    )


class WorkChoiceSituation private constructor(override val choice: ArrayHistogram, val personAttributes: PersonAttributes, val patternAttributes: FinalizedPatternAttributes) :
    ChoiceSituation<ArrayHistogram>(), PersonAttributes by personAttributes, FinalizedPatternAttributes by patternAttributes {
        constructor(choice: ArrayHistogram, finalizedPattern: FinalizedActivityPattern): this(choice, PersonAttributesFromElement(finalizedPattern.person), PatternAttributesByElement(finalizedPattern))

}

fun main() {
    val workHistogram = WorkHistograms.fromResourcePath()
    println(workHistogram)
}