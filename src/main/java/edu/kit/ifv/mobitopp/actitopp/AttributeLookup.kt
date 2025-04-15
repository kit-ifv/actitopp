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
	 * constructor
	 * 
	 * @param currentPerson
	 */
	public AttributeLookup (ActitoppPerson currentPerson)
	{
		this.currentPerson = currentPerson;
	}
	
	/**
	 * 
	 * constructor
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
	 * constructor
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
	 * constructor
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
 	
  	switch(reference)
  	{
  		case "default":
	  	case "person":
	  		assert currentPerson!=null : "no person!";
	  		attributeValue = ActitoppPersonParameters.getEnumValue(attributeName).getAttribute(currentPerson);
	  		break;
	  	case "day":
	  		assert currentDay!=null : "no day!";
	  		attributeValue = HDayParameters.getEnumValue(attributeName).getAttribute(currentDay);
	  		break;
	  	case "tour":
	  		assert currentTour!=null : "no tour!";
	  		attributeValue = HTourParameters.getEnumValue(attributeName).getAttribute(currentTour);
	  		break;
	  	case "activity":
	  		assert currentActivity!=null : "no activity!";
	  		attributeValue = HActivityParameters.getEnumValue(attributeName).getAttribute(currentActivity);
	  		break;		
  	}

  	assert attributeValue != 999999 : "AttributeValue couldn't be read! - Reference: " + reference + " - Attribute: " + attributeName;
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
