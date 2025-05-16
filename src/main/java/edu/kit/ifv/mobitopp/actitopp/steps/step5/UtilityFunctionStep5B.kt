package edu.kit.ifv.mobitopp.actitopp.steps.step5

import edu.kit.ifv.mobitopp.actitopp.steps.step1.times
import edu.kit.ifv.mobitopp.actitopp.steps.step3.TourSituationInt
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters

val ParameterSet5B = ParameterCollectionStep5B(
    one = ParameterStep5B(
        base = -2.0784,
        tourliegtvorhaupttour = -0.5151,
        tourliegtnachhaupttour = -0.7230,
        anzaktvorhauptaktist1 = 0.8519,
        anzaktvorhauptaktist2 = 0.4943,
        anzaktvorhauptaktist3 = 0.3991,
        tourtyp_work = 0.5193,
        tourtyp_education = 0.0403,
        tag_sa = -0.3161,
        tag_so = -0.6726,
        alter_18bis35 = 0.2687,
        alter_36bis50 = 0.1531,
        alter_51bis60 = 0.1606,
        taghat1tour = 1.2483,
        taghat2touren = 0.5485,
        mean_1akt = -2.6058,
        mean_2akt = -1.1583,
        mean_3akt = -0.4630,
    ),
    two = ParameterStep5B(
        base = -3.1018,
        tourliegtvorhaupttour = -0.8201,
        tourliegtnachhaupttour = -1.4016,
        anzaktvorhauptaktist1 = 0.4102,
        anzaktvorhauptaktist2 = 0.1910,
        anzaktvorhauptaktist3 = 0.1471,
        tourtyp_work = 0.8456,
        tourtyp_education = 0.5640,
        tag_sa = -0.4062,
        tag_so = -0.9829,
        alter_18bis35 = 0.2799,
        alter_36bis50 = 0.1770,
        alter_51bis60 = 0.2119,
        taghat1tour = 1.8677,
        taghat2touren = 0.8603,
        mean_1akt = -5.0522,
        mean_2akt = -2.3127,
        mean_3akt = -0.8452,
    ),
    three = ParameterStep5B(
        base = -4.2932,
        tourliegtvorhaupttour = -1.0606,
        tourliegtnachhaupttour = -1.4953,
        anzaktvorhauptaktist1 = 0.5124,
        anzaktvorhauptaktist2 = 0.1081,
        anzaktvorhauptaktist3 = 0.0437,
        tourtyp_work = 0.9072,
        tourtyp_education = 0.4734,
        tag_sa = -0.6776,
        tag_so = -1.1956,
        alter_18bis35 = 0.3258,
        alter_36bis50 = 0.2295,
        alter_51bis60 = 0.3100,
        taghat1tour = 2.4447,
        taghat2touren = 1.0490,
        mean_1akt = -16.2820,
        mean_2akt = -3.3737,
        mean_3akt = -1.2953,
    ),
    four = ParameterStep5B(
        base = -5.1109,
        tourliegtvorhaupttour = -1.1327,
        tourliegtnachhaupttour = -1.5191,
        anzaktvorhauptaktist1 = 0.3907,
        anzaktvorhauptaktist2 = -0.0821,
        anzaktvorhauptaktist3 = 0.0576,
        tourtyp_work = 1.1357,
        tourtyp_education = 0.9179,
        tag_sa = -0.5851,
        tag_so = -1.4169,
        alter_18bis35 = 0.0873,
        alter_36bis50 = -0.0376,
        alter_51bis60 = -0.1265,
        taghat1tour = 2.9222,
        taghat2touren = 1.4016,
        mean_1akt = -16.6839,
        mean_2akt = -4.5298,
        mean_3akt = -1.8738,
    ),
    five = ParameterStep5B(
        base = -6.0107,
        tourliegtvorhaupttour = -1.0865,
        tourliegtnachhaupttour = -1.3880,
        anzaktvorhauptaktist1 = 0.3098,
        anzaktvorhauptaktist2 = 0.2972,
        anzaktvorhauptaktist3 = 0.3875,
        tourtyp_work = 1.0696,
        tourtyp_education = 0.7483,
        tag_sa = -0.5867,
        tag_so = -1.6875,
        alter_18bis35 = 0.0975,
        alter_36bis50 = 0.0277,
        alter_51bis60 = 0.0450,
        taghat1tour = 3.6449,
        taghat2touren = 1.7386,
        mean_1akt = -16.8479,
        mean_2akt = -6.0334,
        mean_3akt = -2.6684,
    )
)


data class ParameterCollectionStep5B(

    val one: ParameterStep5B,
    val two: ParameterStep5B,
    val three: ParameterStep5B,
    val four: ParameterStep5B,
    val five: ParameterStep5B,
)

data class ParameterStep5B(
    val base: Double,
    val tourliegtvorhaupttour: Double,
    val tourliegtnachhaupttour: Double,
    val anzaktvorhauptaktist1: Double,
    val anzaktvorhauptaktist2: Double,
    val anzaktvorhauptaktist3: Double,
    val tourtyp_work: Double,
    val tourtyp_education: Double,
    val tag_sa: Double,
    val tag_so: Double,
    val alter_18bis35: Double,
    val alter_36bis50: Double,
    val alter_51bis60: Double,
    val taghat1tour: Double,
    val taghat2touren: Double,
    val mean_1akt: Double,
    val mean_2akt: Double,
    val mean_3akt: Double,


)


val step5BModel =
    ModifiableDiscreteChoiceModel<Int, TourSituationInt, ParameterCollectionStep5B>(AllocatedLogit.create {
        option(0) { 0.0 }
        option(1, parameters = { one }, {
            val util = standardUtilityFunction(this, it)
            util
        })
        option(2, parameters = { two }, { standardUtilityFunction(this, it) })
        option(3, parameters = { three }, { standardUtilityFunction(this, it) })
        option(4, parameters = { four }, { standardUtilityFunction(this, it) })
        option(5, parameters = { five }, { standardUtilityFunction(this, it) })
    })

val step5BWithParams = step5BModel.initializeWithParameters(ParameterSet5B)
private val standardUtilityFunction: ParameterStep5B.(TourSituationInt) -> Double = {
    base +
            (it.isBeforeMainTour()) * tourliegtvorhaupttour+
                (it.isAfterMainTour()) * tourliegtnachhaupttour+
                (it.numActivitiesBeforeMainActivityIs1()) * anzaktvorhauptaktist1+
                (it.numActivitiesBeforeMainActivityIs2()) * anzaktvorhauptaktist2+
                (it.numActivitiesBeforeMainActivityIs3()) * anzaktvorhauptaktist3+
                (it.tourMainActivityIsWork()) * tourtyp_work+
                (it.tourMainActivityIsEducation()) * tourtyp_education+
                (it.isSaturday()) * tag_sa+
                (it.isSunday()) * tag_so+
                (it.isAged18To35()) * alter_18bis35+
                (it.isAged36To50()) * alter_36bis50+
                (it.isAged51To60()) * alter_51bis60+
                (it.amountOfToursIs1()) * taghat1tour+
                (it.amountOfToursIs2()) * taghat2touren+
                (it.averageAmountOfActivitiesIs1()) * mean_1akt+
                (it.averageAmountOfActivitiesIs2()) * mean_2akt+
                (it.averageAmountOfActivitiesIs3()) * mean_3akt

}
