package edu.kit.ifv.mobitopp.actitopp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Tim Hilgert
 *
 * object to handle the model flow information of each model step
 * contains relevant parameters with their context of the step and alternatives
 */
public class ModellnformationDC
{
	// contains all parameters for the specific model step
  private HashMap<String, String> parameterNamesContexts;
  // contains all alternative for the specific model step
  private ArrayList<String> alternativesList;
  // contains all possible alternatives including parameter values
  private Map<String, ModelAlternativeParameterValues> alternativesParameters;
  
  

  public ModellnformationDC()
  {}


	/**
	 * @return the alternativesList
	 */
  public List<String> getAlternativesList()
  {
  	assert alternativesList!=null : "alternativesList is null";
    return alternativesList;
  }

	/**
	 * @return the parameterNamesContexts
	 */
	public Map<String, String> getParameterNamesContexts()
	{
		assert parameterNamesContexts!=null : "parameterNamesContexts is null";
		return parameterNamesContexts;
	}
	
	public HashMap<String,Double> getParameterValuesforAlternative(String alternativeName)
	{
		return alternativesParameters.get(alternativeName).getAllParameterValues();
	}

	public String getContextforParameter(String parameterName)
	{
		return parameterNamesContexts.get(parameterName);
	}

	/**
	 * @return the alternativesParameters
	 */
	public Map<String, ModelAlternativeParameterValues> getAlternativesParameters() 
	{
		assert alternativesParameters!=null : "alternativesParameters is null";
		return alternativesParameters;
	}


	/**
	 * @param alternativesParameters the alternativesParameters to set
	 */
	public void setAlternativesParameters(Map<String, ModelAlternativeParameterValues> alternativesParameters) {
		this.alternativesParameters = alternativesParameters;
	}


	/**
	 * @param parameterNamesContexts the parameterNamesContexts to set
	 */
	public void setParameterNamesContexts(HashMap<String, String> parameterNamesContexts) {
		this.parameterNamesContexts = parameterNamesContexts;
	}


	/**
	 * @param alternativesList the alternativesList to set
	 */
	public void setAlternativesList(ArrayList<String> alternativesList) {
		this.alternativesList = alternativesList;
	}
	


}
