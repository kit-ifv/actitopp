package edu.kit.ifv.mobitopp.actitopp;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class LCUtilityFunction implements UtilityFunction
{

  private double baseWeight;
  private List<ModelParameterWeight> utilityPairs;
  
  public LCUtilityFunction()
  {
    this(0f);
  }
  
  public LCUtilityFunction(double baseWeight)
  {
    this.baseWeight = baseWeight;
    this.utilityPairs = new ArrayList<ModelParameterWeight>();
  }
  
  public List<ModelParameterWeight> getUtilityPairs()
  {
    return utilityPairs;
  }

  public void setUtilityPairs(List<ModelParameterWeight> utilityPairs)
  {
    this.utilityPairs = utilityPairs;
  }

  public double getBaseWeight()
  {
    return baseWeight;
  }

  public void setBaseWeight(double baseWeight)
  {
    this.baseWeight = baseWeight;
  }
  
  public ModelParameterWeight getUtilityPairByName(String name)
  {
    for(ModelParameterWeight pair : utilityPairs)
    {
      if(pair.getName().equals(name)) return pair;
    }
    
    return null;
  }

  @Override
  public double getUtility()
  {   
    double utility = 0.0 + baseWeight;
    for(ModelParameterWeight pair : utilityPairs)
    {
      utility += (pair.getattributevalue() * pair.getWeight());
    }
    
    return utility;
  }
}
