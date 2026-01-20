package edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes


import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.modernization.durations.MobilityPlanInputs
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.MainDurationAlternative
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.ArrayHistogram
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.choicemodels.FIRST_TOUR_HISTOGRAM
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.structure.loadFromList
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit
import kotlin.random.Random

interface PersonPreferredTourStart {
    context(rng: Random)
    fun determinePreferredTourStart(input: MobilityPlanInputs): ArrayHistogram
}

private val standardUtilityFunction9A: ParameterStep9A.(MainDurationAlternative) -> Double = {
    base +
            (it.isStudent()) * beruf_schueler +
            (it.householdHasYouths()) * haushalthatkinderunter18 +
            (it.isAged10to17()) * alter_10bis17 +
            (it.anztagemit_tourenvorht()) * anztagemit_tourenvorht +
            (it.anztagemit_tourennachht()) * anztagemit_tourennachht

}

val ParametersStep9A = ParameterCollectionStep9A.create(
    first = ParameterStep9A(
        base = .0,
        beruf_schueler = .0,
        haushalthatkinderunter18 = .0,
        alter_10bis17 = .0,
        anztagemit_tourenvorht = .0,
        anztagemit_tourennachht = .0,
    ),
    second = ParameterStep9A(
        base = 1.4166,
        beruf_schueler = 8.1463,
        haushalthatkinderunter18 = -1.2139,
        alter_10bis17 = 9.3784,
        anztagemit_tourenvorht = 0.2392,
        anztagemit_tourennachht = -0.0224,
    ),
    third = ParameterStep9A(
        base = 4.4923,
        beruf_schueler = 8.0775,
        haushalthatkinderunter18 = -0.2609,
        alter_10bis17 = 7.6829,
        anztagemit_tourenvorht = 0.3725,
        anztagemit_tourennachht = -0.1154,
    ),
    fourth = ParameterStep9A(
        base = 5.3296,
        beruf_schueler = 9.6610,
        haushalthatkinderunter18 = -0.1922,
        alter_10bis17 = 8.5941,
        anztagemit_tourenvorht = 0.2168,
        anztagemit_tourennachht = -0.1284,
    ),
    fifth = ParameterStep9A(
        base = 5.3757,
        beruf_schueler = 10.5099,
        haushalthatkinderunter18 = 0.0574,
        alter_10bis17 = 8.4106,
        anztagemit_tourenvorht = 0.3245,
        anztagemit_tourennachht = -0.0703,
    ),
    sixth = ParameterStep9A(
        base = 4.7193,
        beruf_schueler = 10.2114,
        haushalthatkinderunter18 = -0.1389,
        alter_10bis17 = 6.3233,
        anztagemit_tourenvorht = 0.6209,
        anztagemit_tourennachht = -0.2453,
    ),
    seventh = ParameterStep9A(
        base = 3.5305,
        beruf_schueler = 10.8371,
        haushalthatkinderunter18 = -0.7788,
        alter_10bis17 = 6.3454,
        anztagemit_tourenvorht = 0.8167,
        anztagemit_tourennachht = -0.3189,
    ),
    eighth = ParameterStep9A(
        base = 2.7473,
        beruf_schueler = 10.9124,
        haushalthatkinderunter18 = -0.7444,
        alter_10bis17 = 5.1724,
        anztagemit_tourenvorht = 0.7644,
        anztagemit_tourennachht = -0.4134,
    ),
    ninth = ParameterStep9A(
        base = 3.2830,
        beruf_schueler = 11.0790,
        haushalthatkinderunter18 = -0.8914,
        alter_10bis17 = 5.7114,
        anztagemit_tourenvorht = 0.8731,
        anztagemit_tourennachht = -0.6653,
    ),
    tenth = ParameterStep9A(
        base = 3.0706,
        beruf_schueler = 10.7897,
        haushalthatkinderunter18 = -0.4475,
        alter_10bis17 = 5.4885,
        anztagemit_tourenvorht = 0.9888,
        anztagemit_tourennachht = -1.0677,
    ),
    eleventh = ParameterStep9A(
        base = 1.3601,
        beruf_schueler = 10.8499,
        haushalthatkinderunter18 = -1.1668,
        alter_10bis17 = 7.5280,
        anztagemit_tourenvorht = 1.0194,
        anztagemit_tourennachht = -0.7874,
    ),
    twelfth = ParameterStep9A(
        base = 1.5348,
        beruf_schueler = 10.5742,
        haushalthatkinderunter18 = -0.2338,
        alter_10bis17 = 7.0052,
        anztagemit_tourenvorht = 0.9637,
        anztagemit_tourennachht = -1.0404,
    ),
    thirteenth = ParameterStep9A(
        base = 1.2217,
        beruf_schueler = 11.1086,
        haushalthatkinderunter18 = -0.6639,
        alter_10bis17 = -2.5290,
        anztagemit_tourenvorht = 0.8631,
        anztagemit_tourennachht = -1.0864,
    ),
    fourteenth = ParameterStep9A(
        base = 1.2836,
        beruf_schueler = 9.9601,
        haushalthatkinderunter18 = -0.1409,
        alter_10bis17 = -1.7263,
        anztagemit_tourenvorht = 0.9099,
        anztagemit_tourennachht = -1.3289,
    ),
    fifteenth = ParameterStep9A(
        base = 2.0529,
        beruf_schueler = 1.2977,
        haushalthatkinderunter18 = -0.8069,
        alter_10bis17 = 2.2583,
        anztagemit_tourenvorht = 1.2407,
        anztagemit_tourennachht = -1.8499,
    ),
    sixteenth = ParameterStep9A(
        base = 0.6064,
        beruf_schueler = 1.5595,
        haushalthatkinderunter18 = -1.3348,
        alter_10bis17 = 3.3018,
        anztagemit_tourenvorht = 1.2503,
        anztagemit_tourennachht = -9.0290,
    )
)

