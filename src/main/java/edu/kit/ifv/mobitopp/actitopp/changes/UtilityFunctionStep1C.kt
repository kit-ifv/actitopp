package edu.kit.ifv.mobitopp.actitopp.changes

import edu.kit.ifv.mobitopp.actitopp.ActitoppPersonModifierFields
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel

val ParameterSet1C = ParameterCollectionStep1C(
    option1 = ParametersStep1C(
        base = 0.7714,
        employmentIsEarning = -0.1966,
        emplomentStudent = -0.2525,
        ageIn61To70 = 0.0147,
        areaTypeIsRural = -0.3753,
        householdHasChildenBelowAge10 = 0.2130,
        amountOfWorkingDays = -0.00570,
    ),
    option2 = ParametersStep1C(
        base = 1.0050,
        employmentIsEarning = -0.0281,
        emplomentStudent = 0.0364,
        ageIn61To70 = 0.1588,
        areaTypeIsRural = -0.4818,
        householdHasChildenBelowAge10 = 0.3640,
        amountOfWorkingDays = -0.0381,
    ),
    option3 = ParametersStep1C(
        base = 1.3068,
        employmentIsEarning = -0.2120,
        emplomentStudent = -0.0352,
        ageIn61To70 = 0.1633,
        areaTypeIsRural = -0.5532,
        householdHasChildenBelowAge10 = 0.3833,
        amountOfWorkingDays = -0.0663,
    ),
    option4 = ParametersStep1C(
        base = 1.3841,
        employmentIsEarning = -0.2480,
        emplomentStudent = -0.1466,
        ageIn61To70 = 0.2794,
        areaTypeIsRural = -0.8125,
        householdHasChildenBelowAge10 = 0.3782,
        amountOfWorkingDays = -0.1161,
    ),
    option5 = ParametersStep1C(
        base = 1.2402,
        employmentIsEarning = -0.3081,
        emplomentStudent = 0.0997,
        ageIn61To70 = 0.3593,
        areaTypeIsRural = -1.0029,
        householdHasChildenBelowAge10 = 0.2191,
        amountOfWorkingDays = -0.1402,
    ),
    option6 = ParametersStep1C(
        base = 1.0663,
        employmentIsEarning = -0.4165,
        emplomentStudent = 0.0254,
        ageIn61To70 = 0.4184,
        areaTypeIsRural = -1.3017,
        householdHasChildenBelowAge10 = 0.1361,
        amountOfWorkingDays = -0.1811,
    ),
    option7 = ParametersStep1C(
        base = 0.9369,
        employmentIsEarning = -0.5151,
        emplomentStudent = -0.5478,
        ageIn61To70 = 0.4617,
        areaTypeIsRural = -0.9691,
        householdHasChildenBelowAge10 = -0.0764,
        amountOfWorkingDays = -0.2329,
    )
)

data class ParameterCollectionStep1C(
    override val option1: ParametersStep1C,
    override val option2: ParametersStep1C,
    override val option3: ParametersStep1C,
    override val option4: ParametersStep1C,
    override val option5: ParametersStep1C,
    override val option6: ParametersStep1C,
    override val option7: ParametersStep1C,
): ParameterCollectionStep1<ParametersStep1C>

data class ParametersStep1C(
    val base: Double,
    val employmentIsEarning: Double,
    val emplomentStudent: Double,
    val ageIn61To70: Double,
    val areaTypeIsRural: Double,
    val householdHasChildenBelowAge10: Double,
    val amountOfWorkingDays: Double
)

class Situation1C(override val choice: Int, modPerson: ActitoppPersonModifierFields): PersonSituation(choice, modPerson) {
    val employment = modPerson.original.employment
    val age = modPerson.original.age

}

val step1CModel = ModifiableDiscreteChoiceModel<Int, Situation1C, ParameterCollectionStep1C>(AllocatedLogit.create {
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

private val standardUtilityFunction:  ParametersStep1C.(Situation1C) -> Double = {
    base +
            (it.isEarningMoney()) * employmentIsEarning +
            (it.isStudent()) * emplomentStudent +
            (it.areaTypeRural()) * areaTypeIsRural +
            (it.hasChildrenInHousehold()) * householdHasChildenBelowAge10 +
            (it.isAged61To70()) * ageIn61To70 +
            it.amountOfWorkingDays() * amountOfWorkingDays
}
