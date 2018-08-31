package edu.kit.ifv.mobitopp.actitopp;

import java.util.ArrayList;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class UtilityFunction
{

  private double baseWeight;
  private ArrayList<UtilityParameterAttributeCombination> parameterattributeCombinations;
  
  public UtilityFunction()
  {
  	this.parameterattributeCombinations = new ArrayList<UtilityParameterAttributeCombination>();
  }
  
  public void setBaseWeight(double baseWeight)
  {
    this.baseWeight = baseWeight;
  }
  
  public void addParameterAttributeCombination (UtilityParameterAttributeCombination combination)
  {
  	parameterattributeCombinations.add(combination);
  }

  public double getUtility()
  {   
    double utility = 0.0 + baseWeight;
    for(UtilityParameterAttributeCombination pair : parameterattributeCombinations)
    {
      utility += (pair.getattributeValue() * pair.getparameterValue());
    }
    
    return utility;
  }
  
	public void printUtilityDetails()
  {
    System.out.print("Base utility: "+ baseWeight);
    for(UtilityParameterAttributeCombination pair : parameterattributeCombinations)
    {
      System.out.print(pair.getName() + ":" + pair.getattributeValue() + "*" + pair.getparameterValue());
      System.out.print(" __ ");
    }
    System.out.println("\nTOTAL UTILITY: " + getUtility());
  }
}
