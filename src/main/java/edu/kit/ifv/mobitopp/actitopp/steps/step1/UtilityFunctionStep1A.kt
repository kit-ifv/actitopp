package edu.kit.ifv.mobitopp.actitopp.steps.step1

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.enums.AreaType
import edu.kit.ifv.mobitopp.actitopp.enums.Employment
import edu.kit.ifv.mobitopp.actitopp.enums.Gender
import edu.kit.ifv.mobitopp.actitopp.enums.isParttime
import edu.kit.ifv.mobitopp.actitopp.enums.isStudent
import edu.kit.ifv.mobitopp.actitopp.steps.PersonSituation
import edu.kit.ifv.mobitopp.actitopp.toModifiable
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.D
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.I
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters

interface ParameterCollectionStep1<T> {
    val option1: T
    val option2: T
    val option3: T
    val option4: T
    val option5: T
    val option6: T
    val option7: T
}
data class ParameterCollectionStep1A(
    override val option1: ParametersStep1A,
    override val option2: ParametersStep1A,
    override val option3: ParametersStep1A,
    override val option4: ParametersStep1A,
    override val option5: ParametersStep1A,
    override val option6: ParametersStep1A,
    override val option7: ParametersStep1A,
): ParameterCollectionStep1<ParametersStep1A>
val ParameterSet1A = ParameterCollectionStep1A(
    option1 = ParametersStep1A(
        base = -2.6967,
        employmentFullTime = 1.3924,
        employmentPartTime = 1.9951,
        employmentStudent = 0.4496,
        employmentVocational = 0.4207,
        ageIn10to17 = -0.6668,
        ageIn18To25 = 0.5526,
        ageIn26To35 = 0.8903,
        ageIn36To50 = 0.4840,
        ageIn51to60 = 0.2741,
        ageIn61to70 = 0.0507,
        areaTypeConurburation = 0.0963,
        areaTypeRural = 0.2612,
        householdHasChildenBelowAge10 = -0.2274,
        genderIsMale = 0.2584,
    ),
    option2 = ParametersStep1A(
        base = -3.7312,
        employmentFullTime = 2.2435,
        employmentPartTime = 3.2298,
        employmentStudent = 0.4710,
        employmentVocational = 0.5813,
        ageIn10to17 = -1.3165,
        ageIn18To25 = 1.0806,
        ageIn26To35 = 1.3150,
        ageIn36To50 = 1.0986,
        ageIn51to60 = 0.4613,
        ageIn61to70 = 0.2806,
        areaTypeConurburation = 0.0951,
        areaTypeRural = -0.3103,
        householdHasChildenBelowAge10 = -0.2287,
        genderIsMale = 0.3238,
    ),
    option3 = ParametersStep1A(
        base = -4.9095,
        employmentFullTime = 3.2366,
        employmentPartTime = 3.9843,
        employmentStudent = 0.7842,
        employmentVocational = 1.4839,
        ageIn10to17 = -1.3236,
        ageIn18To25 = 1.3074,
        ageIn26To35 = 2.0831,
        ageIn36To50 = 1.9625,
        ageIn51to60 = 1.3826,
        ageIn61to70 = 0.7616,
        areaTypeConurburation = 0.2233,
        areaTypeRural = -0.3370,
        householdHasChildenBelowAge10 = -0.5606,
        genderIsMale = 0.3756,
    ),
    option4 = ParametersStep1A(
        base = -5.2542,
        employmentFullTime = 4.5024,
        employmentPartTime = 4.5377,
        employmentStudent = 1.2901,
        employmentVocational = 2.5183,
        ageIn10to17 = -1.0378,
        ageIn18To25 = 1.2678,
        ageIn26To35 = 1.9770,
        ageIn36To50 = 1.9111,
        ageIn51to60 = 1.3704,
        ageIn61to70 = 0.5493,
        areaTypeConurburation = 0.2128,
        areaTypeRural = 0.1159,
        householdHasChildenBelowAge10 = -0.5594,
        genderIsMale = 0.2290,
    ),
    option5 = ParametersStep1A(
        base = -5.2060,
        employmentFullTime = 5.4192,
        employmentPartTime = 4.5107,
        employmentStudent = 1.1691,
        employmentVocational = 2.1640,
        ageIn10to17 = -0.7940,
        ageIn18To25 = 1.7500,
        ageIn26To35 = 2.1937,
        ageIn36To50 = 2.2435,
        ageIn51to60 = 1.6490,
        ageIn61to70 = 0.5787,
        areaTypeConurburation = 0.1144,
        areaTypeRural = 0.2653,
        householdHasChildenBelowAge10 = -0.4192,
        genderIsMale = 0.3755,
    ),
    option6 = ParametersStep1A(
        base = -5.7256,
        employmentFullTime = 4.9968,
        employmentPartTime = 4.2828,
        employmentStudent = 0.8078,
        employmentVocational = 1.3241,
        ageIn10to17 = -1.9561,
        ageIn18To25 = 0.9081,
        ageIn26To35 = 1.4656,
        ageIn36To50 = 1.3610,
        ageIn51to60 = 0.7854,
        ageIn61to70 = -0.0174,
        areaTypeConurburation = -0.2214,
        areaTypeRural = 0.5503,
        householdHasChildenBelowAge10 = -0.7847,
        genderIsMale = 0.6314,
    ),
    option7 = ParametersStep1A(
        base = -18.3579,
        employmentFullTime = 4.8243,
        employmentPartTime = 4.3736,
        employmentStudent = -10.1760,
        employmentVocational = 2.1563,
        ageIn10to17 = 1.1626,
        ageIn18To25 = 12.1348,
        ageIn26To35 = 12.5538,
        ageIn36To50 = 12.6949,
        ageIn51to60 = 12.2650,
        ageIn61to70 = 11.5783,
        areaTypeConurburation = -0.3178,
        areaTypeRural = 0.5613,
        householdHasChildenBelowAge10 = -0.7393,
        genderIsMale = 0.0489,
    )
)

