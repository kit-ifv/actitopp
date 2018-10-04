package edu.kit.ifv.mobitopp.actitopp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author Tim Hilgert
 *
 */
public class ActitoppPerson
{
	
	//corresponding household
	private ActiToppHousehold household;
	
	// stores all attributes that are not directly accessible by variables
	private Map<String, Double> attributes;
	
	private HWeekPattern weekPattern;

	private int PersIndex;
	
	private int age;
	private int gender;
	private int employment;
	
	// commuting distance are 0 by default, i.e. not available or person is not commuting
	private double commutingdistance_work = 0.0;
	private double commutingdistance_education = 0.0;
	
	// Variables used for modeling of joint actions

		// based on lineare regression model to determine modeling order within the household
		private double probableshareofjointactions=-1;
		// List of joint actions to consider that are first created from other household members
		private List<HActivity> jointActivitiesforConsideration;

	
	
	/**
	 * constructor to create a peron without household context
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
		
		/*
		 * Person can be generated without having household context.
		 * To simplify the internal modeling process, a household object 
		 * containing this person only will be created for these cases.
		 */
		this.household = new ActiToppHousehold(
				PersIndex, 
				children0_10, 
				children_u18, 
				areatype, 
				numberofcarsinhousehold
		);
		this.household.addHouseholdmember(this, 1);
				
		this.setPersIndex(PersIndex);
		this.setAge(age);
		this.setEmployment(employment);
		this.setGender(gender);  
		
		this.attributes = new HashMap<String, Double>();
		this.jointActivitiesforConsideration = new ArrayList<HActivity>();
		
