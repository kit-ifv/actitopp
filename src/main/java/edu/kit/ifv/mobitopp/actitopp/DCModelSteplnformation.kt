package edu.kit.ifv.mobitopp.actitopp


/**
 * @author Tim Hilgert
 *
 *
 * object to handle the model flow information of each model step
 * contains relevant parameters with their context of the step and alternatives
 */
class DCModelSteplnformation {
    // contains all parameters for the specific model step
    private lateinit var parameterNamesContexts: HashMap<String, String>

    // contains all alternative for the specific model step
    lateinit var alternativesList: ArrayList<String>

    // contains all possible alternatives including parameter values
    lateinit var alternativesParameters: Map<String, DCModelAlternativeParameterValues>


    /**
     * @return the alternativesList
     */
    fun getAlternativesListDeprecated(): List<String> {
        return alternativesList
    }

    /**
     * @return the parameterNamesContexts
     */
    fun getParameterNamesContexts(): Map<String, String> {

        return parameterNamesContexts
    }

    fun getParameterValuesforAlternativeDepre(alternativeName: String): HashMap<String, Double> {
        return alternativesParameters[alternativeName]?.allParameterValues ?: HashMap()
    }

    fun getContextforParameterDepre(parameterName: String): String? {
        return parameterNamesContexts[parameterName]
    }

    /**
     * @return the alternativesParameters
     */
    fun getAlternativesParametersDepre(): Map<String, DCModelAlternativeParameterValues> {

        return alternativesParameters
    }


    /**
     * @param alternativesParameters the alternativesParameters to set
     */
    fun setAlternativesParametersDepre(alternativesParameters: Map<String, DCModelAlternativeParameterValues>) {
        this.alternativesParameters = alternativesParameters
    }


    /**
     * @param parameterNamesContexts the parameterNamesContexts to set
     */
    fun setParameterNamesContextsDepre(parameterNamesContexts: HashMap<String, String>) {
        this.parameterNamesContexts = parameterNamesContexts
    }


    /**
     * @param alternativesList the alternativesList to set
     */
    fun setAlternativesListDepre(alternativesList: ArrayList<String>) {
        this.alternativesList = alternativesList
    }
}