/**
 * @param base originally called "Grundnutzen"
 * @param employmentFullTime originally called "beruf_vollzeit"
 * @param employmentPartTime originally called "beruf_teilzeit"
 * @param
 */
data class ParametersStep1A(
    val base: Double,
    val employmentFullTime: Double,
    val employmentPartTime: Double,
    val employmentStudent: Double,
    val employmentVocational: Double,
    val ageIn10to17: Double,
    val ageIn18To25: Double,
    val ageIn26To35: Double,
    val ageIn36To50: Double,
    val ageIn51to60: Double,
    val ageIn61to70: Double,   
    val areaTypeConurburation: Double,
    val areaTypeRural: Double,
    val householdHasChildenBelowAge10: Double,
    val genderIsMale: Double,
)
// TODO move these to a util definition
inline operator fun Boolean.times(other: Double): Double = this.D * other
inline operator fun Boolean.times(other: Int): Int = this.I * other
class Situation1A(override val choice: Int, person: ActitoppPerson): PersonSituation(choice, person.toModifiable()) {
    val employment = person.employment
    val age = person.age
    val areaType = person.areatype
    val hasChildrenUnder10 = person.children0_10 > 0
    val gender = person.gender
}

val step1AModel = ModifiableDiscreteChoiceModel<Int, PersonSituation, ParameterCollectionStep1A>(AllocatedLogit.create {

    option(1, parameters = {option1}, { standardUtilityFunction(this, it) })
    option(2, parameters = {option2}, { standardUtilityFunction(this, it) })
    option(3, parameters = {option3}, { standardUtilityFunction(this, it) })
    option(4, parameters = {option4}, { standardUtilityFunction(this, it) })
    option(5, parameters = {option5}, { standardUtilityFunction(this, it) })
    option(6, parameters = {option6}, { standardUtilityFunction(this, it) })
    option(7, parameters = {option7}, { standardUtilityFunction(this, it) })
    option(0) {
        0.0
    }
    
})
val step1AWithParams = step1AModel.initializeWithParameters(ParameterSet1A)
private val standardUtilityFunction:  ParametersStep1A.(PersonSituation) -> Double = {
    base +
            (it.isFulltimeEmployee()) * employmentFullTime +
            (it.isParttimeEmployee()) * employmentPartTime +
            (it.isStudent()) * employmentStudent +
            (it.isVocational()) * employmentVocational+

            (it.isAged10To17()) * ageIn10to17 +
            (it.isAged18To25()) * ageIn18To25 +
            (it.isAged26To35()) * ageIn26To35 +
            (it.isAged36To50()) * ageIn36To50 +
            (it.isAged51To60()) * ageIn51to60 +
            (it.isAged61To70()) * ageIn61to70 +

            (it.areaTypeConurbation()) * areaTypeConurburation +
            (it.areaTypeRural()) * areaTypeRural +
            (it.hasChildrenInHousehold()) * householdHasChildenBelowAge10 +
            (it.isMale()) * genderIsMale
}