		this.addAttributetoMap("numbermodeledinhh", (double) (1));	
		}
	
	
	/**
	 * constructor to create a peron without household context but with commuting distances
	 * 
	 * @param PersIndex
	 * @param children0_10
	 * @param children_u18
	 * @param age
	 * @param employment
	 * @param gender
	 * @param areatype
	 * @param numberofcarsinhousehold
	 * @param commutingdistance_work
	 * @param commutingdistance_education
	 */
	public ActitoppPerson(
			int PersIndex,
			int children0_10,
			int children_u18,
			int age,
			int employment,
			int gender,
			int areatype,
			int numberofcarsinhousehold,
			double commutingdistance_work,
			double commutingdistance_education
	) {
		
		this(PersIndex,children0_10,children_u18,age,employment,gender,areatype,numberofcarsinhousehold);
		
		this.setCommutingdistance_work(commutingdistance_work);
		this.setCommutingdistance_education(commutingdistance_education);
		}	
	
	
	/**
	 * constructor to create a peron with household context
	 * 
	 * @param household
	 * @param PersIndex
	 * @param age
	 * @param employment
	 * @param gender
	 */
	public ActitoppPerson(
			ActiToppHousehold household,
			int persnrinhousehold,
			int PersIndex,
			int age,
			int employment,
			int gender
	) {

		this.household = household;
		this.household.addHouseholdmember(this, persnrinhousehold);
		
		this.setPersIndex(PersIndex);
		this.setAge(age);
		this.setEmployment(employment);
		this.setGender(gender);
		
		this.attributes = new HashMap<String, Double>();
		this.jointActivitiesforConsideration = new ArrayList<HActivity>();
		
		/*
		 * set modeling order within the household.
		 * When modeling joint actions, this will be changed depending on linear regression model
		 * to determine probable share of joint actions
		 */
		this.addAttributetoMap("numbermodeledinhh", (double) persnrinhousehold);
		}	

	
  /**
	 * constructor to create a peron with household context and commuting distances
	 * 
	 * @param household
	 * @param PersIndex
	 * @param age
	 * @param employment
	 * @param gender
	 * @param commutingdistance_work
	 * @param commutingdistance_education
	 */
	public ActitoppPerson(
			ActiToppHousehold household,
			int persnrinhousehold,
			int PersIndex,
			int age,
			int employment,
			int gender,
			double commutingdistance_work,
			double commutingdistance_education
	) 
	{
		
		this(household,persnrinhousehold,PersIndex,age,employment,gender);
		
		this.setCommutingdistance_work(commutingdistance_work);
		this.setCommutingdistance_education(commutingdistance_education);
	
	}
	
	
	/**
	 * 
	 * constructor to "clone" household including all persons in the household
	 *
	 * @param tmppers
	 * @param tmphh
	 */
	public ActitoppPerson(ActitoppPerson tmppers, ActiToppHousehold tmphh)
	{
		this(tmphh,
				tmppers.getPersNrinHousehold(),
				tmppers.getPersIndex(),
				tmppers.getAge(),
				tmppers.getEmployment(),
				tmppers.getGender(),
				tmppers.getCommutingdistance_work(),
				tmppers.getCommutingdistance_education());
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
	 * @return the household
	 */
	public ActiToppHousehold getHousehold() {
		return household;
	}

	/**
	 * 
	 * @return the personnr in the household
	 */
	public int getPersNrinHousehold() {
		
		int result=-1;
		Map<Integer, ActitoppPerson> tmpmap = this.getHousehold().getHouseholdmembers();
		
		for (Map.Entry<Integer, ActitoppPerson> tmpmapentry: tmpmap.entrySet()) {
			if (tmpmapentry.getValue().equals(this)) result = tmpmapentry.getKey();
		}
		
		assert result!=-1: "Person does not exist in this household or has no PersNr in this household";
		return result;
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
	 * @return the children0_10
	 */
	public int getChildren0_10() {
		return getHousehold().getChildren0_10();
	}

	/**
	 * @return the children_u18
	 */
	public int getChildren_u18() {
		return getHousehold().getChildren_u18();
	}


	/**
	 * @return the areatype
	 */
	public int getAreatype() {
		return getHousehold().getAreatype();
	}


	/**
	 * @return the numberofcarsinhousehold
	 */
	public int getNumberofcarsinhousehold() {
		return getHousehold().getNumberofcarsinhousehold();
	}

	
	
	/**
	 * @return the commutingdistance_work
	 */
	public double getCommutingdistance_work() {
		return commutingdistance_work;
	}


	/**
	 * @param commutingdistance_work the commutingdistance_work to set
	 */
	public void setCommutingdistance_work(double commutingdistance_work) {
		this.commutingdistance_work = commutingdistance_work;
	}


	/**
	 * @return the commutingdistance_education
	 */
	public double getCommutingdistance_education() {
		return commutingdistance_education;
	}


	/**
	 * @param commutingdistance_education the commutingdistance_education to set
	 */
	public void setCommutingdistance_education(double commutingdistance_education) {
		this.commutingdistance_education = commutingdistance_education;
	}
	
	/**
	 * 
	 * @return the commutingduration_work [min]
	 */
	public int getCommutingDuration_work()
	{
		/*
		 * mean commuting speed in kilometers/hour is calculated using commuting distance groups
		 * based on data of commuting trips of the German Mobility Panel (2004-2013)
		 */
		double commutingspeed_work;
		if 			(commutingdistance_work>0  && commutingdistance_work <= 5)  commutingspeed_work = 16;
		else if (commutingdistance_work>5  && commutingdistance_work <= 10) commutingspeed_work = 29;
		else if (commutingdistance_work>10 && commutingdistance_work <= 20) commutingspeed_work = 38;
		else if (commutingdistance_work>20 && commutingdistance_work <= 50) commutingspeed_work = 51;
		else if (commutingdistance_work>50) 																commutingspeed_work = 67;
		else																																commutingspeed_work = 32;
		
		// minimum trip duration: 1 Minute
		return (int) Math.max(1, Math.round((commutingdistance_work/commutingspeed_work)*60));
	}
	
	
	/**
	 * 
	 * @return the commutingduration_education [min]
	 */
	public int getCommutingDuration_education()
	{
		/*
		 * mean commuting speed in kilometers/hour is calculated using commuting distance groups
		 * based on data of commuting trips of the German Mobility Panel (2004-2013)
		 */
		double commutingspeed_education;
		if 			(commutingdistance_education>0  && commutingdistance_education <= 5)  commutingspeed_education = 12;
		else if (commutingdistance_education>5  && commutingdistance_education <= 10) commutingspeed_education = 21;
		else if (commutingdistance_education>10 && commutingdistance_education <= 20) commutingspeed_education = 28;
		else if (commutingdistance_education>20 && commutingdistance_education <= 50) commutingspeed_education = 40;
		else if (commutingdistance_education>50) 																			commutingspeed_education = 55;
		else																																					commutingspeed_education = 21;
		
		// minimum trip duration: 1 Minute
		return (int) Math.max(1, Math.round((commutingdistance_education/commutingspeed_education)*60));
	}
	

	/**
	 * @param attributes
	 */
	public void addAttributetoMap(String name, Double value) {
		this.attributes.put(name, value);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public double getAttributefromMap(String name) {
		return this.attributes.get(name);
	}
	
	/**
	 * check existence of attribute
	 * 
	 * @param name
	 * @return
	 */
	public boolean existsAttributeinMap(String name) {
		return this.attributes.get(name)!=null;
	}


	/**
	 * @return the attributes
	 */
	public Map<String, Double> getAttributesMap() {
		return attributes;
	}
	
	/**
	 * delete all attributes from map
	 */
	public void clearAttributesMap() {
		attributes.clear();
	}
	
	/**
	 * delete weekpattern of person
	 */
	public void clearWeekPattern() {
		weekPattern = null;
	}

	/**
	 * delete all joint actions to consider
	 */
	public void clearJointActivitiesforConsideration()
	{
		jointActivitiesforConsideration.clear();
	}

	/**
	 * @return the probableshareofjointactions
	 */
	public double getProbableshareofjointactions() {
		return probableshareofjointactions;
	}

	/**
	 * calculates the probable share of joint actions based on person characteristics
	 * this share is used to determine the modeling order within the household
	 * 
	 * @param fileBase
	 */
	public void calculateProbableshareofjointactions(ModelFileBase fileBase) {
		
		// create attribute lookup
		AttributeLookup lookup = new AttributeLookup(this);
		
		// create modeling object (97estimates.csv contains modeling information)
		LinRegDefaultCalculation regression = new LinRegDefaultCalculation("97estimates", fileBase, lookup);
		
		regression.initializeEstimates();				
		this.probableshareofjointactions = regression.calculateRegression();

	}


	@Override
	public String toString()	{
  	StringBuffer message = new StringBuffer();

  	message.append("\n person information");
  	
		message.append("\n - persindex : ");
		message.append(PersIndex);
				
		message.append("\n - age : ");
		message.append(getAge());

		message.append("\n - employment type : ");
		message.append(getEmployment());
		
		message.append("\n - gender : ");
		message.append(getGender());
		
		message.append("\n - commuting distance work : ");
		message.append(getCommutingdistance_work());		
		
		message.append("\n - commuting distance education : ");
		message.append(getCommutingdistance_education());		
		
		message.append("\n - household ");
		message.append(getHousehold());
		
		return message.toString();
	}
	
	/**
	 * sort list of household members descending by their probable share of joint actions
   * 
   * @param personList
   */
  public static void sortPersonListOnProbabilityofJointActions_DESC(List<ActitoppPerson> personList, ModelFileBase fileBase)
  {
  	assert personList != null : "nothing to sort!";
  	
  	for (ActitoppPerson tmpperson : personList)
  	{
  		tmpperson.calculateProbableshareofjointactions(fileBase);
  	}
  	
      Collections.sort(personList, new Comparator<ActitoppPerson>()
      {
        @Override
        public int compare(ActitoppPerson person1, ActitoppPerson person2)
        {   
          if(person1.getProbableshareofjointactions() < person2.getProbableshareofjointactions())
          {
            return +1;
          }
          else if(person1.getProbableshareofjointactions() == person2.getProbableshareofjointactions())
          {
          	return 0;
          }
          else
          {
          	return -1;
          }
        }
      }
      );
  }
	
	/**
	 * create activity schedule for this person
	 * 
	 * @param modelbase
	 * @param rnghelper
	 * @throws InvalidPatternException
	 */
	public void generateSchedule(ModelFileBase modelbase, RNGHelper randomgenerator)	throws InvalidPatternException
	{		
		// Erzeuge ein leeres Default-Pattern
		weekPattern = new HWeekPattern(this);
		
		// Erzeuge einen Coordinator zum Modellablauf
		Coordinator modelCoordinator = new Coordinator(this, modelbase, randomgenerator);
	
		// Erzeuge den Schedule
		modelCoordinator.executeModel();		
	}
	
	/**
	 * create activity schedule for this person using debug loggers to log results
	 * 
	 * @param modelbase
	 * @param rnghelper
	 * @param debugloggers
	 * @throws InvalidPatternException
	 */
	public void generateSchedule(ModelFileBase modelbase, RNGHelper randomgenerator, DebugLoggers debugloggers)	throws InvalidPatternException
	{		
		// Erzeuge ein leeres Default-Pattern
		weekPattern = new HWeekPattern(this);
		
		// Erzeuge einen Coordinator zum Modellablauf
		Coordinator modelCoordinator = new Coordinator(this, modelbase, randomgenerator, debugloggers);
	
		// Erzeuge den Schedule
		modelCoordinator.executeModel();		
	}
	
  /**
   * 
   * @return
   */
  public List<HActivity> getAllJointActivitiesforConsideration()
  {
  	return jointActivitiesforConsideration;
  }
  
  /**
   * 
   * @param aktliste
   */
  public void setAllJointActivitiesforConsideration(List<HActivity> aktliste)
  {
  	this.jointActivitiesforConsideration = aktliste;
  }
  
	/**
	 * add activity to list of joint actions to consider when there if no conflict
	 * 
	 * @param act
	 */
	public void addJointActivityforConsideration(HActivity act){
		
		//make sure the activity is joint
		assert JointStatus.JOINTELEMENTS.contains(act.getJointStatus()) : "no jointAct!";
		
		// check if there is already an activitx at the same time
		boolean activityconflict = false;
		for (HActivity tmpact : jointActivitiesforConsideration)
		{
			if(HActivity.checkActivityOverlapping(act,tmpact))
			{
				activityconflict = true;
				System.err.println("HH" + getHousehold().getHouseholdIndex() + "/P" + getPersIndex() + ": activity was not added as joint acticity due to conflict with existing activity!");
				if (Configuration.debugenabled)
				{
					System.err.println("act to add: " + act);
					System.err.println("existing act: " + tmpact);
				}

				break;
			}
		}

		if (!activityconflict) 
		{
			jointActivitiesforConsideration.add(act);
		}
	}
	
	/**
	 * determined if a person is anyway employed (full time, part time or in vocational program)
	 * 
	 * @return
	 */
	public boolean personisAnywayEmployed()
	{
		int employmenttype = getEmployment();
		return (employmenttype == 1 || employmenttype == 2 ||  employmenttype == 21 || employmenttype == 22 || employmenttype == 5);
	}
	
	/**
	 * determined if a person is in school or student
	 * 
	 * @return
	 */
	public boolean personisinEducation()
	{
		int employmenttype = getEmployment();
		return (employmenttype == 4 || employmenttype == 40 ||  employmenttype == 41 ||  employmenttype == 42 ||  employmenttype == 5);
	}
	
	
	/**
	 * @return
	 */
	public boolean isPersonWorkorSchoolCommuterAndMainToursAreScheduled()
	{   
    if (personisAnywayEmployed() || personisinEducation())
    {
      for (HDay day : getWeekPattern().getDays())
      {
        for (HTour tour : day.getTours())
        {
          if (tour.getActivity(0).getActivityType() == ActivityType.WORK || tour.getActivity(0).getActivityType() == ActivityType.EDUCATION)
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
