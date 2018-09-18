package edu.kit.ifv.mobitopp.actitopp;

import java.util.HashMap;

/**
 * 
 * @author Tim Hilgert
 * 
 * object stores all distribution information of different categories (e.g. time classes) for a specific model step
 * needed to make a weighted random draw (wrd) decision
 *
 */
public class WRDModelSteplnformation {
	
  // contains all distribution information for different categories of the step
  private HashMap<String, WRDModelDistributionInformation> distributionInformation;
  
  /**
   * 
   * constructor
   *
   */
  public WRDModelSteplnformation()
  {
  	this.distributionInformation = new HashMap<String, WRDModelDistributionInformation>();
  }

  /**
   * 
   * method to add distribution information for a step loaded from the file system
   * 
   * @param category
   * @param distribution
   */
  public void addDistributionInformation(String category, WRDModelDistributionInformation distribution)
  {
  	distributionInformation.put(category, distribution);
  }
  
  /**
   * 
   * method to get distribution information of a specified category
   * 
   * @param category
   * @return
   */
  public WRDModelDistributionInformation getWRDDistribution(String category)
  {
  	return distributionInformation.get(category);
  }
  

}
