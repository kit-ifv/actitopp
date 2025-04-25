package edu.kit.ifv.mobitopp.actitopp.steps.step3

import edu.kit.ifv.mobitopp.actitopp.steps.step1.times
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters

val ParameterSet3B = ParameterCollectionStep3B(
    one = ParameterStep3B(
        base = 0.5789,
        employmentPartTime = 0.0113,
        mainActivityIsWork = 0.9500,
        mainActivityIsEducation = 1.4834,
        mainActivityIsShopping = 0.4203,
        friday = 0.2256,
        saturday = -0.2637,
        sunday = -0.6720,
        aged26To35 = 0.0471,
        aged36To50 = 0.0792,
        aged51To60 = 0.0190,
        areaTypeConurbation = -0.0108,
        areaTypeRural = -0.1101,
        amountOfPKW = 0.0136,
        amountOfToursBeforeMainAct = -1.0145,
        commuteOver50km = -0.0652,
        commuteIn0To5km = 0.0790,
        averageAmountOfToursIs1 = -3.0086,
        averageAmountOfToursIs2 = -1.3333,
        previousDayHas0TourAfterMainAct = -0.1752,
        previousDayHas1TourAfterMainAct = -0.0343,
    ),
    two =  ParameterStep3B(
        base = 0.1936,
        employmentPartTime = 0.2043,
        mainActivityIsWork = 1.0110,
        mainActivityIsEducation = 1.5312,
        mainActivityIsShopping = 0.4252,
        friday = 0.4964,
        saturday = -0.5850,
        sunday = -1.4555,
        aged26To35 = 0.1015,
        aged36To50 = 0.2735,
        aged51To60 = 0.1010,
        areaTypeConurbation = -0.1302,
        areaTypeRural = -0.2701,
        amountOfPKW = 0.0763,
        amountOfToursBeforeMainAct = -1.5397,
        commuteOver50km = -0.0894,
        commuteIn0To5km = 0.1619,
        averageAmountOfToursIs1 = -5.9799,
        averageAmountOfToursIs2 = -2.5788,
        previousDayHas0TourAfterMainAct = -0.3761,
        previousDayHas1TourAfterMainAct = -0.1560,
    ),
    three =  ParameterStep3B(
        base = -0.8203,
        employmentPartTime = 0.4412,
        mainActivityIsWork = 0.8873,
        mainActivityIsEducation = 1.5317,
        mainActivityIsShopping = 0.4632,
        friday = 0.5957,
        saturday = -1.4020,
        sunday = -2.7180,
        aged26To35 = 0.1765,
        aged36To50 = 0.4615,
        aged51To60 = 0.0616,
        areaTypeConurbation = -0.2589,
        areaTypeRural = -0.2546,
        amountOfPKW = 0.0703,
        amountOfToursBeforeMainAct = -1.7337,
        commuteOver50km = -0.2747,
        commuteIn0To5km = 0.2061,
        averageAmountOfToursIs1 = -10.2485,
        averageAmountOfToursIs2 = -3.7658,
        previousDayHas0TourAfterMainAct = -0.5696,
        previousDayHas1TourAfterMainAct = -0.3298,
    ),
    four =  ParameterStep3B(
        base = -2.2168,
        employmentPartTime = 0.4129,
        mainActivityIsWork = 0.3950,
        mainActivityIsEducation = 1.4755,
        mainActivityIsShopping = 0.3248,
        friday = 0.6611,
        saturday = -0.8942,
        sunday = -3.0416,
        aged26To35 = -0.2819,
        aged36To50 = 0.4303,
        aged51To60 = -0.0365,
        areaTypeConurbation = -0.2239,
        areaTypeRural = -0.1855,
        amountOfPKW = 0.0900,
        amountOfToursBeforeMainAct = -1.7013,
        commuteOver50km = 0.9286,
        commuteIn0To5km = 0.4319,
        averageAmountOfToursIs1 = -16.9995,
        averageAmountOfToursIs2 = -4.8098,
        previousDayHas0TourAfterMainAct = -0.3370,
        previousDayHas1TourAfterMainAct = -0.5268,
    ),
    five =  ParameterStep3B(
        base = -3.6532,
        employmentPartTime = 1.3211,
        mainActivityIsWork = 0.6101,
        mainActivityIsEducation = 1.9014,
        mainActivityIsShopping = -0.3265,
        friday = 1.0038,
        saturday = -0.2701,
        sunday = -10.1743,
        aged26To35 = 0.2141,
        aged36To50 = 0.0606,
        aged51To60 = 0.3266,
        areaTypeConurbation = -0.3580,
        areaTypeRural = 0.8252,
        amountOfPKW = -0.2729,
        amountOfToursBeforeMainAct = -1.6136,
        commuteOver50km = 1.2476,
        commuteIn0To5km = 0.4168,
        averageAmountOfToursIs1 = -15.7495,
        averageAmountOfToursIs2 = -5.2465,
        previousDayHas0TourAfterMainAct = -1.2471,
        previousDayHas1TourAfterMainAct = -0.9772,
    )

)


