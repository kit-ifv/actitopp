package edu.kit.ifv.mobitopp.actitopp.changes

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.enums.Employment
import edu.kit.ifv.mobitopp.actitopp.enums.isParttime
import edu.kit.ifv.mobitopp.actitopp.enums.isStudent
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.D
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.DiscreteChoiceModel

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
    val employmentVocational: Double,
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
inline operator fun Boolean.times(other: Double): Double = this.D * other
class Situation(override val choice: Int, val person: ActitoppPerson): ChoiceSituation<Int>() {
    val employment = person.employment
    val age = person.age

}
val step1Model = DiscreteChoiceModel<Int, Situation, BiggerParameterCollection>(AllocatedLogit.create {
    option(0) {
        0.0
    }
    option(1, parameters = {option1}, {standardUtilityFunction(this, it)})

})

val standardUtilityFunction:  ParametersStepOne.(Situation) -> Double = {
    base +
            (it.employment == Employment.FULLTIME) * employmentFullTime +
            (it.employment.isParttime()) * employmentPartTime +
            (it.employment.isStudent()) * employmentStudent +
            (it.employment == Employment.VOCATIONAL) * employmentVocational+

            (it.age in 10..17) * ageIn10to17 +
            (it.age in 18..25) * ageIn18To25 +
            (it.age in 26..35) * ageIn26To35 +
            (it.age in 36..50) * ageIn36To50 +
            (it.age in 51..60) * ageIn51to60 +
            (it.age in 61..70) * ageIn61to70
}