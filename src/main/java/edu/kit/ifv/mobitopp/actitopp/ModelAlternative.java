package edu.kit.ifv.mobitopp.actitopp;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class ModelAlternative
{
  private LCUtilityFunction utilityFunction;
  private String name;
  private double probability=-1;
  private boolean enabled = true;
  
  /*
   * Faktor, mit denen der Nutzen gewichtet wird.
   * In der Regel 1, wird bei manchen Modellteilen aus
   * Stabilitätsaspekten angepasst, bspw. auf 1.1
   * Das heißt der Nutzen wird um 10% erhöht!
   * 
   * Der utillityFactor wird bei getUtility Methode verrechnet!
   */
  private double utilityfactor=1.0;
  
  /**
   * 
   * Konstruktor
   * 
   * @param name
   */
	public ModelAlternative(String name)
	{
	  super();
	  this.name = name;
	  utilityFunction = new LCUtilityFunction(0f);
	}

	public LCUtilityFunction getUtilityFunction()
  {
    return utilityFunction;
  }

  /**
	 * @param utilityfactor the utilityfactor to set
	 */
	public void setUtilityfactor(double utilityfactor) {
		this.utilityfactor = utilityfactor;
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
    return utilityFunction.getUtility() * utilityfactor;
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
