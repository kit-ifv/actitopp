package edu.kit.ifv.mobitopp.actitopp.changes

import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters

val ParameterSet1E = ParameterCollectionStep1E(
    option1 = ParametersStep1E(
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
    option2 = ParametersStep1E(
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
    option3 = ParametersStep1E(
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
    option4 = ParametersStep1E(
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
    option5 = ParametersStep1E(
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
    option6 = ParametersStep1E(
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
    option7 = ParametersStep1E(
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

data class ParameterCollectionStep1E(
    override val option1: ParametersStep1E,
    override val option2: ParametersStep1E,
    override val option3: ParametersStep1E,
    override val option4: ParametersStep1E,
    override val option5: ParametersStep1E,
    override val option6: ParametersStep1E,
    override val option7: ParametersStep1E,
): ParameterCollectionStep1<ParametersStep1E>

data class ParametersStep1E(
    val base: Double,
    val employmentIsFulltime: Double,
    val employmentIsParttime: Double,

    val ageIn10to17: Double,
    val ageIn26to35: Double,
    val ageIn36to50: Double,
    val areaTypeIsConurbation: Double,
    val householdAmountYouths: Double,
    val householdHasChildren: Double,
    val isMale: Double
)
val step1EModel = ModifiableDiscreteChoiceModel<Int, PersonSituation, ParameterCollectionStep1E>(AllocatedLogit.create {
    option(0) {
        0.0
    }
    option(1, parameters = {option1}) { standardUtilityFunction(this, it)}
    option(2, parameters = {option2}) { standardUtilityFunction(this, it)}
    option(3, parameters = {option3}) { standardUtilityFunction(this, it)}
    option(4, parameters = {option4}) { standardUtilityFunction(this, it)}
    option(5, parameters = {option5}) { standardUtilityFunction(this, it)}
    option(6, parameters = {option6}) { standardUtilityFunction(this, it)}
    option(7, parameters = {option7}) { standardUtilityFunction(this, it)}
}
)
val step1EWithParams = step1EModel.initializeWithParameters(ParameterSet1E)
private val standardUtilityFunction:  ParametersStep1E.(PersonSituation) -> Double = {
    base +
            (it.isFulltimeEmployee()) * employmentIsFulltime +
            (it.isParttimeEmployee()) * employmentIsParttime +
            (it.isAged10To17()) * ageIn10to17 +
            (it.isAged26To35()) * ageIn26to35 +
            (it.isAged36To50()) * ageIn36to50 +
            (it.areaTypeConurbation()) * areaTypeIsConurbation +
            (it.hasChildrenInHousehold()) * householdHasChildren +
            (it.amountOfYouthsInHousehold()) * householdAmountYouths +
            (it.isMale()) * isMale

}
