package edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters

/**
 * Collects parameters of [ServiceDayParameters] to apply the parameters to the set of options, which all take the same
 * parameter structure.
 */
data class ServiceDaySet(
    override val parameters: Map<Int, ServiceDayParameters>,
) : WeekRoutineParameterSet<ServiceDayParameters>, Map<Int, ServiceDayParameters> by parameters {
    companion object {
        fun create(
            option1: ServiceDayParameters,
            option2: ServiceDayParameters,
            option3: ServiceDayParameters,
            option4: ServiceDayParameters,
            option5: ServiceDayParameters,
            option6: ServiceDayParameters,
            option7: ServiceDayParameters,
        ): ServiceDaySet {
            return ServiceDaySet(
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
 * This class contains the parameters for the utility function to determine the amount of service days in the week routine.
 * @param base The default parameter.
 * @param employmentIsFulltime Parameter that should be applied when the person employment is full time
 * @param employmentIsParttime Parameter that should be applied when the person employment is part-time
 * @param ageIn10to17 Parameter that should be applied when the person is aged between 10 and 17 (inclusive)
 * @param ageIn26to35 Parameter that should be applied when the person is aged between 26 and 35 (inclusive)
 * @param ageIn36to50 Parameter that should be applied when the person is aged between 36 and 50 (inclusive)
 * @param areaTypeIsConurbation Parameter that should be applied when the household area is considered Conurbation
 * @param householdAmountYouths Parameter that should be multiplied with the amount of youths (0-17) in the household
 * @param householdHasChildren Parameter that should be applied when the household has children (0-10)
 *

 */
data class ServiceDayParameters(
    val base: Double,
    val employmentIsFulltime: Double,
    val employmentIsParttime: Double,

    val ageIn10to17: Double,
    val ageIn26to35: Double,
    val ageIn36to50: Double,
    val areaTypeIsConurbation: Double,
    val householdAmountYouths: Double,
    val householdHasChildren: Double,
    val isMale: Double,
)

/**
 * The original parameter set for the amount of service days, taken from mop14_withpkwhh. Originally called 1EParams.
 */
val DefaultServiceParameters = ServiceDaySet.create(
    option1 = ServiceDayParameters(
        base = -1.2172,
        employmentIsFulltime = -0.1229,
        employmentIsParttime = 0.2107,
        ageIn10to17 = -1.2521,
        ageIn26to35 = -0.0203,
        ageIn36to50 = 0.0688,
        areaTypeIsConurbation = -0.1962,
        householdAmountYouths = 0.2413,
        householdHasChildren = 0.2955,
        isMale = 0.0740,
    ),
    option2 = ServiceDayParameters(
        base = -2.1790,
        employmentIsFulltime = -0.2866,
        employmentIsParttime = 0.4389,
        ageIn10to17 = -2.0616,
        ageIn26to35 = -0.0528,
        ageIn36to50 = 0.3667,
        areaTypeIsConurbation = -0.2582,
        householdAmountYouths = 0.4494,
        householdHasChildren = 0.4345,
        isMale = 0.1216,
    ),
    option3 = ServiceDayParameters(
        base = -3.0977,
        employmentIsFulltime = -0.4666,
        employmentIsParttime = 0.7064,
        ageIn10to17 = -3.0800,
        ageIn26to35 = 0.1707,
        ageIn36to50 = 0.4437,
        areaTypeIsConurbation = -0.3219,
        householdAmountYouths = 0.6039,
        householdHasChildren = 0.7027,
        isMale = 0.2155,
    ),
    option4 = ServiceDayParameters(
        base = -3.7580,
        employmentIsFulltime = -0.7686,
        employmentIsParttime = 0.7016,
        ageIn10to17 = -4.2393,
        ageIn26to35 = 0.6319,
        ageIn36to50 = 0.8019,
        areaTypeIsConurbation = -0.2717,
        householdAmountYouths = 0.6232,
        householdHasChildren = 1.4051,
        isMale = -0.0667,
    ),
    option5 = ServiceDayParameters(
        base = -3.9756,
        employmentIsFulltime = -0.9124,
        employmentIsParttime = 0.2978,
        ageIn10to17 = -4.6221,
        ageIn26to35 = 0.9999,
        ageIn36to50 = 1.0698,
        areaTypeIsConurbation = -0.2423,
        householdAmountYouths = 0.4820,
        householdHasChildren = 2.4615,
        isMale = -0.6287,
    ),
    option6 = ServiceDayParameters(
        base = -5.3297,
        employmentIsFulltime = -0.6603,
        employmentIsParttime = 0.6219,
        ageIn10to17 = -3.9789,
        ageIn26to35 = 1.0719,
        ageIn36to50 = 1.1898,
        areaTypeIsConurbation = -0.3103,
        householdAmountYouths = 0.7116,
        householdHasChildren = 1.6530,
        isMale = -0.5425,
    ),
    option7 = ServiceDayParameters(
        base = -6.5684,
        employmentIsFulltime = -1.3006,
        employmentIsParttime = 0.2101,
        ageIn10to17 = -11.4473,
        ageIn26to35 = 1.2377,
        ageIn36to50 = 1.4420,
        areaTypeIsConurbation = -0.3470,
        householdAmountYouths = 0.7588,
        householdHasChildren = 1.0888,
        isMale = -0.5573,
    )
)