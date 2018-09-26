package edu.kit.ifv.mobitopp.actitopp;

import java.util.HashMap;
import java.util.Map;

public class LinRegDefaultCalculation {
	
	private String regressionname;
	private ModelFileBase fileBase;
	private AttributeLookup attributeLookup;
	
	Map<String, LinRegEstimate> estimatesMap;
	
	public LinRegDefaultCalculation(String regressionname, ModelFileBase fileBase, AttributeLookup attributeLookup)
	{
		this.regressionname = regressionname;
		this.fileBase = fileBase;
		this.attributeLookup = attributeLookup;
		this.estimatesMap = new HashMap<String, LinRegEstimate>();
	}
	
	public void initializeEstimates()
	{
		
		// copy the parameters loaded frome file base to the decision of this modeling step
		for (String key : fileBase.getLinearRegressionEstimates(regressionname).keySet())
		{
			LinRegEstimate fromFileBase = fileBase.getLinearRegressionEstimates(regressionname).get(key);
			LinRegEstimate estimate = new LinRegEstimate(fromFileBase.getName(), fromFileBase.getEstimateValue(), fromFileBase.getContextIdentifier());
			estimatesMap.put(key, estimate);
		}
		
		// read attribute values for estimates
		for (String key : estimatesMap.keySet())
		{
			LinRegEstimate estimate = estimatesMap.get(key);
			
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
	 * calculate the linear combination of the estimates and the according attributes
	 * 
	 * @return
	 */
	public double calculateRegression()
	{
		double result=0;
		
		// Estimates auslesen und Linearkombination bilden
		for (String key : estimatesMap.keySet())
		{
	  	LinRegEstimate estimate = estimatesMap.get(key);
			result = result + (estimate.getEstimateValue() * estimate.getAttributeValue());
		}
		
		return result;
	}
	

}
