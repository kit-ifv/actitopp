package edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters

/**
 * Collects parameters of [LeisureDayParameters] to apply the parameters to the set of options, which all take the same
 * parameter structure.
 */
data class LeisureDaySet(
    override val parameters: Map<Int, LeisureDayParameters>,
) : WeekRoutineParameterSet<LeisureDayParameters>, Map<Int, LeisureDayParameters> by parameters {
    companion object {
        fun create(
            option1: LeisureDayParameters,
            option2: LeisureDayParameters,
            option3: LeisureDayParameters,
            option4: LeisureDayParameters,
            option5: LeisureDayParameters,
            option6: LeisureDayParameters,
            option7: LeisureDayParameters,
        ): LeisureDaySet {
            return LeisureDaySet(
                mapOf(
                    1 to option1,
                    2 to option2,
                    3 to option3,
                    4 to option4,
                    5 to option5,
                    6 to option6,
                    7 to option7,
                )
            )
        }
    }
}

/**
 * This class contains the parameters for the utility function to determine the amount of leisure days in the week routine.
 * @param base The default parameter.
 * @param employmentIsEarning Parameter that should be applied when the person employment is considered to earn money
 * @param emplomentStudent Parameter that should be applied when the person is a student.
 * @param ageIn61To70 Parameter that should be applied when the person is aged between 61 and 70 (inclusive)
 * @param areaTypeIsRural Parameter that should be applied when the household area type is considered rural
 * @param householdHasChildenBelowAge10 Parameter that should be applied when the household has children (0-10)
 * @param amountOfWorkingDays Parameter that should be multiplied with the amount of working days in the week routine
 */
data class LeisureDayParameters(
    val base: Double,
    val employmentIsEarning: Double,
    val emplomentStudent: Double,
    val ageIn61To70: Double,
    val areaTypeIsRural: Double,
    val householdHasChildenBelowAge10: Double,
    val amountOfWorkingDays: Double,
)

/**
 * The original parameter set for the amount of leisure days, taken from mop14_withpkwhh. Originally called 1CParams.
 */
val DefaultLeisureParameters = LeisureDaySet.create(
    option1 = LeisureDayParameters(
        base = 0.7714,
        employmentIsEarning = -0.1966,
        emplomentStudent = -0.2525,
        ageIn61To70 = 0.0147,
        areaTypeIsRural = -0.3753,
        householdHasChildenBelowAge10 = 0.2130,
        amountOfWorkingDays = -0.00570,
    ),
    option2 = LeisureDayParameters(
        base = 1.0050,
        employmentIsEarning = -0.0281,
        emplomentStudent = 0.0364,
        ageIn61To70 = 0.1588,
        areaTypeIsRural = -0.4818,
        householdHasChildenBelowAge10 = 0.3640,
        amountOfWorkingDays = -0.0381,
    ),
    option3 = LeisureDayParameters(
        base = 1.3068,
        employmentIsEarning = -0.2120,
        emplomentStudent = -0.0352,
        ageIn61To70 = 0.1633,
        areaTypeIsRural = -0.5532,
        householdHasChildenBelowAge10 = 0.3833,
        amountOfWorkingDays = -0.0663,
    ),
    option4 = LeisureDayParameters(
        base = 1.3841,
        employmentIsEarning = -0.2480,
        emplomentStudent = -0.1466,
        ageIn61To70 = 0.2794,
        areaTypeIsRural = -0.8125,
        householdHasChildenBelowAge10 = 0.3782,
        amountOfWorkingDays = -0.1161,
    ),
    option5 = LeisureDayParameters(
        base = 1.2402,
        employmentIsEarning = -0.3081,
        emplomentStudent = 0.0997,
        ageIn61To70 = 0.3593,
        areaTypeIsRural = -1.0029,
        householdHasChildenBelowAge10 = 0.2191,
        amountOfWorkingDays = -0.1402,
    ),
    option6 = LeisureDayParameters(
        base = 1.0663,
        employmentIsEarning = -0.4165,
        emplomentStudent = 0.0254,
        ageIn61To70 = 0.4184,
        areaTypeIsRural = -1.3017,
        householdHasChildenBelowAge10 = 0.1361,
        amountOfWorkingDays = -0.1811,
    ),
    option7 = LeisureDayParameters(
        base = 0.9369,
        employmentIsEarning = -0.5151,
        emplomentStudent = -0.5478,
        ageIn61To70 = 0.4617,
        areaTypeIsRural = -0.9691,
        householdHasChildenBelowAge10 = -0.0764,
        amountOfWorkingDays = -0.2329,
    )
)