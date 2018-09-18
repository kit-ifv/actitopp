package edu.kit.ifv.mobitopp.actitopp;

import java.util.HashMap;

public class DCModelAlternativeParameterValues {
	
	private HashMap<String, Double> parameterValues;
	
	public DCModelAlternativeParameterValues()
	{
		parameterValues = new HashMap<String, Double>();
	}
		
	public Double getParameterValue (String parameterName)
	{
		Double parameterValue = parameterValues.get(parameterName);
		assert parameterValue!=null : "could not read parameterValue for ParameterName " + parameterName;
		return parameterValue;
	}
	
	public HashMap<String, Double> getAllParameterValues()
	{
		assert parameterValues!=null : "parameterValues are null";
		return parameterValues;
	}
	
	public void addParameterValue(String parameterName, Double parameterValue)
	{
		parameterValues.put(parameterName, parameterValue);
	}
}
