package edu.kit.ifv.mobitopp.actitopp.changes

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.DiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.Logit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.UtilityFunction

data class BiggerParameterCollection(
    val option1: ParametersStepOne,
    val option2: ParametersStepOne,
    val option3: ParametersStepOne,
    val option4: ParametersStepOne,
    val option5: ParametersStepOne,
    val option6: ParametersStepOne,
    val option7: ParametersStepOne,
)

data class ParametersStepOne(
    val base: Double,
    val employmentFullTime: Double,
    val employmentPartTime: Double,
    val employmentStudent: Double,
    val employmentTrainee: Double,
    val genderIsMale: Double,
    val areaTypeConurburation: Double,
    val areaTypeRural: Double,
    val ageIn10to17: Double,
    val ageIn18To25: Double,
    val ageIn26To35: Double,
    val ageIn36To50: Double,
    val ageIn51to60: Double,
    val ageIn61to70: Double,
    val householdHasChildenBelowAge10: Double,
)
class Situation(override val choice: Int, val person: ActitoppPerson): ChoiceSituation<Int>() {
    val employment = person.employment

}
val step1Model = DiscreteChoiceModel<Int, Situation, BiggerParameterCollection>(AllocatedLogit.create {
    option(0) {
        0.0
    }
    option(1, parameters = {option1}, {standardUtilityFunction(this, it)})

})

val standardUtilityFunction:  ParametersStepOne.(Situation) -> Double = {
    base
}