data class ParameterCollectionStep3B(

    val one: ParameterStep3B,
    val two: ParameterStep3B,
    val three: ParameterStep3B,
    val four: ParameterStep3B,
    val five: ParameterStep3B,
)

data class ParameterStep3B(
    val base: Double,
    val employmentPartTime: Double,
    val mainActivityIsWork: Double,
    val mainActivityIsEducation: Double,
    val mainActivityIsShopping: Double,
    val friday: Double,
    val saturday: Double,
    val sunday: Double,
    val aged26To35: Double,
    val aged36To50: Double,
    val aged51To60: Double,
    val areaTypeConurbation: Double,
    val areaTypeRural: Double,
    val amountOfPKW: Double,
    val amountOfToursBeforeMainAct: Double,
    val commuteOver50km: Double,
    val commuteIn0To5km: Double,
    val averageAmountOfToursIs1: Double,
    val averageAmountOfToursIs2: Double,
    val previousDayHas0TourAfterMainAct: Double,
    val previousDayHas1TourAfterMainAct: Double
)


val step3BModel =
    ModifiableDiscreteChoiceModel<Int, PreviousDaySituation, ParameterCollectionStep3B>(AllocatedLogit.create {
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

val step3BWithParams = step3BModel.initializeWithParameters(ParameterSet3B)
private val standardUtilityFunction: ParameterStep3B.(PreviousDaySituation) -> Double = {
    base +
            (it.isParttimeEmployee()) * employmentPartTime+
                (it.mainActivityIsWork()) * mainActivityIsWork+
                (it.mainActivityIsEducation()) * mainActivityIsEducation+
                (it.mainActivityIsShopping()) * mainActivityIsShopping+
                (it.isFriday()) * friday+
                (it.isSaturday()) * saturday+
                (it.isSunday()) * sunday+
                (it.isAged26To35()) * aged26To35+
                (it.isAged36To50()) * aged36To50+
                (it.isAged51To60()) * aged51To60+
                (it.areaTypeConurbation()) * areaTypeConurbation+
                (it.areaTypeRural()) * areaTypeRural+
                (it.amountOfPKW()) * amountOfPKW+
                (it.amountOfBeforeTours()) * amountOfToursBeforeMainAct+
                (it.commuteOver50km()) * commuteOver50km+
                (it.commuteIn0To5km()) * commuteIn0To5km+
                (it.averageAmountOfToursIs1()) * averageAmountOfToursIs1+
                (it.averageAmountOfToursIs2()) * averageAmountOfToursIs2+
                (it.previousDayHasNoAfterTour()) * previousDayHas0TourAfterMainAct+
                (it.previousDayHasOneAfterTour()) * previousDayHas1TourAfterMainAct
}
