package edu.kit.ifv.mobitopp.actitopp.steps.step5

import edu.kit.ifv.mobitopp.actitopp.steps.step1.times

import edu.kit.ifv.mobitopp.actitopp.steps.step3.TourSituationInt
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters



data class ParameterCollectionStep5A(
    val one: ParameterStep5A,
    val two: ParameterStep5A,
    val three: ParameterStep5A,
    val four: ParameterStep5A,
    val five: ParameterStep5A,
)

data class ParameterStep5A(
    val base: Double,
    val tourliegtvorhaupttour: Double,
    val tourliegtnachhaupttour: Double,
    val tourtyp_work: Double,
    val tourtyp_education: Double,
    val tourtyp_shopping: Double,
    val tourtyp_transport: Double,
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



val ParameterSet5A = ParameterCollectionStep5A(
    one = ParameterStep5A(
        base = -1.5334,
        tourliegtvorhaupttour = -0.5963,
        tourliegtnachhaupttour = -0.7165,
        tourtyp_work = -0.3789,
        tourtyp_education = -1.4326,
        tourtyp_shopping = 0.1317,
        tourtyp_transport = -0.4666,
        tag_sa = -0.1720,
        tag_so = -0.5308,
        alter_18bis35 = 0.3741,
        alter_36bis50 = 0.2290,
        alter_51bis60 = 0.0832,
        taghat1tour = 1.0682,
        taghat2touren = 0.5393,
        mean_1akt = -3.1187,
        mean_2akt = -1.4959,
        mean_3akt = -0.5852,
    ),
    two = ParameterStep5A(
        base = -2.6983,
        tourliegtvorhaupttour = -0.9454,
        tourliegtnachhaupttour = -1.0733,
        tourtyp_work = -0.1161,
        tourtyp_education = -1.1785,
        tourtyp_shopping = 0.0488,
        tourtyp_transport = -0.2580,
        tag_sa = -0.2537,
        tag_so = -1.0807,
        alter_18bis35 = 0.3858,
        alter_36bis50 = 0.1643,
        alter_51bis60 = 0.1188,
        taghat1tour = 1.7630,
        taghat2touren = 0.8818,
        mean_1akt = -4.9836,
        mean_2akt = -2.3602,
        mean_3akt = -0.9110,
    ),
    three = ParameterStep5A(
        base = -3.3992,
        tourliegtvorhaupttour = -0.9943,
        tourliegtnachhaupttour = -1.5503,
        tourtyp_work = -0.7125,
        tourtyp_education = -1.9518,
        tourtyp_shopping = -0.1926,
        tourtyp_transport = -0.5158,
        tag_sa = -0.3903,
        tag_so = -1.3523,
        alter_18bis35 = 0.3273,
        alter_36bis50 = 0.0822,
        alter_51bis60 = 0.0762,
        taghat1tour = 2.2201,
        taghat2touren = 1.0280,
        mean_1akt = -16.8269,
        mean_2akt = -3.5207,
        mean_3akt = -1.3387,
    ),
    four = ParameterStep5A(
        base = -4.5555,
        tourliegtvorhaupttour = -1.6154,
        tourliegtnachhaupttour = -1.8120,
        tourtyp_work = -0.2830,
        tourtyp_education = -1.5008,
        tourtyp_shopping = 0.0747,
        tourtyp_transport = -0.1849,
        tag_sa = -0.2954,
        tag_so = -1.0478,
        alter_18bis35 = 0.1626,
        alter_36bis50 = 0.0455,
        alter_51bis60 = 0.0991,
        taghat1tour = 2.4098,
        taghat2touren = 1.2911,
        mean_1akt = -17.0225,
        mean_2akt = -4.0460,
        mean_3akt = -1.6038,
    ),
    five = ParameterStep5A(
        base = -4.4273,
        tourliegtvorhaupttour = -1.8531,
        tourliegtnachhaupttour = -1.8879,
        tourtyp_work = -0.9325,
        tourtyp_education = -2.6543,
        tourtyp_shopping = -0.7311,
        tourtyp_transport = -0.4100,
        tag_sa = -0.5969,
        tag_so = -2.2775,
        alter_18bis35 = 0.4454,
        alter_36bis50 = 0.0800,
        alter_51bis60 = 0.4022,
        taghat1tour = 3.1419,
        taghat2touren = 1.5784,
        mean_1akt = -17.3409,
        mean_2akt = -5.4947,
        mean_3akt = -2.6838,
    )
)

val step5AModel =
    ModifiableDiscreteChoiceModel<Int, TourSituationInt, ParameterCollectionStep5A>(AllocatedLogit.create {
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

val step5AWithParams = step5AModel.initializeWithParameters(ParameterSet5A)
private val standardUtilityFunction: ParameterStep5A.(TourSituationInt) -> Double = {
    base +

    (it.isBeforeMainTour()) * tourliegtvorhaupttour+
        (it.isAfterMainTour()) * tourliegtnachhaupttour+
        (it.mainActivityIsWork()) * tourtyp_work+
        (it.mainActivityIsEducation()) * tourtyp_education+
        (it.mainActivityIsShopping()) * tourtyp_shopping+
        (it.mainActivityIsTransport()) * tourtyp_transport+
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
