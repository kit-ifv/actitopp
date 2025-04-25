package edu.kit.ifv.mobitopp.actitopp.steps.step3


data class ParameterCollectionStep3B(
    val zero: ParameterStep3B,
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
    val aged35To50: Double,
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