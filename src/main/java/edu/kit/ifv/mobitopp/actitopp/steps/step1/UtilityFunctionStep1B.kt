package edu.kit.ifv.mobitopp.actitopp.steps.step1

import edu.kit.ifv.mobitopp.actitopp.ActitoppPersonModifierFields
import edu.kit.ifv.mobitopp.actitopp.steps.PersonSituation
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters

val ParameterSet1B = ParameterCollectionStep1B(
    option1 = ParametersStep1B(
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
    option2 = ParametersStep1B(
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
    option3 = ParametersStep1B(
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
    option4 = ParametersStep1B(
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
    option5 = ParametersStep1B(
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
    option6 = ParametersStep1B(
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
    option7 = ParametersStep1B(
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

data class ParameterCollectionStep1B(
    override val option1: ParametersStep1B,
    override val option2: ParametersStep1B,
    override val option3: ParametersStep1B,
    override val option4: ParametersStep1B,
    override val option5: ParametersStep1B,
    override val option6: ParametersStep1B,
    override val option7: ParametersStep1B,
): ParameterCollectionStep1<ParametersStep1B>

data class ParametersStep1B(
    val base: Double,
    val employmentIsEarning: Double,
    val emplomentStudent: Double,
    val employmentVocational: Double,
    val ageIn10to17: Double,
    val ageIn18To25: Double,
    val ageIn26To35: Double,
    val ageIn36To50: Double,
    val amountOfWorkingDays: Double
)

class Situation1B(override val choice: Int, modPerson: ActitoppPersonModifierFields): PersonSituation(choice, modPerson) {
    val employment = modPerson.original.employment
    val age = modPerson.original.age
    val amountOfWorkingDays = modPerson.amountOfWorkingDays
}

val step1BModel = ModifiableDiscreteChoiceModel<Int, PersonSituation, ParameterCollectionStep1B>(AllocatedLogit.create {
    option(0) {
        0.0
    }
    option(1, parameters = {option1}) { standardUtilityFunction(this, it) }
    option(2, parameters = {option2}) { standardUtilityFunction(this, it) }
    option(3, parameters = {option3}) { standardUtilityFunction(this, it) }
    option(4, parameters = {option4}) { standardUtilityFunction(this, it) }
    option(5, parameters = {option5}) { standardUtilityFunction(this, it) }
    option(6, parameters = {option6}) { standardUtilityFunction(this, it) }
    option(7, parameters = {option7}) { standardUtilityFunction(this, it) }
}
)

val step1BWithParams = step1BModel.initializeWithParameters(ParameterSet1B)

private val standardUtilityFunction:  ParametersStep1B.(PersonSituation) -> Double = {
    base +
            (it.isEarningMoney()) * employmentIsEarning +
            (it.isStudent()) * emplomentStudent +
            (it.isVocational()) * employmentVocational +
            (it.isAged10To17()) * ageIn10to17 +
            (it.isAged18To25()) * ageIn18To25 +
            (it.isAged26To35()) * ageIn26To35 +
            (it.isAged36To50()) * ageIn36To50 +
            it.amountOfWorkingDays() * amountOfWorkingDays
}
