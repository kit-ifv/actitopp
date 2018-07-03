package edu.kit.ifv.mobitopp.actitopp;

import java.util.HashMap;
import java.util.Map;

public class DefaultLinearRegressionCalculation {
	
	private String regressionname;
	private ModelFileBase fileBase;
	private AttributeLookup attributeLookup;
	
	Map<String, LinearRegressionEstimate> estimatesMap;
	
	public DefaultLinearRegressionCalculation(String regressionname, ModelFileBase fileBase, AttributeLookup attributeLookup)
	{
		this.regressionname = regressionname;
		this.fileBase = fileBase;
		this.attributeLookup = attributeLookup;
		this.estimatesMap = new HashMap<String, LinearRegressionEstimate>();
	}
	
	/**
	 * 
	 * Initialisierung der Estimates mit Belegung der Attributwerte
	 * 
	 */
	public void initializeEstimates()
	{
		
		// Parameter laden und in Map für diese Entscheidung kopieren
		for (String key : fileBase.getLinearRegressionEstimates(regressionname).keySet())
		{
			estimatesMap.put(key, fileBase.getLinearRegressionEstimates(regressionname).get(key));
		}
		
		// Attributwerte auslesen
		for (String key : estimatesMap.keySet())
		{
	  	
			LinearRegressionEstimate estimate = estimatesMap.get(key);
			
			if (key.equals("Grundnutzen") || key.equals("Intercept"))
			{
				estimate.setAttributeValue(1);
			}
			else 
			{
				double attributeValue = 0;
				attributeValue = attributeLookup.getAttributeValue(estimate.getContextIdentifier(), estimate.getName());
        estimate.setAttributeValue(attributeValue);
			}
		}
	}
	
	/**
	 * 
	 * Berechnung der Linearkombination der Regression
	 * 
	 * @return
	 */
	public double calculateRegression()
	{
		double result=0;
		
		// Estimates auslesen und Linearkombination bilden
		for (String key : estimatesMap.keySet())
		{
	  	LinearRegressionEstimate estimate = estimatesMap.get(key);
			result = result + (estimate.getEstimateValue() * estimate.getAttributeValue());
		}
		
		return result;
	}
	

}
