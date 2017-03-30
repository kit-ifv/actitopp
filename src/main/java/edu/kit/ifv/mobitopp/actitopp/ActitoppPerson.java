package edu.kit.ifv.mobitopp.actitopp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class ActitoppPerson
{
	
	// Enthält alle Attribute, die nicht direkt über Variablen ausgelesen werden können
	private Map<String, Double> attributes;
	
	private HWeekPattern weekPattern;

	private int PersIndex;
	
	private int age;
	private int gender;
	private int children0_10;
	private int children_u18;
	private int employment;
	private int areatype;
	private int numberofcarsinhousehold;	
	
	
	/**
	 * Konstruktor zum Erstellen einer Person
	 * 
	 * @param PersIndex
	 * @param children0_10
	 * @param children_u18
	 * @param age
	 * @param employment
	 * @param gender
	 * @param areatype
	 * @param numberofcarsinhousehold
	 */
	public ActitoppPerson(
			int PersIndex,
			int children0_10,
			int children_u18,
			int age,
			int employment,
			int gender,
			int areatype,
			int numberofcarsinhousehold
	) {
		this.PersIndex = PersIndex;
		this.children0_10 = children0_10;
		this.children_u18 = children_u18;
		this.age = age;
		this.employment = employment;
		this.gender = gender;
		this.areatype = areatype;
		this.numberofcarsinhousehold = numberofcarsinhousehold;  
		
		this.attributes = new HashMap<String, Double>();
		
		}

	
  /**
	 * @return the weekPattern
	 */
	public HWeekPattern getWeekPattern() {
		return weekPattern;
	}


	/**
	 * @return the persIndex
	 */
	public int getPersIndex() {
		return PersIndex;
	}


	/**
	 * @param persIndex the persIndex to set
	 */
	public void setPersIndex(int persIndex) {
		PersIndex = persIndex;
	}


	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}


	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}


	/**
	 * @return the gender
	 */
	public int getGender() {
		return gender;
	}


	/**
	 * @param gender the gender to set
	 */
	public void setGender(int gender) {
		this.gender = gender;
	}


	/**
	 * @return the children0_10
	 */
	public int getChildren0_10() {
		return children0_10;
	}


	/**
	 * @param children0_10 the children0_10 to set
	 */
	public void setChildren0_10(int children0_10) {
		this.children0_10 = children0_10;
	}


	/**
	 * @return the children_u18
	 */
	public int getChildren_u18() {
		return children_u18;
	}


	/**
	 * @param children_u18 the children_u18 to set
	 */
	public void setChildren_u18(int children_u18) {
		this.children_u18 = children_u18;
	}


	/**
	 * @return the employment
	 */
	public int getEmployment() {
		return employment;
	}


	/**
	 * @param employment the employment to set
	 */
	public void setEmployment(int employment) {
		this.employment = employment;
	}


	/**
	 * @return the areatype
	 */
	public int getAreatype() {
		return areatype;
	}


	/**
	 * @param areatype the areatype to set
	 */
	public void setAreatype(int areatype) {
		this.areatype = areatype;
	}


	/**
	 * @return the numberofcarsinhousehold
	 */
	public int getNumberofcarsinhousehold() {
		return numberofcarsinhousehold;
	}


	/**
	 * @param numberofcarsinhousehold the numberofcarsinhousehold to set
	 */
	public void setNumberofcarsinhousehold(int numberofcarsinhousehold) {
		this.numberofcarsinhousehold = numberofcarsinhousehold;
	}

	/**
	 * @param attributes spezifischesAttribut für Map
	 */
	public void addAttributetoMap(String name, Double value) {
		this.attributes.put(name, value);
	}
	
	/**
	 * 
	 * @param name spezifisches Attribut aus Map
	 * @return
	 */
	public double getAttributefromMap(String name) {
		return this.attributes.get(name);
	}


	/**
	 * @return the attributes
	 */
	public Map<String, Double> getAttributesMap() {
		return attributes;
	}


	@Override
	public String toString()	{
  	StringBuffer message = new StringBuffer();

  	message.append("\n Personeninformationen");
  	
		message.append("\n - Nummer : ");
		message.append(PersIndex);
		
		message.append("\n - Anzahl Kinder 0-10 : ");
		message.append(getChildren0_10());
		
		message.append("\n - Anzahl Kinder unter 18 : ");
		message.append(getChildren_u18());
		
		message.append("\n - Alter : ");
		message.append(getAge());

		message.append("\n - Beruf : ");
		message.append(getEmployment());
		
		message.append("\n - Geschlecht : ");
		message.append(getGender());
		
		message.append("\n - Raumtyp : ");
		message.append(getAreatype());
		
		message.append("\n - Pkw im HH : ");
		message.append(getNumberofcarsinhousehold());		

		
		return message.toString();
	}
	
	/**
	 * Methode erzeugt Wochenaktivitätenplan für Person
	 * 
	 * @param modelbase
	 * @throws InvalidPatternException
	 * @throws PrerequisiteNotMetException
	 */
	public void generateSchedule(ModelFileBase modelbase, RNGHelper randomgenerator)	throws InvalidPatternException 
	{
		// Erzeuge ein leeres Default-Pattern
		weekPattern = new HWeekPattern(this);
		
		// Erzeuge einen Coordinator zum Modellablauf
		Coordinator modelCoordinator = new Coordinator(this, modelbase, randomgenerator);
		
		// Erzeuge den Schedule
		try 
		{
			modelCoordinator.executeModel();
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Methode zum Überprüfen der Vorbedingung von Schritt 9
	 * 
	 * @return
	 */
	public boolean isPersonWorkerAndWorkMainToursAreScheduled()
	{   
		int employmenttype = this.getEmployment();
    if (employmenttype == 1 || employmenttype == 2 || employmenttype == 40 || employmenttype == 41 || employmenttype == 42 || employmenttype == 5)
    {
      for (HDay day : getWeekPattern().getDays())
      {
        for (HTour tour : day.getTours())
        {
          if (tour.getActivity(0).getType() == 'W' || tour.getActivity(0).getType() == 'E')
        	{
          	return true;
          }
        }
      }
    }
    else
    {
        return false;
    }
    return false;
	}
	
}
