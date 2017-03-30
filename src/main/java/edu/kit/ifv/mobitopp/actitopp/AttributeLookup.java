package edu.kit.ifv.mobitopp.actitopp;

/**
 * @author Tim Hilgert
 *
 */
public class AttributeLookup {

	ActitoppPerson currentPerson;
	HDay currentDay;
	HTour currentTour;
	HActivity currentActivity;
	
	/**
	 * 
	 * Konstruktor
	 * 
	 * @param currentPerson
	 */
	public AttributeLookup (ActitoppPerson currentPerson)
	{
		this.currentPerson = currentPerson;
	}
	
	/**
	 * 
	 * Konstruktor
	 * 
	 * @param currentPerson
	 * @param currentDay
	 */
	public AttributeLookup (ActitoppPerson currentPerson, HDay currentDay)
	{
		this(currentPerson);
		this.currentDay = currentDay;
	}

	/**
	 * 
	 * Konstruktor
	 * 
	 * @param currentPerson
	 * @param currentDay
	 * @param currentTour
	 */
	public AttributeLookup (ActitoppPerson currentPerson, HDay currentDay, HTour currentTour)
	{
		this(currentPerson, currentDay);
		this.currentTour = currentTour;
	}
	
	/**
	 * 
	 * Konstruktor
	 * 
	 * @param currentPerson
	 * @param currentDay
	 * @param currentTour
	 * @param currentActivity
	 */
	public AttributeLookup (ActitoppPerson currentPerson, HDay currentDay, HTour currentTour, HActivity currentActivity)
	{
		this(currentPerson, currentDay, currentTour);
		this.currentActivity = currentActivity;
	}
	
	/**
	 * 
	 * Get AttributeValue for specific reference
	 * 
	 * @param reference
	 * @param attributeName
	 * @return
	 */
  public double getAttributeValue (String reference, String attributeName)
  {
  	double attributeValue = 999999;
  	assert (reference.equals("default") || reference.equals("person") || reference.equals("day") || reference.equals("tour") || reference.equals("activity")) : "Unknown reference Value - " + reference;
  	
  	if (reference.equals("default") || reference.equals("person")) 
  	{
  		assert currentPerson!=null : "Person nicht initialisiert";
  		attributeValue = ActitoppPersonParameters.getEnumValue(attributeName).getAttribute(currentPerson);
  	}
  	if (reference.equals("day")) 
  	{
  		assert currentDay!=null : "Tag nicht intialisiert";
  		attributeValue = HDayParameters.getEnumValue(attributeName).getAttribute(currentDay);
  	}
  	if (reference.equals("tour")) 
  	{
  		assert currentTour!=null : "Tour nicht initialisiert";
  		attributeValue = HTourParameters.getEnumValue(attributeName).getAttribute(currentTour);
  	}
  	if (reference.equals("activity")) 
  	{
  		assert currentActivity!=null : "Aktivität nicht initialisiert";
  		attributeValue = HActivityParameters.getEnumValue(attributeName).getAttribute(currentActivity);
  	}
  	
  	assert attributeValue != 999999 : "AttributeValue couldn't be read! - Reference: " + reference + " - Attribut: " + attributeName;
  	return attributeValue;
  }
  
  public String toString()
  {
  	String personindex = "n.a.";
  	String daynumber = "n.a.";
  	String tournr = "n.a.";
  	String aktnr = "n.a.";
  	
  	if (currentPerson!=null) personindex = "" + currentPerson.getPersIndex();
  	if (currentDay!=null) daynumber = "" + currentDay.getWeekday();
  	if (currentTour!=null) tournr = "" + currentTour.getIndex();
  	if (currentActivity!=null) aktnr = "" + currentActivity.getIndex();
  	
  	return " Personindex: " + personindex + " // Weekday: " + daynumber + " // Tourindex: " + tournr + " // Actindex: "+ aktnr;
  }
	
}
