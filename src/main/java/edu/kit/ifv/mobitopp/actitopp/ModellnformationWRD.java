package edu.kit.ifv.mobitopp.actitopp;

import java.util.HashMap;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class ModellnformationWRD {
	
  // contains all distribution information for different categories of the step
  private HashMap<String, ModelDistributionInformation> distributionInformation;
  
  public ModellnformationWRD()
  {
  	this.distributionInformation = new HashMap<String, ModelDistributionInformation>();
  }

  public void addDistributionInformation(String category, ModelDistributionInformation distribution)
  {
  	distributionInformation.put(category, distribution);
  }
  
  public ModelDistributionInformation getWRDDistribution(String category)
  {
  	return distributionInformation.get(category);
  }
  
  public HashMap<String, ModelDistributionInformation> getWRDDistributions()
  {
  	return distributionInformation;
  }

  
  
  
  

}
