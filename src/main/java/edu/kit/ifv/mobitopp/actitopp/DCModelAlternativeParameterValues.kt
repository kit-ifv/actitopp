package edu.kit.ifv.mobitopp.actitopp

class DCModelAlternativeParameterValues {
    private val parameterValues = HashMap<String, Double>()

    fun getParameterValue(parameterName: String): Double {
        val parameterValue =
            checkNotNull(parameterValues[parameterName]) { "could not read parameterValue for ParameterName $parameterName" }
        return parameterValue
    }

    val allParameterValues: HashMap<String, Double>
        get() {
            checkNotNull(parameterValues) { "parameterValues are null" }
            return parameterValues
        }

    fun addParameterValue(parameterName: String, parameterValue: Double) {
        parameterValues[parameterName] = parameterValue
    }
}
