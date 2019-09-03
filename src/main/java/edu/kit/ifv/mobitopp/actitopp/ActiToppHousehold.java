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
	
	private int householdIndex;
	
	private Map<Integer, ActitoppPerson> householdmembers;
	
	// household properties
	private int children0_10;
	private int children_u18;
	private int areatype;
	private int numberofcarsinhousehold;	
	
	/**
	 * 
	 * constructor with number of cars in household
	 *
	 * @param householdIndex
	 * @param children0_10
	 * @param children_u18
	 * @param areatype
	 * @param numberofcarsinhousehold
	 */
	public ActiToppHousehold(int householdIndex, int children0_10, int children_u18, int areatype, int numberofcarsinhousehold) {
		
		super();
		this.householdIndex = householdIndex;

		this.children0_10 = children0_10;
		this.children_u18 = children_u18;
		this.areatype = areatype;
		this.numberofcarsinhousehold = numberofcarsinhousehold;

		this.householdmembers = new HashMap<Integer, ActitoppPerson>();
	}
	
	/**
	 * 
	 * constructor without number of cars in household
	 *
	 * @param householdIndex
	 * @param children0_10
	 * @param children_u18
	 * @param areatype
	 */
	public ActiToppHousehold(int householdIndex, int children0_10, int children_u18, int areatype) {
		
		super();
		this.householdIndex = householdIndex;

		this.children0_10 = children0_10;
		this.children_u18 = children_u18;
		this.areatype = areatype;

		this.householdmembers = new HashMap<Integer, ActitoppPerson>();
	}
	
	/**
	 * 
	 * constructor used to "clone" household including all persons in the household
	 *
	 * @param tmphh
	 */
	public ActiToppHousehold(ActiToppHousehold tmphh)
	{

		this (tmphh.getHouseholdIndex(),
					tmphh.getChildren0_10(),
					tmphh.getChildren_u18(),
					tmphh.getAreatype(),
					tmphh.getNumberofcarsinhousehold());
		
		// "clone" all householdmembers
		for (ActitoppPerson tmppers : tmphh.getHouseholdmembersasList())
		{
			new ActitoppPerson(tmppers, this);		
		}
		
	}

	/**
	 * @return the householdIndex
	 */
	public int getHouseholdIndex() {
		return householdIndex;
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
	 * 
	 * @return the numberofpersonsinhousehold
	 */
	public int getNumberofPersonsinHousehold() {
		return this.householdmembers.size();
	}
	
	/**
	 * resets all modeling results for this household
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

  	message.append("\n household information");
  	
		message.append("\n - HH-index : ");
		message.append(getHouseholdIndex());
		
		message.append("\n - #HH-members : ");
		message.append(getNumberofPersonsinHousehold());		
		
		message.append("\n - #children 0-10 : ");
		message.append(getChildren0_10());
		
		message.append("\n - #children <18 : ");
		message.append(getChildren_u18());
		
		message.append("\n - area type : ");
		message.append(getAreatype());
		
		message.append("\n - #car in HH : ");
		message.append(getNumberofcarsinhousehold());		
		
		return message.toString();
	}
	
	/**
	 * generates activity schedules for the household (i.e. for each hh member)
	 * 
	 * @param modelbase
	 * @param rnghelper
	 * @throws InvalidPatternException
	 */
	public void generateSchedules(ModelFileBase fileBase, RNGHelper randomgenerator)	throws InvalidPatternException
	{
		List<ActitoppPerson> hhmembers = getHouseholdmembersasList();
		if (Configuration.model_joint_actions) ActitoppPerson.sortPersonListOnProbabilityofJointActions_DESC(hhmembers, fileBase);
		
		for (int i=0; i<hhmembers.size(); i++)
		{
			ActitoppPerson actperson = hhmembers.get(i);

			boolean personscheduleOK = false;
	    while (!personscheduleOK)
	    {
	      try
	      {
					// stores the modeling order of persons within the household
					actperson.addAttributetoMap("numbermodeledinhh", (double) (i+1));
					
	    		// generates week schedule
	      	actperson.generateSchedule(fileBase, randomgenerator);
	    			      	
	        personscheduleOK = true;                
	      }
	      catch (InvalidPatternException e)
	      {
	        //System.err.println(e.getReason());
	         
	        /*
	         * When modeling joint actions, errors on person level are passed to household level (here). As household members
	         * are connected through joint actions, we need to remodel the whole household.
	         * 
	         * When ignoring modeling joint actions, errors on person level are handled there and we need to remodel the error
	         * person only.
	         */
	        if (Configuration.model_joint_actions)
	        {
	        	throw new InvalidPatternException("Household",actperson.getWeekPattern(),"Remodel Household");
	        }
	      }
	    }	    			        	
		}
	}

	/**
	 * generates activity schedules for the household (i.e. for each hh member) using debug loggers to log results
	 * 
	 * @param modelbase
	 * @param rnghelper
	 * @param debugloggers
	 * @throws InvalidPatternException
	 */
	public void generateSchedules(ModelFileBase fileBase, RNGHelper randomgenerator, DebugLoggers debugloggers)	throws InvalidPatternException
	{
		List<ActitoppPerson> hhmembers = getHouseholdmembersasList();
		if (Configuration.model_joint_actions) ActitoppPerson.sortPersonListOnProbabilityofJointActions_DESC(hhmembers, fileBase);
		
		for (int i=0; i<hhmembers.size(); i++)
		{
			ActitoppPerson actperson = hhmembers.get(i);
	
			boolean personscheduleOK = false;
	    while (!personscheduleOK)
	    {
	      try
	      {
					// stores the modeling ordner of persons within the household
					actperson.addAttributetoMap("numbermodeledinhh", (double) (i+1));
					
	    		// generates week schedule
	      	actperson.generateSchedule(fileBase, randomgenerator, debugloggers);
	    			      	
	        personscheduleOK = true;                
	      }
	      catch (InvalidPatternException e)
	      {
	        //System.err.println(e.getReason());
	        debugloggers.deleteInformationforPerson(actperson);
	        
	        /*
	         * When modeling joint actions, errors on person level are passed to household level (here). As household members
	         * are connected through joint actions, we need to remodel the whole household.
	         * 
	         * When ignoring modeling joint actions, errors on person level are handled there and we need to remodel the error
	         * person only.
	         */
	        if (Configuration.model_joint_actions)
	        {
	        	throw new InvalidPatternException("Household",actperson.getWeekPattern(),"Remodel Household");
	        }
	      }
	    }	    			        	
		}
	}
}
