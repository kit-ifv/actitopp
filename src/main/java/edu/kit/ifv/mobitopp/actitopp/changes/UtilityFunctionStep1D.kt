package edu.kit.ifv.mobitopp.actitopp.changes

import edu.kit.ifv.mobitopp.actitopp.ActitoppPersonModifierFields
import edu.kit.ifv.mobitopp.actitopp.enums.AreaType
import edu.kit.ifv.mobitopp.actitopp.enums.Employment
import edu.kit.ifv.mobitopp.actitopp.enums.isEarning
import edu.kit.ifv.mobitopp.actitopp.enums.isStudent
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters

val ParameterSet1D = ParameterCollectionStep1D(
    option1 = ParametersStep1D(
        base = 0.9053,
        employmentIsEarning = 0.0556,
        emplomentStudent = -0.0939,
        employmentVocational = -0.1432,
        ageIn10To17 = -0.2341,
        areaTypeIsConurbation = 0.1679,
        areaTypeIsRural = -0.3859,
        amountOfWorkingDays = -0.0943,
        amountOfEducationDays = -0.2162,
        amountOfLeisureDays = 0.0807,
    ),
    option2 = ParametersStep1D(
        base = 1.6032,
        employmentIsEarning = 0.0908,
        emplomentStudent = -0.4189,
        employmentVocational = -0.7189,
        ageIn10To17 = -0.7975,
        areaTypeIsConurbation = 0.1636,
        areaTypeIsRural = -0.3414,
        amountOfWorkingDays = -0.2019,
        amountOfEducationDays = -0.3122,
        amountOfLeisureDays = 0.0950,
    ),
    option3 = ParametersStep1D(
        base = 1.8425,
        employmentIsEarning = -0.0876,
        emplomentStudent = -0.8263,
        employmentVocational = -0.9395,
        ageIn10To17 = -1.3385,
        areaTypeIsConurbation = 0.2795,
        areaTypeIsRural = -0.4664,
        amountOfWorkingDays = -0.2749,
        amountOfEducationDays = -0.4017,
        amountOfLeisureDays = 0.1456,
    ),
    option4 = ParametersStep1D(
        base = 1.8553,
        employmentIsEarning = -0.2713,
        emplomentStudent = -1.0863,
        employmentVocational = -1.0351,
        ageIn10To17 = -1.6907,
        areaTypeIsConurbation = 0.3847,
        areaTypeIsRural = -0.6626,
        amountOfWorkingDays = -0.3279,
        amountOfEducationDays = -0.5432,
        amountOfLeisureDays = 0.1704,
    ),
    option5 = ParametersStep1D(
        base = 1.6620,
        employmentIsEarning = -0.3127,
        emplomentStudent = -1.2827,
        employmentVocational = -1.2987,
        ageIn10To17 = -3.1522,
        areaTypeIsConurbation = 0.4411,
        areaTypeIsRural = -0.6480,
        amountOfWorkingDays = -0.3836,
        amountOfEducationDays = -0.5795,
        amountOfLeisureDays = 0.1608,
    ),
    option6 = ParametersStep1D(
        base = 0.9768,
        employmentIsEarning = -0.4578,
        emplomentStudent = -1.5374,
        employmentVocational = -2.7380,
        ageIn10To17 = -3.3079,
        areaTypeIsConurbation = 0.4446,
        areaTypeIsRural = -0.8533,
        amountOfWorkingDays = -0.4350,
        amountOfEducationDays = -0.5509,
        amountOfLeisureDays = 0.1825,
    ),
    option7 = ParametersStep1D(
        base = -0.5037,
        employmentIsEarning = -0.2544,
        emplomentStudent = -1.0175,
        employmentVocational = -0.9605,
        ageIn10To17 = -11.3719,
        areaTypeIsConurbation = 0.6513,
        areaTypeIsRural = -0.8707,
        amountOfWorkingDays = -0.4274,
        amountOfEducationDays = -0.5946,
        amountOfLeisureDays = 0.1052,
    )
)

data class ParameterCollectionStep1D(
    override val option1: ParametersStep1D,
    override val option2: ParametersStep1D,
    override val option3: ParametersStep1D,
    override val option4: ParametersStep1D,
    override val option5: ParametersStep1D,
    override val option6: ParametersStep1D,
    override val option7: ParametersStep1D,
) : ParameterCollectionStep1<ParametersStep1D>

data class ParametersStep1D(
    val base: Double,
    val employmentIsEarning: Double,
    val emplomentStudent: Double,
    val employmentVocational: Double,
    val ageIn10To17: Double,
    val areaTypeIsConurbation: Double,
    val areaTypeIsRural: Double,
    val amountOfWorkingDays: Double,
    val amountOfEducationDays: Double,
    val amountOfLeisureDays: Double,
)

val step1DModel = ModifiableDiscreteChoiceModel<Int, PersonSituation, ParameterCollectionStep1D>(AllocatedLogit.create {
    option(0) {
        0.0
    }
    option(1, parameters = { option1 }) { standardUtilityFunction(this, it) }
    option(2, parameters = { option2 }) { standardUtilityFunction(this, it) }
    option(3, parameters = { option3 }) { standardUtilityFunction(this, it) }
    option(4, parameters = { option4 }) { standardUtilityFunction(this, it) }
    option(5, parameters = { option5 }) { standardUtilityFunction(this, it) }
    option(6, parameters = { option6 }) { standardUtilityFunction(this, it) }
    option(7, parameters = { option7 }) { standardUtilityFunction(this, it) }
}
)
val step1DWithParams = step1DModel.initializeWithParameters(ParameterSet1D)
private val standardUtilityFunction: ParametersStep1D.(PersonSituation) -> Double = {
    base +
            (it.isEarningMoney()) * employmentIsEarning +
            (it.isStudent()) * emplomentStudent +
            (it.isVocational()) * employmentVocational +
            (it.areaTypeConurbation()) * areaTypeIsConurbation +
            (it.areaTypeRural()) * areaTypeIsRural +
            (it.isAged10To17()) * ageIn10To17 +
            it.amountOfWorkingDays() * amountOfWorkingDays +
            it.amountOfEducationDays() * amountOfEducationDays +
            it.amountOfLeisureDays() * amountOfLeisureDays
}
