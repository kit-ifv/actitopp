package edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters


data class EducationDaySet(
    override val parameters: Map<Int, EducationDayParameters>,
) : WeekRoutineParameterSet<EducationDayParameters>, Map<Int, EducationDayParameters> by parameters {
    companion object {
        fun create(
            option1: EducationDayParameters,
            option2: EducationDayParameters,
            option3: EducationDayParameters,
            option4: EducationDayParameters,
            option5: EducationDayParameters,
            option6: EducationDayParameters,
            option7: EducationDayParameters,
        ): EducationDaySet {
            return EducationDaySet(
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
 * This class contains the parameters for the utility function to determine the amount of education days in the week
 * pattern.
 * @param base The default parameter for the utility function.
 * @param employmentIsEarning Parameter that should be applied when the person employment is considered a money-earner.
 * @param employmentVocational Parameter that should be applied when the person is a student.
 * @param beruf_azubi Parameter that should be applied when the person is in a vocational program.
 * @param ageIn10to17 Parameter that should be applied when the person is aged between 10 and 17 (inclusive)
 * @param ageIn18To25 Parameter that should be applied when the person is aged between 18 and 25 (inclusive)
 * @param ageIn26To35 Parameter that should be applied when the person is aged between 26 and 35 (inclusive)
 * @param ageIn36To50 Parameter that should be applied when the person is aged between 36 and 50 (inclusive)
 * @param amountOfWorkingDays Parameter that should be applied to the amount of work days in the week pattern
 */
data class EducationDayParameters(
    val base: Double,
    val employmentIsEarning: Double,
    val emplomentStudent: Double,
    val employmentVocational: Double,
    val ageIn10to17: Double,
    val ageIn18To25: Double,
    val ageIn26To35: Double,
    val ageIn36To50: Double,
    val amountOfWorkingDays: Double,
)

/**
 * The original parameter set for the education day amount, taken from mop14_withpkwhh. Originally called 1BParams.
 */
val DefaultEducationParameters = EducationDaySet.create(
    option1 = EducationDayParameters(
        base = -3.6837,
        employmentIsEarning = -0.0309,
        emplomentStudent = 1.7797,
        employmentVocational = 1.9284,
        ageIn10to17 = 1.1245,
        ageIn18To25 = 1.0811,
        ageIn26To35 = 0.2360,
        ageIn36To50 = 0.1252,
        amountOfWorkingDays = -0.0419,
    ),
    option2 = EducationDayParameters(
        base = -5.3808,
        employmentIsEarning = 0.6181,
        emplomentStudent = 3.0265,
        employmentVocational = 3.3246,
        ageIn10to17 = 2.3716,
        ageIn18To25 = 1.6515,
        ageIn26To35 = 0.7754,
        ageIn36To50 = 0.4040,
        amountOfWorkingDays = -0.1913,
    ),
    option3 = EducationDayParameters(
        base = -7.2629,
        employmentIsEarning = 0.6163,
        emplomentStudent = 3.1352,
        employmentVocational = 2.9263,
        ageIn10to17 = 5.0566,
        ageIn18To25 = 3.8711,
        ageIn26To35 = 2.8639,
        ageIn36To50 = 1.8879,
        amountOfWorkingDays = -0.3825,
    ),
    option4 = EducationDayParameters(
        base = -7.6147,
        employmentIsEarning = 0.1156,
        emplomentStudent = 3.3109,
        employmentVocational = 3.1657,
        ageIn10to17 = 6.5826,
        ageIn18To25 = 4.5985,
        ageIn26To35 = 3.6671,
        ageIn36To50 = 2.5828,
        amountOfWorkingDays = -0.6194,
    ),
    option5 = EducationDayParameters(
        base = -6.2095,
        employmentIsEarning = 0.7225,
        emplomentStudent = 3.9314,
        employmentVocational = 4.3272,
        ageIn10to17 = 6.3976,
        ageIn18To25 = 3.7410,
        ageIn26To35 = 2.1711,
        ageIn36To50 = 2.0582,
        amountOfWorkingDays = -0.9444,
    ),
    option6 = EducationDayParameters(
        base = -19.9765,
        employmentIsEarning = 6.9806,
        emplomentStudent = 10.1357,
        employmentVocational = 10.7913,
        ageIn10to17 = 10.1219,
        ageIn18To25 = 8.2822,
        ageIn26To35 = 6.8287,
        ageIn36To50 = 6.3302,
        amountOfWorkingDays = -0.8178,
    ),
    option7 = EducationDayParameters(
        base = -8.6375,
        employmentIsEarning = -8.4610,
        emplomentStudent = 3.0937,
        employmentVocational = 4.4255,
        ageIn10to17 = 3.2337,
        ageIn18To25 = 2.3171,
        ageIn26To35 = -4.1795,
        ageIn36To50 = -3.5479,
        amountOfWorkingDays = -0.8655,
    )
)