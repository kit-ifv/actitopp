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
public class HActivity
{
	
	//stores all attributes that are not directly accessible by variables	
	private Map<String, Double> attributes;
		
	private HDay day;	
	private HTour tour;
	
	private ActivityType acttype = ActivityType.UNKNOWN;
	
  private int index								= -99;
  private int duration 						= -1;
  private int starttime 					= -1;
  
  
  private HTrip tripbeforeactivity;
  private HTrip tripafteractivity;
  
  private JointStatus jointStatus = JointStatus.UNKNOWN;
  
  private List<ActitoppPerson> jointParticipants  = new ArrayList<ActitoppPerson>();
 
	/**
   * 
   * constructor
   * 
   * @param parent
   * @param index
   */
	public HActivity(HTour parent, int index)
	{
		assert parent!=null : "tour is not initialized";
    this.tour = parent;
    
    this.day = parent.getDay();
    setIndex(index);
    
    this.attributes = new HashMap<String, Double>();
    setCreatorPersonIndex(day.getPerson().getPersIndex());
	}  
	
	/**
	 * 
	 * constructor
	 * 
	 * @param parent
	 * @param index
	 * @param type
	 */
	public HActivity(HTour parent, int index, ActivityType type)
	{
	    this(parent, index);
	    assert type!=ActivityType.UNKNOWN : "unknown activity type!";
	    setActivityType(type);
	}  	

	/**
	 * 
	 * constructor for home activities
	 * 
	 * @param parent
	 * @param ActType
	 * @param duration
	 * @param starttime
	 */
	public HActivity(HDay parent, ActivityType type, int duration, int starttime)
	{
		assert parent!=null : "day is not initialized";
		this.day = parent;
	  setActivityType(type);
		setDuration(duration);
		setStartTime(starttime);
		setJointStatus(JointStatus.NOJOINTELEMENT);
	}



	public HTour getTour() 
	{
		return tour;
	}
	
	public HDay getDay() 
	{
		assert day != null : "day is not initialized";
		return day;
	}
	
	public HWeekPattern getWeekPattern()
	{
		return day.getPattern();
	}
	
	public ActitoppPerson getPerson()
	{
		return day.getPerson();
	}

	public int getIndex()
	{
		assert index != -99 : "index is not set";
	    return index;
	}

	public void setIndex(int index)
	{
	    this.index = index;
	}
	
	public ActivityType getActivityType()
	{
		assert ActivityType.FULLSET.contains(acttype) : "unknown activity type:" + acttype;
		return acttype;
	}

	public void setActivityType(ActivityType acttype)
	{
		assert ActivityType.FULLSET.contains(acttype) : "unknown activity type:" + acttype;
		this.acttype = acttype;
	}

	public int getDuration() 
	{
		assert duration != -1 : "duration is not set";
		return duration;
	}

	public void setDuration(int duration) 
	{
		assert duration>0 : "duration is not >0: " + duration;
		this.duration = duration;
	}

	public int getStartTime() 
	{
		assert starttime != -1 : "starttime is not set";
		return starttime;
	}

	public void setStartTime(int starttime) 
	{
		assert starttime>=0 : "starttime is not >0: "  + starttime;
		this.starttime = starttime;
	}

	public JointStatus getJointStatus() {
		assert jointStatus!=JointStatus.UNKNOWN : "jointStatus not set";
		return jointStatus;
	}

	public void setJointStatus(JointStatus jointStatus) {
		assert JointStatus.FULLSET.contains(jointStatus) : "invalid value for jointStatus - actual value: " + jointStatus;
		this.jointStatus = jointStatus;
	}

	/**
	 * 
	 * @return the estimatedTripTimeBeforeActivity
	 */
	public int getEstimatedTripTimeBeforeActivity() 
	{
		assert tripBeforeActivityisScheduled() : "trip before activity in not intialized";
		int tmptriptimebefore = tripbeforeactivity.getDuration();
		return tmptriptimebefore;
	}

	/**
	 * @return the estimatedTripTimeAfterActivity
	 */
	public int getEstimatedTripTimeAfterActivity() 
	{
		assert tripAfterActivityisScheduled() : "trip after activity in not intialized";
		int tmptriptimeafter = tripafteractivity.getDuration();
		return tmptriptimeafter;
	}
	
