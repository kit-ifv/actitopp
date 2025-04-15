package edu.kit.ifv.mobitopp.actitopp;

/**
 * object from this class represent modeling elements for linear regression models consisting of:
 * 
 * 	- name : name of the parameter, e.g. alter10bis17
 *  - contextIdentifier: corresponding context, e.g. person, day, tour, ...
 *  - estimateValue : value of the estimate for regression calculation (readed as input from file base)
 *  - attributevalue : value of the attribute (determined at runtime for the corresponding object, e.g. person)
 *  
 *  attributevalue is initialized with -99999. During the modeling execution it will be overwritten with the runtime value
 *  
 * @author Tim Hilgert
 *
 */
public class LinRegEstimate {
	
	private String name;
	private String contextIdentifier;
	private double estimateValue  = -99999;
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
		assert estimateValue!=-99999 : "no correct initialisation of estimate value - " + estimateValue;
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
		assert attributeValue!=-99999 : "no correct initialisation of attribute value - " + attributeValue;
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
		return name + " (" + contextIdentifier + ") estimate value:" + estimateValue + " attribute value: " + attributeValue;
	}

}
