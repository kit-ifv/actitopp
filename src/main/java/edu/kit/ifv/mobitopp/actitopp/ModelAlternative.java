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

  public void setUtilityFunction(LCUtilityFunction utilityFunction)
  {
    this.utilityFunction = utilityFunction;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public double getUtility()
  {
    return utilityFunction.getUtility();
  }


}
