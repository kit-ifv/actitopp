package edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters

/**
 * Collects parameters of [ShoppingDayParameters] to apply the parameters to the set of options, which all take the same
 * parameter structure.
 */
data class ShoppingDaySet(
    override val parameters: Map<Int, ShoppingDayParameters>,
) : WeekRoutineParameterSet<ShoppingDayParameters>, Map<Int, ShoppingDayParameters> by parameters {
    companion object {
        fun create(
            option1: ShoppingDayParameters,
            option2: ShoppingDayParameters,
            option3: ShoppingDayParameters,
            option4: ShoppingDayParameters,
            option5: ShoppingDayParameters,
            option6: ShoppingDayParameters,
            option7: ShoppingDayParameters,
        ): ShoppingDaySet {
            return ShoppingDaySet(
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
 * This class contains the parameters for the utility function to determine the amount of shopping days in the week routine.
 * @param base The default parameter.
 * @param employmentIsEarning Parameter that should be applied when the person employment is considered to earn money
 * @param emplomentStudent Parameter that should be applied when the person is a student.
 * @param employmentVocational Parameter that should be applied when the person is a in a vocational program.
 * @param ageIn10To17 Parameter that should be applied when the person is aged between 10 and 17 (inclusive)
 * @param areaTypeIsConurbation Parameter that should be applied when the household area type is considered conurbation
 * @param areaTypeIsRural Parameter that should be applied when the household area type is considered rural
 * @param amountOfWorkingDays Parameter that should be multiplied with the amount of working days in the week routine
 * @param amountOfEducationDays Parameter that should be multiplied with the amount of education days in the week routine
 * @param amountOfLeisureDays Parameter that should be multiplied with the amount of leisure days in the week routine

 */
data class ShoppingDayParameters(
    val base: Double,
    val employmentIsEarning: Double,
    val emplomentStudent: Double,
    val employmentVocational: Double,
    val ageIn10To17: Double,
    val areaTypeIsConurbation: Double,
    val areaTypeIsRural: Double,
    val amountOfWorkingDays: Double,
    val amountOfEducationDays: Double,
    val amountOfLeisureDays: Double,
)

/**
 * The original parameter set for the amount of shopping days, taken from mop14_withpkwhh. Originally called 1DParams.
 */
val DefaultShoppingParameters = ShoppingDaySet.create(
    option1 = ShoppingDayParameters(
        base = 0.9053,
        employmentIsEarning = 0.0556,
        emplomentStudent = -0.0939,
        employmentVocational = -0.1432,
        ageIn10To17 = -0.2341,
        areaTypeIsConurbation = 0.1679,
        areaTypeIsRural = -0.3859,
        amountOfWorkingDays = -0.0943,
        amountOfEducationDays = -0.2162,
        amountOfLeisureDays = 0.0807,
    ),
    option2 = ShoppingDayParameters(
        base = 1.6032,
        employmentIsEarning = 0.0908,
        emplomentStudent = -0.4189,
        employmentVocational = -0.7189,
        ageIn10To17 = -0.7975,
        areaTypeIsConurbation = 0.1636,
        areaTypeIsRural = -0.3414,
        amountOfWorkingDays = -0.2019,
        amountOfEducationDays = -0.3122,
        amountOfLeisureDays = 0.0950,
    ),
    option3 = ShoppingDayParameters(
        base = 1.8425,
        employmentIsEarning = -0.0876,
        emplomentStudent = -0.8263,
        employmentVocational = -0.9395,
        ageIn10To17 = -1.3385,
        areaTypeIsConurbation = 0.2795,
        areaTypeIsRural = -0.4664,
        amountOfWorkingDays = -0.2749,
        amountOfEducationDays = -0.4017,
        amountOfLeisureDays = 0.1456,
    ),
    option4 = ShoppingDayParameters(
        base = 1.8553,
        employmentIsEarning = -0.2713,
        emplomentStudent = -1.0863,
        employmentVocational = -1.0351,
        ageIn10To17 = -1.6907,
        areaTypeIsConurbation = 0.3847,
        areaTypeIsRural = -0.6626,
        amountOfWorkingDays = -0.3279,
        amountOfEducationDays = -0.5432,
        amountOfLeisureDays = 0.1704,
    ),
    option5 = ShoppingDayParameters(
        base = 1.6620,
        employmentIsEarning = -0.3127,
        emplomentStudent = -1.2827,
        employmentVocational = -1.2987,
        ageIn10To17 = -3.1522,
        areaTypeIsConurbation = 0.4411,
        areaTypeIsRural = -0.6480,
        amountOfWorkingDays = -0.3836,
        amountOfEducationDays = -0.5795,
        amountOfLeisureDays = 0.1608,
    ),
    option6 = ShoppingDayParameters(
        base = 0.9768,
        employmentIsEarning = -0.4578,
        emplomentStudent = -1.5374,
        employmentVocational = -2.7380,
        ageIn10To17 = -3.3079,
        areaTypeIsConurbation = 0.4446,
        areaTypeIsRural = -0.8533,
        amountOfWorkingDays = -0.4350,
        amountOfEducationDays = -0.5509,
        amountOfLeisureDays = 0.1825,
    ),
    option7 = ShoppingDayParameters(
        base = -0.5037,
        employmentIsEarning = -0.2544,
        emplomentStudent = -1.0175,
        employmentVocational = -0.9605,
        ageIn10To17 = -11.3719,
        areaTypeIsConurbation = 0.6513,
        areaTypeIsRural = -0.8707,
        amountOfWorkingDays = -0.4274,
        amountOfEducationDays = -0.5946,
        amountOfLeisureDays = 0.1052,
    )
)