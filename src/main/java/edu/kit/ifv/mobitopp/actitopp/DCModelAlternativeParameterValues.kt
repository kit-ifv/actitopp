package edu.kit.ifv.mobitopp.actitopp

/**
 * This class is basically a named map.Why? - Robin, 25
 */
class DCModelAlternativeParameterValues(private val parameterValues: MutableMap<String, Double> = mutableMapOf()) :
    Map<String, Double> by parameterValues {


    fun addParameterValue(parameterName: String, parameterValue: Double) {
        parameterValues[parameterName] = parameterValue
    }
}
