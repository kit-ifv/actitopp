package edu.kit.ifv.mobitopp.actitopp;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class HWeekPattern
{  
	private ActitoppPerson person;
  private List<HDay> days;
  
  private List<HActivity> homeactivitities;
  
  /**
   * 
   * Constructor
   * 
   */
  public HWeekPattern(ActitoppPerson person)
  {
  	this.person = person;
    homeactivitities = new ArrayList<HActivity>();
    
    
    days = new ArrayList<HDay>();
    for (int i=0; i<7; i++) 
    {
    	days.add(new HDay(this, i+1));
    }
  }    
    
   
    
  /**
	 * @return the person
	 */
	public ActitoppPerson getPerson() {
		return person;
	}

	public List<HDay> getDays()
	{
    return days;
	}

	/**
   * 
   * @param index
   * @return
   */
  public HDay getDay(int index)
  {
  	return days.get(index);
  }
    

  /**
   * 
   * returns activities of the week (only out of home, no home activities) in a list
   * 
   * @return
   */
  public List<HActivity> getAllOutofHomeActivities()
  {
	  List<HActivity> actList = new ArrayList<HActivity>();
	  for(HDay day : this.getDays())
	  {
	    for(HTour tour : day.getTours())
	    {
	      for(HActivity act : tour.getActivities())
	      {
	        actList.add(act);
	      }
	    }
	  }   
	  return actList;
  }
  
  /**
   * 
   * returns activities of the week (only joint activities) in a list
   * 
   * @return
   */
  public List<HActivity> getAllJointActivities()
  {
  	List<HActivity> tmpliste = new ArrayList<HActivity>();
  	for (HActivity act : getAllActivities())
  	{
  		if (JointStatus.JOINTELEMENTS.contains(act.getJointStatus())) tmpliste.add(act);
  	}
  	return tmpliste;
  }
  
  /**
   * 
   * returns activities of the week (only home activities) in a list
   * 
   * @return
   */
  public List<HActivity> getAllHomeActivities()
  {
  	return homeactivitities;
  }

  
  /**
   * 
   * returns all activities of the week (out of home + home activities) in a list
   * 
   * @return
   */
  public List<HActivity> getAllActivities()
  {
  	List<HActivity> tmpliste = new ArrayList<HActivity>();
  	tmpliste.addAll(getAllOutofHomeActivities());
  	tmpliste.addAll(getAllHomeActivities());
  	HActivity.sortActivityListbyWeekStartTimes(tmpliste);
  	return tmpliste;
  }
  
  
  /**
   * 
   * returns all trips of the week in a list
   * 
   * @return
   */
  public List<HTrip> getAllTrips()
  {
  	List<HTrip> tmpliste = new ArrayList<HTrip>();
  	for (HActivity tmpact : getAllActivities())
  	{
  		if (tmpact.tripBeforeActivityisScheduled()) tmpliste.add(tmpact.getTripbeforeactivity());
  		if (tmpact.tripAfterActivityisScheduled()) tmpliste.add(tmpact.getTripafteractivity());
  	}
  	return tmpliste;
  }
  
  
  /**
   * 
   * returns all tours of the week in a list
   * 
   * @return
   */
  public List<HTour> getAllTours()
  {
    List<HTour> tourList = new ArrayList<HTour>();
    for(HDay day : this.getDays())
    {
      for(HTour tour : day.getTours())
      {
        tourList.add(tour);       
      }
    }
    return tourList;
  }
    

  /**
	 * 
	 * returns number of activities in the week (only out of home, no home activities)
	 * 
	 * @return
	 */
	public int getTotalAmountOfOutofHomeActivities()
	{
	  int activities = 0;
	  for(HDay d : this.getDays())
	  {
	    activities += d.getTotalAmountOfActivitites();           
	  }
	  
	  return activities;
	}



	/**
   * 
   * returns number of activities in the week (only out of home, no home activities) for a specific activity type
   * 
   * @param activityType
   * @return
   */
  public int countActivitiesPerWeek(ActivityType activityType)
  {
  	int ctr=0;
  	for(HDay day : this.getDays())
	  {
	    for(HTour tour : day.getTours())
	    {
	      for(HActivity act : tour.getActivities())
	      {
	        if(act.getActivityType()==activityType)ctr++;;
	      }
	    }
	  }
	  return ctr;  	
  }
  
	/**
   * 
   * returns number of tours in the week for a specific activity type
   * 
   * @param activityType
   * @return
   */
  public int countToursPerWeek(ActivityType activityType)
  {
  	
  	int ctr=0;
  	for(HDay day : this.getDays())
	  {
	    for(HTour tour : day.getTours())
	    {
	      if(tour.getActivity(0).getActivityType()==activityType)ctr++;;
	    }
	  }
	  return ctr;  	
  }
  
	/**
   * 
   * returns number of days in the week where an activity of a specific activity type exists 
   * 
   * @param activityType
   * @return
   */
  public int countDaysWithSpecificActivity(ActivityType activityType)
  {
  	int ctr=0;  	
  	for (HDay currentDay : getDays())
		{
			if (currentDay.getTotalNumberOfActivitites(activityType)>0) ctr++; 
		}
  	return ctr;
  }
  
	
	public void printOutofHomeActivitiesList()
  {  	
		printActivities(getAllOutofHomeActivities());
  }
	
	public void printAllActivitiesList()
  {  		
  	printActivities(getAllActivities());
  }


	private void printActivities(List<HActivity> listtoprint) 
	{
		HActivity.sortActivityListbyWeekStartTimes(listtoprint);
  	
  	System.out.println("");
  	System.out.println(" -------------- activity list --------------");
  	System.out.println("");
 	
  	for (int i=0 ; i< listtoprint.size() ; i++)
  	{
  		HActivity act = listtoprint.get(i);   		
  		if (!act.isHomeActivity())
  		{
  			System.out.println(i + " " + act.getTripbeforeactivity());
  		}
  		
  		System.out.println(i + " act : " + act);
  		
  		if (!act.isHomeActivity() && act.isActivityLastinTour())
  		{
  			System.out.println(i + " last " + act.getTripafteractivity());
  		}
  	}
	}
	
	
	public void printJointActivitiesList()
  {  	
		List<HActivity> listtoprint = getAllJointActivities();
  	HActivity.sortActivityListbyWeekStartTimes(listtoprint);

  	System.out.println("");
  	System.out.println(" -------------- list of joint activities --------------");
  	System.out.println("");
 	
  	for (int i=0 ; i< listtoprint.size() ; i++)
  	{
  		HActivity act = listtoprint.get(i);   		
  		System.out.println(i + " akt : " + act + " // creator: " + act.getCreatorPersonIndex()); 
  	}
  }
  
	/**
	 * 
	 * @param act
	 */
	public void addHomeActivity(HActivity act){
		assert act.getActivityType()==ActivityType.HOME : "no home activity";
		homeactivitities.add(act);
	}
	
	/**
	 * 
	 * check if pattern has activity overlaps
	 * 
	 * @return
	 */
	public boolean weekPatternisFreeofOverlaps() 
	{
		boolean freeofOverlaps=true;
	
		List<HActivity> allActivities = this.getAllActivities();
		HActivity.sortActivityListbyWeekStartTimes(allActivities);
    
    for (int i = 0; i < allActivities.size()-1; i++)
    {
    	HActivity aktuelleakt = allActivities.get(i);
    	HActivity naechsteakt = allActivities.get(i+1);
    	
    	assert !HActivity.checkActivityOverlapping(aktuelleakt,naechsteakt) : "activities are overlapping " + aktuelleakt +  " vs " + naechsteakt;
    }
    return freeofOverlaps;
	}

	
}