	/**
	 * sorts list of activities ascending by week-order start time
   * 
   * @param actList
   */
  public static void sortActivityListbyWeekStartTimes(List<HActivity> actList)
  {
  	assert actList != null : "empty list";
  	
  Collections.sort(actList, new Comparator<HActivity>()
  {
	    @Override
	    public int compare(HActivity act1, HActivity act2)
	    {
	      if(act1.getStartTimeWeekContext()< act2.getStartTimeWeekContext())
	      {
	          return -1;
	      }
	      else if(act1.getStartTimeWeekContext() == act2.getStartTimeWeekContext())
	      {
	          return 0;
	      }
	      else
	      {
	          return 1;
	      }
	    }
	  });
  }
    
  /**
   * 
   * sorts list of activities ascending by week-order indices
   * 
   * @param list
   */
  public static void sortActivityListbyIndices(List<HActivity> list)
  {
  	assert list != null : "list is empty";
  		
      Collections.sort(list, new Comparator<HActivity>()
      {
        @Override
        public int compare(HActivity o1, HActivity o2)
        {
        	int result = 99;
        	if			(o1.getDayIndex() <  o2.getDayIndex()) result = -1;
        	else if	(o1.getDayIndex() >  o2.getDayIndex()) result = +1;
        	else
        	{
          	if			(o1.getTour().getIndex() <   o2.getTour().getIndex()) result = -1;
          	else if	(o1.getTour().getIndex() >   o2.getTour().getIndex()) result = +1;
          	else
          	{
              if			(o1.getIndex() < o2.getIndex()) result = -1;
              else if	(o1.getIndex() > o2.getIndex()) result = +1;
              else 																		result = 0;
          	}
        	}
        	
        	assert result!=99 : "Could not compare these two activities! - Act1: " + o1 + " - Act2: " + o2;
        	return result;      		
        }
        
      });
  }
  
  /**
   * calculates duration between end of first activity (including trip after activity if there is one)
   * and the beginning of a second activity (including trip before activity if there is one)
   * 
   * @param firstact
   * @param secondact
   * @return
   */
  public static int getTimebetweenTwoActivities(HActivity firstact, HActivity secondact)
  {
  	int result=-999999;
  	
  	int endezeitersteakt;
  	int anfangszeitzweiteakt;
  	
  	// end of first activity
  	endezeitersteakt = firstact.getEndTimeWeekContext();
  	if (firstact.isActivityLastinTour())
  	{
  		endezeitersteakt += firstact.getEstimatedTripTimeAfterActivity();
  	}

  	// beginning second activity
  	anfangszeitzweiteakt = secondact.getTripStartTimeBeforeActivityWeekContext();
  	

  	result = anfangszeitzweiteakt - endezeitersteakt; 	
  	assert result!=-999999 : "Could not determine time between these two activities";
  	return result;
  }
  
  
  
  /**
   * check if two activities are overlapping
   * false = no overlapping
   * true = overlapping
	 * 
	 * @param tmpact
	 * @return
	 */
	public static boolean checkActivityOverlapping(HActivity act1, HActivity act2)
	{
		boolean result = false;
		
		// check time occupation of first activity
		int starttime_first = act1.getStartTimeWeekContext() - (act1.tripBeforeActivityisScheduled() ? act1.getEstimatedTripTimeBeforeActivity() : 0);
		int endtime_first = (act1.durationisScheduled() ? act1.getEndTimeWeekContext() : act1.getStartTimeWeekContext()) + (act1.tripAfterActivityisScheduled() ? act1.getEstimatedTripTimeAfterActivity() : 0);
	
		// check time occupation of second activity
		int starttime_second = act2.getStartTimeWeekContext() - (act2.tripBeforeActivityisScheduled() ? act2.getEstimatedTripTimeBeforeActivity() : 0);
		int endtime_second = (act2.durationisScheduled() ? act2.getEndTimeWeekContext() : act2.getStartTimeWeekContext()) + (act2.tripAfterActivityisScheduled() ? act2.getEstimatedTripTimeAfterActivity() : 0);
		
		if (
					// start or end of second activity is within time occupation of the first
					(starttime_second > starttime_first && starttime_second < endtime_first) ||
					(endtime_second 	> starttime_first && endtime_second 	< endtime_first)
					||
					// start or end of first activity is within time occupation of the second
					(starttime_first > starttime_second && starttime_first < endtime_second) ||
					(endtime_first 	 > starttime_second && endtime_first 	 < endtime_second)
			 )
		{
			result=true;
		}
		
		return result;
	}
	
