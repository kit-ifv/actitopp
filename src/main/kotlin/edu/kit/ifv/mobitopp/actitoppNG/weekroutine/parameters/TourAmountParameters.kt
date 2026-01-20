package edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters


data class TourAmountSet(
    val parameters: List<TourAmountParameters>,


    ) : List<TourAmountParameters> by parameters {
    companion object {
        fun create(
            option2: TourAmountParameters,
            option3: TourAmountParameters,
            option4: TourAmountParameters,
        ): TourAmountSet {
            return TourAmountSet(listOf(option2, option3, option4))
        }
    }
}

data class TourAmountParameters(
    val base: Double,
    val employmentFulltime: Double,
    val employmentParttime: Double,
    val employmentStudent: Double,
    val ageIn26To35: Double,
    val ageIn36To50: Double,
    val ageIn51To60: Double,
    val ageIn61To70: Double,
    val areaTypeConurbation: Double,
    val areaTypeRural: Double,
    val commuteOver50km: Double,
    val commuteIn0To5Km: Double,
    val householdHasChildren: Double,
    val isMale: Double,
)

val DefaultTourAmountParameters = TourAmountSet.create(
    option2 = TourAmountParameters(
        base = -0.2683,
        employmentFulltime = -0.4042,
        employmentParttime = 0.4274,
        employmentStudent = 0.1331,
        ageIn26To35 = 0.1219,
        ageIn36To50 = 0.3386,
        ageIn51To60 = 0.1542,
        ageIn61To70 = 0.2268,
        areaTypeConurbation = -0.1595,
        areaTypeRural = -0.4380,
        commuteOver50km = -0.4031,
        commuteIn0To5Km = 0.5536,
        householdHasChildren = 0.3813,
        isMale = 0.191,
    ),
    option3 = TourAmountParameters(
        base = -2.9111,
        employmentFulltime = -1.3990,
        employmentParttime = 0.3747,
        employmentStudent = -0.9105,
        ageIn26To35 = 1.0101,
        ageIn36To50 = 1.4530,
        ageIn51To60 = 0.9634,
        ageIn61To70 = 0.7962,
        areaTypeConurbation = -0.5179,
        areaTypeRural = -0.5972,
        commuteOver50km = -0.9523,
        commuteIn0To5Km = 0.5824,
        householdHasChildren = 0.9318,
        isMale = 0.4350,
    ),
    option4 = TourAmountParameters(
        base = -5.1338,
        employmentFulltime = -1.7418,
        employmentParttime = -0.3285,
        employmentStudent = -2.2448,
        ageIn26To35 = 1.5401,
        ageIn36To50 = 2.1000,
        ageIn51To60 = 1.4808,
        ageIn61To70 = 0.8747,
        areaTypeConurbation = -0.4314,
        areaTypeRural = -0.4502,
        commuteOver50km = -10.7277,
        commuteIn0To5Km = 0.6899,
        householdHasChildren = 0.7007,
        isMale = -0.0365,
    )
)