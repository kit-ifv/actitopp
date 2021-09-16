package edu.kit.ifv.mobitopp.actitopp;

/**
 * 
 * 	represents a combination of parameter and attribute as an element of a utility function.
 * 	 
 * 	- name 						: name of the parameter
 *  - parameterValue 	: value of the parameter. Is loaded from the file system at beginning of model execution process
 *  - attributevalue 	: value of the according attribute. Is determined at runtime depeding on actual person, day, tour or activity
 * 
 * @author Tim Hilgert
 *
 */
public class UtilityParameterAttributeCombination
{
  private String name;
  private double parameterValue=-99999;
  private double attributeValue=-99999;
  
  /**
   * 
   * @param name
   * @param parameterValue
   * @param attributeValue
   */
  public UtilityParameterAttributeCombination(String name, double parameterValue, double attributeValue)
  {
    this.name = name;
    this.parameterValue = parameterValue;
    this.attributeValue = attributeValue;
  }

  
  public double getattributeValue()
  {
  	assert attributeValue!=-99999 : "attribute is not set correctly - actual value: " + attributeValue;
    return attributeValue;
  }
  public void setattributevalue(double attributevalue)
  {
    this.attributeValue = attributevalue;
  }
  public double getparameterValue()
  {
  	assert parameterValue!=-99999 : "attribute is not set correctly - actual value: " + parameterValue;
    return parameterValue;
  }
  public String getName()
  {
    return name;
  }
  
  public String toString()
  {
    return name + " // attributValue: " + attributeValue + " // parameterValue: " + parameterValue;
  }
}