	/**
	 * create start times for activities in list when they can be determined
	 * 
	 * @param actliste
	 */
  public static void createPossibleStarttimes(List<HActivity> actliste)
  {
  	for (HActivity act : actliste)
    {
    	if (!act.startTimeisScheduled())
    	{
        /*
         * if the previous activity in the tour has a determined startime time and duration,
         * also the actual activity can be determined concerning start time.
         */
        if (!act.isActivityFirstinTour() 
        				&& act.getPreviousActivityinTour().startTimeisScheduled() 
        				&& act.getPreviousActivityinTour().durationisScheduled())
    		{
        	act.setStartTime(act.getPreviousActivityinTour().getEndTime() + act.getEstimatedTripTimeBeforeActivity());
    		}	
        	
        /*
         * if the following activity in the tour has a determined startime time and duration,
         * also the actual activity can be determined concerning start time.
         */
        if (!act.isActivityLastinTour() 
        		&& act.durationisScheduled()
        		&& act.getNextActivityinTour().startTimeisScheduled())
        {
        	act.setStartTime(act.getNextActivityinTour().getTripStartTimeBeforeActivity() - act.getDuration());
        }
    	}
    }
  }

	/**
   * compare two activities concerning indices
   * 
   * 0  - same activity
   * 1  - acttocompare is AFTER the other activity (higher day, tour or activity index)
   * -1 - acttocompare is BEFORE the other activity
   * 
   * @param acttocompare
   * @return
   */
  public int compareTo(HActivity acttocompare) 
  {
    int result=99;
    if (acttocompare.getWeekDay() >  this.getWeekDay()) result = 1;
    if (acttocompare.getWeekDay() <  this.getWeekDay()) result = -1;
    if (acttocompare.getWeekDay() == this.getWeekDay())
    {
      if (acttocompare.getTour().getIndex() >  this.getTour().getIndex()) result = 1;
      if (acttocompare.getTour().getIndex() <  this.getTour().getIndex()) result = -1;    	
      if (acttocompare.getTour().getIndex() == this.getTour().getIndex())
      {
        if (acttocompare.getIndex() >  this.getIndex()) result = 1;
        if (acttocompare.getIndex() <  this.getIndex()) result = -1;
        if (acttocompare.getIndex() == this.getIndex()) result = 0;
      } 
    }
    assert result!=99 : "Could not compare these two activities! - Act1: " + this + " - Act2: " + acttocompare;
    return result;
  }
 

  @Override
	public String toString()
	{
  	String result="";
  	
  	if (isHomeActivity())
  	{
  		result= getDayIndex() + 	
  				" start " + (startTimeisScheduled() ? getStartTimeWeekContext() : "n.a.") + 
  				" end " + (startTimeisScheduled() && durationisScheduled() ? getEndTimeWeekContext() : "n.a.") + 
  				" duration: " + (durationisScheduled() ? this.duration : "n.a.") + 
  				" type: " + (activitytypeisScheduled() ? this.acttype.getTypeasChar() : "n.a.") + 
  				" jointStatus: " + this.jointStatus
  				;  		
  	}
  	else
  	{
  		result= getDayIndex() + "/" + getTour().getIndex() + "/" + getIndex() + 	
		  				" start " + (startTimeisScheduled() ? getStartTimeWeekContext() : "n.a.") + 
		  				" end" + (startTimeisScheduled() && durationisScheduled() ? getEndTimeWeekContext() : "n.a.") + 
		  				" duration: " + (durationisScheduled() ? this.duration : "n.a.") + 
		  				" type: " + (activitytypeisScheduled() ? this.acttype.getTypeasChar() : "n.a.") + 
		  				" jointStatus: " + this.jointStatus +
		  				" trip before: " + (tripBeforeActivityisScheduled() ? getEstimatedTripTimeBeforeActivity() : "n.a.") + 
		  				" trip after: " + (tripAfterActivityisScheduled() ? getEstimatedTripTimeAfterActivity() : "n.a.")
		  				;
  	}
		return result;
	}


	public boolean isActivityFirstinTour()
	{
		return getTour().getLowestActivityIndex()==getIndex();
	}
	
