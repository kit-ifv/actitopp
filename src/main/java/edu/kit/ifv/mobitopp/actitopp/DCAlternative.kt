package edu.kit.ifv.mobitopp.actitopp;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class DCAlternative
{
  private UtilityFunction utilityFunction;

  
  private String name;
  private double probability=-1;
  private boolean enabled = true;
  
  /*
   * Faktor to weight utility.
   * by default, factor is equal to 1, i.e. no additional
   * weithing. Some model steps used weighting factor to ensure
   * stability aspects, i.e. setting it to 1.1 is equal to a 10%
   * raising of the utility.
   * 
   * factor is inclued when getUtility
   */
  private double utilityweithingfactor=1.0;
  
  /**
   * 
   * Konstruktor
   * 
   * @param name
   */
	public DCAlternative(String name)
	{
	  super();
	  this.name = name;
	  utilityFunction = new UtilityFunction();
	}

	public UtilityFunction getUtilityFunction()
  {
    return utilityFunction;
  }

  /**
	 * @param utilityfactor the utilityfactor to set
	 */
	public void setUtilityfactor(double utilityfactor) {
		this.utilityweithingfactor = utilityfactor;
	}

	public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

	/**
	 * @return the utility*utilityFactor
	 */
  public double getUtility()
  {
    return utilityFunction.getUtility() * utilityweithingfactor;
  }
  
	/**
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * @param probability the probability to set
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String toString()
  {
  	return getName();
  }
	
}
