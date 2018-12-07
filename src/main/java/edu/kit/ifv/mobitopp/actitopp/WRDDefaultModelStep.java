package edu.kit.ifv.mobitopp.actitopp;

/**
 * 
 * @author Tim Hilgert
 *
 * object for a wrd (weighted random draw) model step 
 */
public class WRDDefaultModelStep extends AbsHModelStep
{
	// category to get a random draw
	private String category;
	// activitytype for personal, activity type specific distributions
	private ActivityType activityType;
	
	// Distribution element that is used to pick a random number
	private WRDDiscreteDistribution wrddist;
	
	// decision if the distribution should be adapted after drawing 
	private boolean modifydistribution = false;
			
	// element that is finally chosen based on weighted random draw
  private int chosenDistributionElement;
       
  //limits the range in which a number should be picked randomly.
  private int lowerBoundLimiter = -1;
  private int upperBoundLimiter = -1;

  /**
   *
   * @param id
   * @param category
   * @param activityType
   * @param modelCoordinator
   */
  public WRDDefaultModelStep(String id, String category, ActivityType activityType, Coordinator modelCoordinator)
  {
    super(id, modelCoordinator);
    
    this.category = category;
    this.activityType = activityType;
    
    // check if a personalized distribution for this id, category and activity type already exists. If not, create one.
    this.wrddist = modelCoordinator.getpersonalWRDdistribution(id, category, activityType);
    if (wrddist==null) 
    {
    	WRDModelSteplnformation modelstep = modelCoordinator.getFileBase().getModelInformationforWRDStep(id);	
  		WRDModelDistributionInformation distributioninformation = modelstep.getWRDDistribution(category);
    	wrddist = new WRDDiscreteDistribution(distributioninformation);
    	
    	modelCoordinator.addpersonalWRDdistribution(id, category, activityType, wrddist);
    }
  }
  
  /**
   * creates wrd model step element without a given activity type
   * may be used when wrddist should not be dependent from activity type
   *
   * @param id
   * @param category
   * @param modelCoordinator
   */
  public WRDDefaultModelStep(String id, String category, Coordinator modelCoordinator)
  {
    this(id, category, ActivityType.UNKNOWN, modelCoordinator);
  }
  
  
  @Override
  public int doStep()
  {
  
    // pick a random number within the given boundaries
    chosenDistributionElement = wrddist.getRandomPickFromDistribution(this.lowerBoundLimiter, this.upperBoundLimiter, modelCoordinator.getRandomGenerator());
        
    if(modifydistribution)
    {
    	wrddist.modifydistributionelement(chosenDistributionElement);
    }     
    
    return getchosenDistributionElement();
  }
  

  public void printDecisionProcess()
  {
  	System.out.println("--------------- MC-Simulation @ " + this.id + this.category + this.activityType +" ---------------");
    System.out.println("From " + this.lowerBoundLimiter);
    System.out.println("To " + this.upperBoundLimiter);
    System.out.println("Random Value: " + modelCoordinator.getRandomGenerator().getLastRandomValue());
    System.out.println("Chosen: " + chosenDistributionElement);
    System.out.println("");
  }
   
  /**
   * 
   * @param lowerbound
   * @param upperbound
   */
  public void setRangeBounds(int lowerbound, int upperbound)
  {
    this.lowerBoundLimiter = lowerbound;
    this.upperBoundLimiter = upperbound;
  }

  public int getchosenDistributionElement()
  {
    return chosenDistributionElement;
  }  
  
	public void setModifydistribution(boolean modifydistribution) 
	{
		this.modifydistribution = modifydistribution;
	}

}
