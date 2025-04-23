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
import java.lang.reflect.Parameter

val ParameterSet1L = ParameterCollectionStep1L(
    option2 = ParametersStep1L(
        base = 0.3956,
        beruf_teilzeit = 0.4052,
        beruf_schueler = 0.3082,
        beruf_azubi = 0.2121,
        alter_26bis35 = 0.4286,
        alter_36bis50 = 0.5350,
        alter_51bis60 = 0.3188,
        alter_61bis70 = 0.3318,
        Raumtyp_mobitopp_rural = -0.5865,
        pendeln_ueber50km = -0.1666,
        pendeln_0bis5km = 0.2628,
        haushalthatkinderunter10 = 0.1804,
    ),
    option3 = ParametersStep1L(
        base = -0.9043,
        beruf_teilzeit = 0.6790,
        beruf_schueler = 0.2746,
        beruf_azubi = 0.1893,
        alter_26bis35 = 1.1473,
        alter_36bis50 = 1.1567,
        alter_51bis60 = 0.6968,
        alter_61bis70 = 0.6427,
        Raumtyp_mobitopp_rural = -0.6093,
        pendeln_ueber50km = -0.4316,
        pendeln_0bis5km = 0.3914,
        haushalthatkinderunter10 = 0.5053,
    ),
    option4 = ParametersStep1L(
        base = -2.5651,
        beruf_teilzeit = 0.8759,
        beruf_schueler = 0.2732,
        beruf_azubi = 0.9468,
        alter_26bis35 = 1.6813,
        alter_36bis50 = 1.7647,
        alter_51bis60 = 1.1197,
        alter_61bis70 = 0.8423,
        Raumtyp_mobitopp_rural = -1.0050,
        pendeln_ueber50km = -0.4089,
        pendeln_0bis5km = 0.4109,
        haushalthatkinderunter10 = 0.6865,
    ),
    option5 = ParametersStep1L(
        base = -3.7358,
        beruf_teilzeit = 0.9369,
        beruf_schueler = 0.3443,
        beruf_azubi = 0.5735,
        alter_26bis35 = 1.8968,
        alter_36bis50 = 1.7846,
        alter_51bis60 = 1.0495,
        alter_61bis70 = 0.5129,
        Raumtyp_mobitopp_rural = -0.3769,
        pendeln_ueber50km = -0.3099,
        pendeln_0bis5km = 0.2829,
        haushalthatkinderunter10 = 0.6870,
    ),
    option6 = ParametersStep1L(
        base = -4.6359,
        beruf_teilzeit = 0.9776,
        beruf_schueler = 0.5584,
        beruf_azubi = 0.3049,
        alter_26bis35 = 2.0686,
        alter_36bis50 = 1.8213,
        alter_51bis60 = 1.4204,
        alter_61bis70 = 0.4929,
        Raumtyp_mobitopp_rural = -0.0415,
        pendeln_ueber50km = -0.1946,
        pendeln_0bis5km = 0.2037,
        haushalthatkinderunter10 = 0.7003,
    ),
)

data class ParameterCollectionStep1L(

    val option2: ParametersStep1L,
    val option3: ParametersStep1L,
    val option4: ParametersStep1L,
    val option5: ParametersStep1L,
    val option6: ParametersStep1L,

)

data class ParametersStep1L(
    val base: Double,
    val beruf_teilzeit: Double, 
    val beruf_schueler: Double, 
    val beruf_azubi: Double, 
    val alter_26bis35: Double, 
    val alter_36bis50: Double, 
    val alter_51bis60: Double, 
    val alter_61bis70: Double, 
    val Raumtyp_mobitopp_rural: Double, 
    val pendeln_ueber50km: Double, 
    val pendeln_0bis5km: Double, 
    val haushalthatkinderunter10: Double, 

)


val step1LModel = ModifiableDiscreteChoiceModel<Int, PersonSituation, ParameterCollectionStep1L>(AllocatedLogit.create {
    option(1) {
        0.0
    }
    option(2, parameters = {option2}) { standardUtilityFunction(this, it)}
    option(3, parameters = {option3}) { standardUtilityFunction(this, it)}
    option(4, parameters = {option4}) { standardUtilityFunction(this, it)}
    option(5, parameters = {option5}) { standardUtilityFunction(this, it)}
    option(6, parameters = {option6}) { standardUtilityFunction(this, it)}
}
)

val step1LWithParams = step1LModel.initializeWithParameters(ParameterSet1L)

private val standardUtilityFunction:  ParametersStep1L.(PersonSituation) -> Double = {
    base +
            (it.isParttimeEmployee()) * beruf_teilzeit+
                (it.isStudent()) * beruf_schueler+
                (it.isVocational()) * beruf_azubi+
                (it.isAged26To35()) * alter_26bis35+
                (it.isAged36To50()) * alter_36bis50+
                (it.isAged51To60()) * alter_51bis60+
                (it.isAged61To70()) * alter_61bis70+
                (it.areaTypeRural()) * Raumtyp_mobitopp_rural+
                (it.commuteOver50km()) * pendeln_ueber50km+
                (it.commuteIn0To5km()) * pendeln_0bis5km+
                (it.hasChildrenInHousehold()) * haushalthatkinderunter10

}