data class ParameterCollectionStep9A(
    val parameters: List<ParameterStep9A>,
) : List<ParameterStep9A> by parameters {


    companion object {
        fun create(
            first: ParameterStep9A,
            second: ParameterStep9A,
            third: ParameterStep9A,
            fourth: ParameterStep9A,
            fifth: ParameterStep9A,
            sixth: ParameterStep9A,
            seventh: ParameterStep9A,
            eighth: ParameterStep9A,
            ninth: ParameterStep9A,
            tenth: ParameterStep9A,
            eleventh: ParameterStep9A,
            twelfth: ParameterStep9A,
            thirteenth: ParameterStep9A,
            fourteenth: ParameterStep9A,
            fifteenth: ParameterStep9A,
            sixteenth: ParameterStep9A,
        ): ParameterCollectionStep9A {
            return ParameterCollectionStep9A(
                listOf(
                    first,
                    second,
                    third,
                    fourth,
                    fifth,
                    sixth,
                    seventh,
                    eighth,
                    ninth,
                    tenth,
                    eleventh,
                    twelfth,
                    thirteenth,
                    fourteenth,
                    fifteenth,
                    sixteenth,
                )
            )
        }
    }
}

data class ParameterStep9A(
    val base: Double,
    val beruf_schueler: Double,
    val haushalthatkinderunter18: Double,
    val alter_10bis17: Double,
    val anztagemit_tourenvorht: Double,
    val anztagemit_tourennachht: Double,

    )


class StandardPreferredTourStart() : PersonPreferredTourStart {
    private val choiceModel =
        DiscreteStructure<ArrayHistogram, MainDurationAlternative, ParameterCollectionStep9A> {

            loadFromList(
                FIRST_TOUR_HISTOGRAM.histograms
            ) { _, it ->
                standardUtilityFunction9A(this, it)
            }

        }.multinomialLogit("Determine preferred histogram for the first tour of the day").build(ParametersStep9A)
    context(rng: Random)
    override fun determinePreferredTourStart(input: MobilityPlanInputs): ArrayHistogram {

        return context(MainDurationAlternative(input), rng) {
            choiceModel.select()
        }

    }
}
