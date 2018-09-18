package edu.kit.ifv.mobitopp.actitopp;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;


public class WRDDiscreteDistribution
{

	/*
	 * map to store distributionelements and their amounts
	 */
	private TreeMap<Integer, Integer> distributionelements;


  /**
   * 
   * creates a new object that represents the distribution used for the model step
   *
   * @param distributioninformation
   */
  public WRDDiscreteDistribution(WRDModelDistributionInformation distributioninformation) 
	{
		distributionelements = new TreeMap<Integer, Integer>();
		
		// read all elements from distributioninformation from model file base and add them to the distribution of the concrete modeling
	  for (Entry<Integer, Integer> elements : distributioninformation.getDistributionElements().entrySet())
	  {	  	
	  	int slot = elements.getKey();
	  	int amount = elements.getValue();
	  	
	  	distributionelements.put(slot, amount);
	  }
	}
  
  /**
   * 
   * return the sum of all distributionselements
   * 
   * @return
   */
  private int getsumofalldistributionelements()
  {
  	int sum=0;
  	for (Entry<Integer, Integer> mapentry : distributionelements.entrySet())
  	{
  		sum+=mapentry.getValue();
  	}
  	assert sum!=0:"sum is zero : no entries in distribution?";
  	return sum;
  }
  
  /**
   * 
   * returns the sum of all distribtionselements within the given boundaries
   * 
   * @param lowerbound
   * @param upperbound
   * @return
   */
  private int getsumofalldistributionelements(int lowerbound, int upperbound)
  {
  	int sum=0;
  	for (Entry<Integer, Integer> mapentry : distributionelements.entrySet())
  	{
  		if (mapentry.getKey()>=lowerbound && mapentry.getKey()<=upperbound) sum+=mapentry.getValue();
  	}
  	return sum;
  }
  
  /**
   * 
   * return the lowest key element
   * 
   * @return
   */
  private int getLowestKey()
  {
  	return Collections.min(distributionelements.keySet());
  }
  
  /**
   * 
   * return the highest key element
   * 
   * @return
   */
  private int getHighestKey()
  {
  	return Collections.max(distributionelements.keySet());
  }
  
  /**
   * 
   * method to modify an element of the distribution
   * 
   * @param slot
   */
  public void modifydistributionelement(int slot)
	{
		int oldvalue = distributionelements.get(slot);
		int newvalue = oldvalue + (int) (0.5*getsumofalldistributionelements());
		distributionelements.put(slot,newvalue);
	}

  /**
   * 
   * returns an element from the distribution based on a random number
   * WRD = weighted random draw - the selection of the element is dependent on their share within the distribution
   * 
   * @param lowerbound
   * @param upperbound
   * @param randomgenerator
   * @return
   */
	public int getRandomPickFromDistribution(int lowerbound, int upperbound, RNGHelper randomgenerator)
  {
  	
    //Phase1: check and apply bounds
    int usedLowerBound = getLowestKey();
    int usedUpperBound = getHighestKey();
            
    if(lowerbound != -1 && upperbound != -1)
    {
    	// make sure that boundaries determined by preconditions fit the boundaries of the wrd distribution
    	assert lowerbound<=usedUpperBound : "inconsistent boundaries! lowerBound from preconditions: " + lowerbound + " does not match wrd distributions boundaries: " + usedLowerBound + " - " + usedUpperBound;
      assert upperbound>=usedLowerBound : "inconsistent boundaries! upperBound from preconditions: " + upperbound + " does not match wrd distributions boundaries: " + usedLowerBound + " - " + usedUpperBound;
      assert lowerbound<=upperbound 		: "inconsistent boundaries! upperbound < lowerbound!";
      
      if (lowerbound>=usedLowerBound) usedLowerBound = lowerbound;
      if (upperbound<=usedUpperBound)	usedUpperBound = upperbound;
    }

    
    //Phase2: get random value
  	double rand = randomgenerator.getRandomValue();   
  	
    //Phase 3: create a map with valid elements (within the boundaries) and their accumulated share (according to all valid elements)
  	int sumofvalidelements = getsumofalldistributionelements(usedLowerBound, usedUpperBound);
    TreeMap<Integer, Double> validelements = new TreeMap<Integer, Double>();
    double runningshare = 0;
    
    int firstslot=-1;
    int lastslot=-1;
    
    // if all element values are equal to zero, choose one of them randomly
    if (sumofvalidelements==0)
    {
    	firstslot = usedLowerBound;
    	lastslot = usedUpperBound;
    }
    // otherwise choose possible results according to their share
    else
    {
	  	for (Entry<Integer, Integer> mapentry : distributionelements.entrySet())
	  	{
	  		int slot = mapentry.getKey();
	  		int amount = mapentry.getValue();
	  		
	  		if (slot>=usedLowerBound && slot<=usedUpperBound)
				{
	  			
	  			//check if the rand value lies between the runninshare of the last slot and the actual slot
	  			if (rand>=runningshare) firstslot = slot;
	  			
	  			//update runningsahre / accumulated share for the distribution element
	  			double share = (double) amount/ (double) sumofvalidelements;
	  			runningshare += share;
	  			validelements.put(slot, runningshare);
	  			
	  			//check if the slot ist the last value where rand is smaller than the runningshare
	  			if (lastslot==-1 && rand<=runningshare) lastslot = slot;
				}
	  	}  	
	  	assert Math.round(runningshare)==1.0d : "sum of valid element share is not equal to 1!";
    }
    
  	assert firstslot!=-1 : "could not determine firstslot for randomPick";
  	assert lastslot!=-1 : "could not determine lastslot for randomPick";
  	
  	//choose one of the possible slots
  	return randomgenerator.getRandomValueBetween(firstslot, lastslot, 1);

    }


}