	public boolean isActivityLastinTour()
	{
		return getTour().getHighestActivityIndex()==getIndex();
	}
	
	
	/**
	 * commuting trips BEFORE are trips
	 * - that start at home
	 * - that end at work (activity type is work)
	 *
	 * @return
	 */
	public boolean hasWorkCommutingTripbeforeActivity()
	{
		return ((isActivityFirstinTour() && getActivityType()==ActivityType.WORK && (getPerson().getCommutingdistance_work() != 0.0)) ? true : false); 
	}
	
	/**
	 * commuting trips AFTER are trips
	 * - that end at home
	 * - that start at work (activity type is home)
	 *
	 * @return
	 */
	public boolean hasWorkCommutingTripafterActivity()
	{
		return ((isActivityLastinTour() && getActivityType()==ActivityType.WORK && (getPerson().getCommutingdistance_work() != 0.0)) ? true : false); 
	}
	
	/**
	 * commuting trips BEFORE are trips
	 * - that start at home
	 * - that end at school/university (activity type is education)
	 *
	 * @return
	 */
	public boolean hasEducationCommutingTripbeforeActivity()
	{
		return ((isActivityFirstinTour() && getActivityType()==ActivityType.EDUCATION && (getPerson().getCommutingdistance_education() != 0.0)) ? true : false); 
	}
	
	/**
	 * commuting trips AFTER are trips
	 * - that end at home
	 * - that start at school/university (activity type is home)
	 *
	 * @return
	 */
	public boolean hasEducationCommutingTripafterActivity()
	{
		return ((isActivityLastinTour() && getActivityType()==ActivityType.EDUCATION && (getPerson().getCommutingdistance_education() != 0.0)) ? true : false); 
	}
	
	/**
	 * @return
	 */
	public boolean isHomeActivity()
	{
		return (activitytypeisScheduled() && getActivityType()==ActivityType.HOME);
	}

	/**
	 * @return
	 */
	public boolean isMainActivityoftheTour()
	{
		return this.getIndex()==0;
	}
	
	/**
	 * @return
	 */
	public boolean isMainActivityoftheDay()
	{
		return isMainActivityoftheTour() && getTour().isMainTouroftheDay();
	}
	
	/**
	 * @return
	 */
	public HActivity getPreviousActivityinTour()
	{
		HActivity previousActivity;
		
    if (!isActivityFirstinTour())
    {
    	previousActivity = getTour().getActivity(getIndex()-1);
    }
    else
    {
    	previousActivity = null;
    }
    return previousActivity;
	}
	
	/**
	 * @return
	 */
	public HActivity getNextActivityinTour()
	{
		HActivity nextActivity;
		
	  if (!isActivityLastinTour())
    {
    	nextActivity = getTour().getActivity(getIndex()+1);
    }
    else
    {
    	nextActivity = null;
    }
    return nextActivity;
	}
	
	
	/**
	 * @return
	 */
	public HActivity getPreviousOutOfHomeActivityinPattern()
	{
		HActivity previousact=null;
		
		// if this is the first actitvity, get the last one from previous tour
		if (isActivityFirstinTour())
		{
			HTour previousTour = getTour().getPreviousTourinPattern();
			if (previousTour!=null) previousact = previousTour.getLastActivityInTour();
		}
		else
		{
			previousact = getPreviousActivityinTour();
		}
		return previousact;
	}

	/**
	 * @return
	 */
	public HActivity getNextOutOfHomeActivityinPattern()
	{
		HActivity nextact=null;
		
		// if this is the last actitvity, get the first one from next tour
		if (isActivityLastinTour())
		{
			HTour nexttour = getTour().getNextTourinPattern();
			if (nexttour!=null) nextact = nexttour.getFirstActivityInTour();
		}
		else
		{
			nextact = getNextActivityinTour();
		}
		return nextact;
	}

	
	
	/**
	 * starttime + duration
	 * 
	 * @return
	 */
	public int getEndTime()
	{
		return getStartTime() + getDuration(); 
	}

	public int getWeekDay()
	{
		return getDay().getWeekday();
	}

	public int getDayIndex()
	{
		return getDay().getIndex();
	}
	
	public int getTourIndex()
	{
		return getTour().getIndex();
	}
	
	public int getStartTimeWeekContext()
	{
		return 1440*getDayIndex() + getStartTime();
	}

	public int getEndTimeWeekContext()
	{
		return getStartTimeWeekContext() + getDuration();
	}
	
