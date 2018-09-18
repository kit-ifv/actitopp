package edu.kit.ifv.mobitopp.actitopp;

/**
 * 
 * 	Objekte dieser Klasse stellen Modellparameter für lineare Regressionsmodelle dar.
 * 	Besteht aus:
 * 
 * 	- name : Name des entsprechenden Parameters, bspw. alter10bis17
 *  - contextIdentifier: Kontextzuordnung der Variable zu Person, Tag, Tour, ...
 *  - estimateValue : Wert des Estimates für die Berechnung der Linearen FUnktion
 *  - attributevalue : Ausprägungswert der Variable im Modellverlauf
 *  
 *  atributevalue wird mit -99999 initialisiert und muss bei der Berechnung dann mit der konkreten Ausprägung überschrieben worden sein
 * 
 * @author Tim Hilgert
 *
 */
public class LinRegEstimate {
	
	// Name der Variable
	private String name;
	// Kontextzuordnung der Variable zu Person, Tag, Tour, ...
	private String contextIdentifier;
	// Wert des Estimates für die Berechnung
	private double estimateValue = -99999;
	// Ausprägungswert der Variable im Modellverlauf
	private double attributeValue = -99999;
	
	/**
	 * 
	 * Konstruktor
	 *
	 * @param name
	 * @param value
	 * @param contextIdentifier
	 */
	public LinRegEstimate(String name, double value, String contextIdentifier)
	{
		this.name = name;
		this.estimateValue = value;
		this.contextIdentifier = contextIdentifier;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the contextIdentifier
	 */
	public String getContextIdentifier() {
		return contextIdentifier;
	}

	/**
	 * @param contextIdentifier the contextIdentifier to set
	 */
	public void setContextIdentifier(String contextIdentifier) {
		this.contextIdentifier = contextIdentifier;
	}

	/**
	 * @return the estimateValue
	 */
	public double getEstimateValue() {
		assert estimateValue!=-99999 : "Estimate wurde nicht korrekt initialisiert - " + estimateValue;
		return estimateValue;
	}

	/**
	 * @param estimateValue the estimateValue to set
	 */
	public void setEstimateValue(double estimateValue) {
		this.estimateValue = estimateValue;
	}

	/**
	 * @return the attributeValue
	 */
	public double getAttributeValue() {
		assert attributeValue!=-99999 : "Attribut wurde nicht korrekt initialisiert - " + attributeValue;
		return attributeValue;
	}

	/**
	 * @param attributeValue the attributeValue to set
	 */
	public void setAttributeValue(double attributeValue) {
		this.attributeValue = attributeValue;
	}

	public String toString()
	{
		return "Variable: " + name + " (" + contextIdentifier + ") value:" + estimateValue + " Attribut: " + attributeValue;
	}

}
