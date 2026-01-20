package edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters

/**
 * The original parameter set for the amount of immobile days, taken from mop14_withpkwhh. Originally called 1FParams.
 */
val DefaultHomeParameters = HomeDaySet.create(
    option1 = HomeDayParameters(
        base = 3.5739,
        employmentNotEarning = -0.3249,
        employmentRetired = -0.3439,
        ageIn18To25 = 0.3145,
        amountOfYouths = -0.1346,
        isMale = -0.2760,
        amountOfWorkingDays = -0.3952,
        amountOfEducationDays = -0.2284,
        amountOfLeisureDays = -0.5973,
        amountOfShoppingDays = -0.3244,
    ),
    option2 = HomeDayParameters(
        base = 7.6320,
        employmentNotEarning = -0.7489,
        employmentRetired = -0.6659,
        ageIn18To25 = 0.3366,
        amountOfYouths = -0.2981,
        isMale = -0.4090,
        amountOfWorkingDays = -1.0750,
        amountOfEducationDays = -0.7947,
        amountOfLeisureDays = -1.1970,
        amountOfShoppingDays = -0.9637,
    ),
    option3 = HomeDayParameters(
        base = 11.8825,
        employmentNotEarning = -1.1891,
        employmentRetired = -1.1118,
        ageIn18To25 = 0.8478,
        amountOfYouths = -0.5397,
        isMale = -0.2338,
        amountOfWorkingDays = -2.0793,
        amountOfEducationDays = -1.8776,
        amountOfLeisureDays = -1.9709,
        amountOfShoppingDays = -1.8096,
    ),
    option4 = HomeDayParameters(
        base = 15.1575,
        employmentNotEarning = -1.5659,
        employmentRetired = -1.4033,
        ageIn18To25 = 0.8321,
        amountOfYouths = -0.4287,
        isMale = -0.1296,
        amountOfWorkingDays = -2.9798,
        amountOfEducationDays = -2.6430,
        amountOfLeisureDays = -2.7910,
        amountOfShoppingDays = -2.8184,
    ),
    option5 = HomeDayParameters(
        base = 18.9161,
        employmentNotEarning = -1.4267,
        employmentRetired = -1.5383,
        ageIn18To25 = 1.5662,
        amountOfYouths = -0.7466,
        isMale = -0.1758,
        amountOfWorkingDays = -4.6381,
        amountOfEducationDays = -4.1567,
        amountOfLeisureDays = -4.1593,
        amountOfShoppingDays = -4.4475,
    ),
    option6 = HomeDayParameters(
        base = 22.0357,
        employmentNotEarning = -2.4041,
        employmentRetired = -2.0068,
        ageIn18To25 = 0.6195,
        amountOfYouths = -1.0575,
        isMale = -0.2178,
        amountOfWorkingDays = -5.9946,
        amountOfEducationDays = -6.0716,
        amountOfLeisureDays = -5.9283,
        amountOfShoppingDays = -6.6534,
    ),
    option7 = HomeDayParameters(
        base = 21.1712,
        employmentNotEarning = -8.8502,
        employmentRetired = -14.6527,
        ageIn18To25 = -9.3926,
        amountOfYouths = -0.4333,
        isMale = 12.4611,
        amountOfWorkingDays = -12.3041,
        amountOfEducationDays = -13.9196,
        amountOfLeisureDays = -25.1790,
        amountOfShoppingDays = -25.2436,
    )
)

/**
 * Collects parameters of [HomeDayParameters] to apply the parameters to the set of options, which all take the same
 * parameter structure.
 */
data class HomeDaySet(
    override val parameters: Map<Int, HomeDayParameters>,
) : WeekRoutineParameterSet<HomeDayParameters>, Map<Int, HomeDayParameters> by parameters {
    companion object {
        fun create(
            option1: HomeDayParameters,
            option2: HomeDayParameters,
            option3: HomeDayParameters,
            option4: HomeDayParameters,
            option5: HomeDayParameters,
            option6: HomeDayParameters,
            option7: HomeDayParameters,
        ): HomeDaySet {
            return HomeDaySet(
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
 * This class contains the parameters for the utility function to determine the amount of home days in the week routine.
 * @param base The default parameter.
 * @param employmentNotEarning Parameter that should be applied when the person employment is considered to not earn money
 * @param employmentRetired Parameter that should be applied when the person is retired.
 * @param ageIn18To25 Parameter that should be applied when the person is aged between 18 and 25 (inclusive)
 * @param amountOfYouths Parameter that should be multiplied with the amount of minors (0-17) in the household.
 * @param isMale Parameter that should be applied when the person is Male
 * @param amountOfWorkingDays Parameter that should be multiplied with the amount of working days in the week routine
 * @param amountOfEducationDays Parameter that should be multiplied with the amount of education days in the week routine
 * @param amountOfLeisureDays Parameter that should be multiplied with the amount of leisure days in the week routine
 * @param amountOfShoppingDays Parameter that should be multiplied with the amount of shopping days in the week routine
 */
data class HomeDayParameters(
    val base: Double,
    val employmentNotEarning: Double,
    val employmentRetired: Double,
    val ageIn18To25: Double,
    val amountOfYouths: Double,
    val isMale: Double,
    val amountOfWorkingDays: Double,
    val amountOfEducationDays: Double,
    val amountOfLeisureDays: Double,
    val amountOfShoppingDays: Double,
)