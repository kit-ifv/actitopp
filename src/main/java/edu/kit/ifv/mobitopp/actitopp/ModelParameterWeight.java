package edu.kit.ifv.mobitopp.actitopp;

/**
 * 
 * 	Methode enthält Modellparameter.
 * 	Besteht aus:
 * 
 * 	- name : Name des entsprechenden Parameters, bspw. alter10bis17
 *  - weight : Gewichtung bzw. Wert des Parameters für die jeweilige zugeordnete Alternative
 *  - attributevalue : Ausprägung des entsprechenden zugeordneten Attributes
 *  
 *  atributevalue wird mit -99 initialisiert und muss bei der Berechnung dann mit der konkreten Ausprägung überschrieben worden sein
 * 
 * @author Tim Hilgert
 *
 */
public class ModelParameterWeight
{
  private String name;
  private double weight; //AKA "parameter" in some cases
  private double attributevalue;
  
  /**
   * 
   * Konstruktor
   * 
   * @param name
   * @param weight
   * @param property
   */
  public ModelParameterWeight(String name, double weight, double property)
  {
    this.name = name;
    this.weight = weight;
    this.attributevalue = property;
  }

  public double getattributevalue()
  {
  	assert attributevalue!=-99999 : "Attribut wurde nicht korrekt initialisiert - " + attributevalue;
    return attributevalue;
  }
  public void setattributevalue(double attributevalue)
  {
    this.attributevalue = attributevalue;
  }
  public double getWeight()
  {
  	assert weight!=-99999 : "Gewicht wurde nicht korrekt initialisiert - " + weight;
    return weight;
  }
  public void setWeight(double weight)
  {
    this.weight = weight;
  }
  public String getName()
  {
    return name;
  }
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String toString()
  {
    return name + " // attributvalue: " + attributevalue + " // weight: " + weight;
  }
}