	public int getTripStartTimeBeforeActivity()
	{
		return getTripbeforeactivity().getStartTime();
	}

	public int getTripStartTimeBeforeActivityWeekContext()
	{
		return getTripbeforeactivity().getStartTimeWeekContext();
	}
	
	public int getTripStartTimeAfterActivity()
	{
		return getTripafteractivity().getStartTime();
	}
	
	public int getTripStartTimeAfterActivityWeekContext()
	{
		return getTripafteractivity().getStartTimeWeekContext();
	}
	
	/**
	 * @return the tripbeforeactivity
	 */
	public HTrip getTripbeforeactivity() {
		return tripbeforeactivity;
	}

	/**
	 * @param tripbeforeactivity the tripbeforeactivity to set
	 */
	public void setTripbeforeactivity(HTrip tripbeforeactivity) {
		this.tripbeforeactivity = tripbeforeactivity;
	}

	/**
	 * @return the tripafteractivity
	 */
	public HTrip getTripafteractivity() {
		return tripafteractivity;
	}

	/**
	 * @param tripafteractivity the tripafteractivity to set
	 */
	public void setTripafteractivity(HTrip tripafteractivity) {
		this.tripafteractivity = tripafteractivity;
	}

	/**
	 * 
	 * mean time calculation
	 * 
	 * @return
	 */
	public int calculateMeanTime()
	{
		double timebudget = getPerson().getAttributefromMap(getActivityType() + "budget_exact");
		double daysWithAct = getWeekPattern().countDaysWithSpecificActivity(getActivityType());
		double specificActivitiesForCurrentDay = getDay().getTotalNumberOfActivitites(getActivityType());
		
		// calculation (between 0 and 1440)
		double meantime;
		meantime = (int) Math.max((timebudget / daysWithAct) * (1 / specificActivitiesForCurrentDay),1.0);
		meantime = Math.min(meantime, 1440.0);
		
		return (int) meantime;
	}
	
	/**
	 * 
	 * mean time calculation (category)
	 * 
	 * @return
	 */
	public int calculateMeanTimeCategory()
	{
		int meantime = calculateMeanTime();
		int meantimecategory = -99;
		for (int i=0; i<Configuration.NUMBER_OF_ACT_DURATION_CLASSES; i++)
		{
			if (meantime >= Configuration.ACT_TIME_TIMECLASSES_LB[i] && meantime <= Configuration.ACT_TIME_TIMECLASSES_UB[i]) meantimecategory = i;
		}
		assert meantimecategory!=-99 : "could not determine category!";
		return meantimecategory;
	}
	
	/**
	 * calculates trip times (default or based on commuting distances)
	 */
	public void createTripsforActivity()
	{

		/*
		 * trip BEFORE activity
		 */
		
		// default
    int actualTripTime_beforeTrip = Configuration.FIXED_TRIP_TIME_ESTIMATOR;
    // better than default if we have commuting information
    if (hasWorkCommutingTripbeforeActivity()) actualTripTime_beforeTrip = getPerson().getCommutingDuration_work();
    if (hasEducationCommutingTripbeforeActivity()) actualTripTime_beforeTrip = getPerson().getCommutingDuration_education();
		if (tripbeforeactivity==null)
		{   
			tripbeforeactivity = new HTrip(this, TripStatus.TRIP_BEFORE_ACT, actualTripTime_beforeTrip);
		}
		else
		{
			tripbeforeactivity.setDuration(actualTripTime_beforeTrip);
		}

		/*
		 * trip AFTER activity
		 */	
		
		if (isActivityLastinTour())
		{
			// default
	    int actualTripTime_afterTrip = Configuration.FIXED_TRIP_TIME_ESTIMATOR;
	    // better than default if we have commuting information
	    if (hasWorkCommutingTripafterActivity()) actualTripTime_afterTrip = getPerson().getCommutingDuration_work();
	    if (hasEducationCommutingTripafterActivity()) actualTripTime_afterTrip = getPerson().getCommutingDuration_education();
	    if (tripafteractivity==null)
	    {
	    	tripafteractivity = new HTrip(this, TripStatus.TRIP_AFTER_ACT, actualTripTime_afterTrip);
	    }
	    else
	    {
	    	tripafteractivity.setDuration(actualTripTime_afterTrip);
	    }
	    
		}
	}
	
