package edu.kit.ifv.mobitopp.actitopp.utilityFunctions.deprecated

/**
 * represents a combination of parameter and attribute as an element of a utility function.
 *
 *
 * - name 						: name of the parameter
 * - parameterValue 	: value of the parameter. Is loaded from the file system at beginning of model execution process
 * ROBIN: Which means that this field can be read only
 * - attributevalue 	: value of the according attribute. Is determined at runtime depeding on actual person, day, tour or activity
 *
 * @author Tim Hilgert
 */
class UtilityParameterAttributeCombination(val name: String,
                                           private val parameterValue: Double = -99999.0,
                                           private var attributeValue: Double = -99999.0
) {


    fun getattributeValue(): Double {
        assert(attributeValue != -99999.0) { "attribute is not set correctly - actual value: $attributeValue" }
        return attributeValue
    }

    fun setattributevalue(attributevalue: Double) {
        this.attributeValue = attributevalue
    }

    fun getparameterValue(): Double {
        assert(parameterValue != -99999.0) { "attribute is not set correctly - actual value: $parameterValue" }
        return parameterValue
    }

    override fun toString(): String {
        return "$name // attributValue: $attributeValue // parameterValue: $parameterValue"
    }
}
