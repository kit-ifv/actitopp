package edu.kit.ifv.mobitopp.actitopp.changes

import edu.kit.ifv.mobitopp.actitopp.ActitoppPersonModifierFields
import edu.kit.ifv.mobitopp.actitopp.enums.AreaType
import edu.kit.ifv.mobitopp.actitopp.enums.Employment
import edu.kit.ifv.mobitopp.actitopp.enums.Gender
import edu.kit.ifv.mobitopp.actitopp.enums.isEarning
import edu.kit.ifv.mobitopp.actitopp.enums.isNotEarning
import edu.kit.ifv.mobitopp.actitopp.enums.isStudent
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters

val ParameterSet1F = ParameterCollectionStep1F(
    option1 = ParametersStep1F(
        base = 3.5739,
        employmentNotEarning = -0.3249,
        employmentRetired = -0.3439,
        ageIn18To25 = 0.3145,
        amountOfYouths = -0.1346,
        isMale = -0.2760,
        amountOfWorkingDays = -0.3952,
        amountOfEducationDays = -0.2284,
        amountOfLeisureDays = -0.5973,
        amountOfServiceDays = -0.3244,
    ),
    option2 = ParametersStep1F(
        base = 7.6320,
        employmentNotEarning = -0.7489,
        employmentRetired = -0.6659,
        ageIn18To25 = 0.3366,
        amountOfYouths = -0.2981,
        isMale = -0.4090,
        amountOfWorkingDays = -1.0750,
        amountOfEducationDays = -0.7947,
        amountOfLeisureDays = -1.1970,
        amountOfServiceDays = -0.9637,
    ),
    option3 = ParametersStep1F(
        base = 11.8825,
        employmentNotEarning = -1.1891,
        employmentRetired = -1.1118,
        ageIn18To25 = 0.8478,
        amountOfYouths = -0.5397,
        isMale = -0.2338,
        amountOfWorkingDays = -2.0793,
        amountOfEducationDays = -1.8776,
        amountOfLeisureDays = -1.9709,
        amountOfServiceDays = -1.8096,
    ),
    option4 = ParametersStep1F(
        base = 15.1575,
        employmentNotEarning = -1.5659,
        employmentRetired = -1.4033,
        ageIn18To25 = 0.8321,
        amountOfYouths = -0.4287,
        isMale = -0.1296,
        amountOfWorkingDays = -2.9798,
        amountOfEducationDays = -2.6430,
        amountOfLeisureDays = -2.7910,
        amountOfServiceDays = -2.8184,
    ),
    option5 = ParametersStep1F(
        base = 18.9161,
        employmentNotEarning = -1.4267,
        employmentRetired = -1.5383,
        ageIn18To25 = 1.5662,
        amountOfYouths = -0.7466,
        isMale = -0.1758,
        amountOfWorkingDays = -4.6381,
        amountOfEducationDays = -4.1567,
        amountOfLeisureDays = -4.1593,
        amountOfServiceDays = -4.4475,
    ),
    option6 = ParametersStep1F(
        base = 22.0357,
        employmentNotEarning = -2.4041,
        employmentRetired = -2.0068,
        ageIn18To25 = 0.6195,
        amountOfYouths = -1.0575,
        isMale = -0.2178,
        amountOfWorkingDays = -5.9946,
        amountOfEducationDays = -6.0716,
        amountOfLeisureDays = -5.9283,
        amountOfServiceDays = -6.6534,
    ),
    option7 = ParametersStep1F(
        base = 21.1712,
        employmentNotEarning = -8.8502,
        employmentRetired = -14.6527,
        ageIn18To25 = -9.3926,
        amountOfYouths = -0.4333,
        isMale = 12.4611,
        amountOfWorkingDays = -12.3041,
        amountOfEducationDays = -13.9196,
        amountOfLeisureDays = -25.1790,
        amountOfServiceDays = -25.2436,
    )
)

data class ParameterCollectionStep1F(
    override val option1: ParametersStep1F,
    override val option2: ParametersStep1F,
    override val option3: ParametersStep1F,
    override val option4: ParametersStep1F,
    override val option5: ParametersStep1F,
    override val option6: ParametersStep1F,
    override val option7: ParametersStep1F,
): ParameterCollectionStep1<ParametersStep1F>

data class ParametersStep1F(
    val base: Double,
    val employmentNotEarning: Double,
    val employmentRetired: Double,
    val ageIn18To25: Double,
    val amountOfYouths: Double,
    val isMale: Double,
    val amountOfWorkingDays: Double,
    val amountOfEducationDays: Double,
    val amountOfLeisureDays: Double,
    val amountOfServiceDays: Double,
)


val step1FModel = ModifiableDiscreteChoiceModel<Int, PersonSituation, ParameterCollectionStep1F>(AllocatedLogit.create {
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

val step1FWithParams = step1FModel.initializeWithParameters(ParameterSet1F)

private val standardUtilityFunction:  ParametersStep1F.(PersonSituation) -> Double = {
    base +
            (it.isNotEarningMoney()) * employmentNotEarning +
            (it.isRetired()) * employmentRetired +
            (it.isAged18To25()) * ageIn18To25 +
            it.amountOfYouthsInHousehold() * amountOfYouths +
            (it.isMale()) * isMale +
            it.amountOfWorkingDays() * amountOfWorkingDays +
            it.amountOfEducationDays() * amountOfEducationDays +
            it.amountOfLeisureDays() * amountOfLeisureDays +
            it.amountOfServiceDays() * amountOfServiceDays
}