	public boolean isScheduled()
	{
		return durationisScheduled() && startTimeisScheduled() && activitytypeisScheduled() && (Configuration.model_joint_actions ? this.jointStatus!=JointStatus.UNKNOWN : true);
	}
	
	public boolean activitytypeisScheduled()
	{
		return this.acttype!=ActivityType.UNKNOWN;
	}
	
	public boolean tripBeforeActivityisScheduled()
	{
		return tripbeforeactivity!=null && tripbeforeactivity.isScheduled();
	}
	
	public boolean tripAfterActivityisScheduled()
	{
		return tripafteractivity!=null && tripafteractivity.isScheduled();
	}
	
	public boolean durationisScheduled()
	{
		return duration!=-1;
	}
	
	public boolean startTimeisScheduled()
	{
		return starttime!=-1;
	}
	
	/**
	 * @param attributes 
	 */
	public void addAttributetoMap(String name, Double value) {
		assert !attributes.containsKey(name) : "attribute is already in map";
		this.attributes.put(name, value);
	}
	
	/**
	 * @param name
	 * @return
	 */
	public Double getAttributefromMap(String name) {
		return this.attributes.get(name);
	}


	/**
	 * @return the attributes
	 */
	public Map<String, Double> getAttributesMap() {
		return attributes;
	}
	
	/**
	 * returns personindex of person that first created this activity.
	 * if modeling joint actions this may be another person of the household that was modeled previous
	 * in modeling order and that first created this joint activity.
	 * 
	 * @return
	 */
	public int getCreatorPersonIndex()
	{
		int result = -1;
		if (attributes.containsKey("CreatorPersonIndex"))
		{
			result = getAttributefromMap("CreatorPersonIndex").intValue();
		}
		else
		{
			result = getPerson().getPersIndex();
		}
		assert result!=-1 : "could not determine CreatorPersonIndex!";
		return result;
	}
	
	/**
	 * sets the personindex of person that first created this activity.
	 * 
	 * @param persindex
	 */
	public void setCreatorPersonIndex(int persindex)
	{
		if (attributes.containsKey("CreatorPersonIndex"))
		{
			attributes.remove("CreatorPersonIndex");
		}
		attributes.put("CreatorPersonIndex", (double) persindex);
	}

	/**
	 * @return the JointParticipants
	 */
	public List<ActitoppPerson> getJointParticipants() {
		return jointParticipants;
	}

	/**
	 * @param JointParticipants the JointParticipants to set
	 */
	public void setJointParticipants(List<ActitoppPerson> gemJointParticipants) {
		this.jointParticipants = gemJointParticipants;
	}
	
	/**
	 * @param person
	 */
	public void addJointParticipant (ActitoppPerson person)
	{
		jointParticipants.add(person);
	}
	
	
	/**
	 * @param person
	 */
	public void removeJointParticipant (ActitoppPerson person)
	{
		jointParticipants.remove(person);
		
		// if there is no other jointParticipant left, remove jointStatus of the activity
		if (jointParticipants.size()==0) setJointStatus(JointStatus.NOJOINTELEMENT);
	}
	
	/**
	 * determines a default activity time based on activity type and the amount of activities for this type on the specific day
	 * 
	 * empirical values are based on MEDIAN-values of duration per day [min] based on German Mobility Panel data
	 * default activity time: defaulttime = empirical value / numberofactivites(type, day)
	 * 
	 * @return
	 */
	public int getDefaultActivityTime()
	{
		int defaulttime=-1;
		switch(getActivityType()){
    case WORK:
    	defaulttime = 472 / getDay().getTotalNumberOfActivitites(ActivityType.WORK);
      break;
    case EDUCATION:
    	defaulttime = 340 / getDay().getTotalNumberOfActivitites(ActivityType.EDUCATION);
      break;
    case LEISURE:
    	defaulttime = 130 / getDay().getTotalNumberOfActivitites(ActivityType.LEISURE);
      break;
    case SHOPPING:
    	defaulttime = 41 / getDay().getTotalNumberOfActivitites(ActivityType.SHOPPING);
      break; 
    case TRANSPORT:
    	defaulttime = 15 / getDay().getTotalNumberOfActivitites(ActivityType.TRANSPORT);
      break; 
    default:
    	defaulttime = 278 / getDay().getTotalAmountOfActivitites();
      break; 
		}
		assert defaulttime!=-1 : "could not determine defaultTime!";
		return defaulttime;
	}
	
}
