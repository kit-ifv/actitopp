package edu.kit.ifv.mobitopp.actitopp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class ActiToppHousehold {
	
	private int HouseholdIndex;
	
	// Enthält alle Haushaltsmitglieder
	private Map<Integer, ActitoppPerson> householdmembers;
	
	// Haushaltseigenschaften
	private int children0_10;
	private int children_u18;
	private int areatype;
	private int numberofcarsinhousehold;	
	
	/**
	 * 
	 * Konstruktor
	 *
	 * @param householdIndex
	 * @param children0_10
	 * @param children_u18
	 * @param areatype
	 * @param numberofcarsinhousehold
	 */
	public ActiToppHousehold(int householdIndex, int children0_10, int children_u18, int areatype, int numberofcarsinhousehold) {
		
		super();
		HouseholdIndex = householdIndex;

		this.children0_10 = children0_10;
		this.children_u18 = children_u18;
		this.areatype = areatype;
		this.numberofcarsinhousehold = numberofcarsinhousehold;

		this.householdmembers = new HashMap<Integer, ActitoppPerson>();
	}

	/**
	 * @return the householdIndex
	 */
	public int getHouseholdIndex() {
		return HouseholdIndex;
	}

	/**
	 * @return the householdmembers
	 */
	public Map<Integer, ActitoppPerson> getHouseholdmembers() {
		return householdmembers;
	}

	/**
	 * @return the householdmembers
	 */
	public List<ActitoppPerson> getHouseholdmembersasList() {
		List<ActitoppPerson> tmpliste = new ArrayList<ActitoppPerson>();
		
		for (Map.Entry<Integer, ActitoppPerson> tmpmapentry: getHouseholdmembers().entrySet()) {
			tmpliste.add(tmpmapentry.getValue());
		}
		
		return tmpliste;
	}
	
	/**
	 * 
	 * @param persnrinhousehold
	 * @return the person in the household
	 */
	public ActitoppPerson getHouseholdMember(int persnrinhousehold) {
		ActitoppPerson tmpperson = this.getHouseholdmembers().get(persnrinhousehold);
		assert tmpperson!=null : "Person does not exist in this household!";
		return tmpperson;
	}
	
	/**
	 * 
	 * Fügt dem Haushalt eine neue Person mit der übergebenen Nummer hinzu
	 * 
	 * @param member
	 * @param persnr
	 */
	public void addHouseholdmember(ActitoppPerson member, int persnr) {
		assert member!=null : "Householdmember is null";
		assert this.householdmembers.get(persnr)==null : "Householdmember using this identifier already exists - persnr " + persnr;
		this.householdmembers.put(persnr, member);
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
	 * Gibt die Anzahl der Personen im Haushalt zurück
	 * @return
	 */
	public int getNumberofPersonsinHousehold() {
		return this.householdmembers.size();
	}
	
	/**
	 * Setzt alle Modellierungsergebnisse für diesen Haushalt zurück!
	 */
	public void resetHouseholdModelingResults()
	{
		for (ActitoppPerson actperson : getHouseholdmembersasList())
		{
			actperson.clearAttributesMap();
			actperson.clearWeekPattern();
			actperson.clearJointActivitiesforConsideration();
		}
	}
	
	
	@Override
	public String toString()	{
  	StringBuffer message = new StringBuffer();

  	message.append("\n Haushaltsinformationen");
  	
		message.append("\n - HHIndex : ");
		message.append(getHouseholdIndex());
		
		message.append("\n - Anzahl HHMember : ");
		message.append(getNumberofPersonsinHousehold());		
		
		message.append("\n - Anzahl Kinder 0-10 : ");
		message.append(getChildren0_10());
		
		message.append("\n - Anzahl Kinder unter 18 : ");
		message.append(getChildren_u18());
		
		message.append("\n - Raumtyp : ");
		message.append(getAreatype());
		
		message.append("\n - Pkw im HH : ");
		message.append(getNumberofcarsinhousehold());		
		
		return message.toString();
	}
}
