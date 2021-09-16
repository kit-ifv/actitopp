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
public class HTour
{
	//stores all attributes that are not directly accessible by variables	
	private Map<String, Double> attributes;
	
  private HDay day;
  private List<HActivity> activities;
  private int index		= -99;
  private int starttime 	= -1;

   
  /**
   * 
   * Constructor
   *   
   * @param parent
   * @param index
   */
  public HTour(HDay parent, int index)
  {
  	assert parent!=null : "day is not initialized";
    this.day = parent;
    setIndex(index);  
    this.activities = new ArrayList<HActivity>();
    
    this.attributes = new HashMap<String, Double>();
  }
    

    
  public HDay getDay()
	{
		assert day != null : "day is not initialized";
	  return day;
	}
  
  public HWeekPattern getWeekPattern()
  {
  	return getDay().getPattern();
  }
  
  public ActitoppPerson getPerson()
  {
  	return getDay().getPerson();
  }


  public List<HActivity> getActivities()
	{
		assert activities != null : "list of activities is not initialized";
	  return activities;
	}

  public void addActivity(HActivity act)
  {
  	assert act.getIndex()!=-99 : "index of the activity is not initialized";
  	boolean actindexexisitiert = false;
  	for (HActivity tmpact : activities)
  	{
  		if (tmpact.getIndex() == act.getIndex()) actindexexisitiert = true;
  	}
  	assert !actindexexisitiert : "an activity using this index already exists";
  	activities.add(act);
  }

	public int getIndex()
	{
		assert index != -99 : "index is not initialized";
	  return index;
	}


	public void setIndex(int index)
	{
    this.index = index;
	}


	public int getStartTime()
	{
		assert starttime >= 0 : "start time is negative - start time: " + starttime;
	  return starttime;
	}


	public void setStartTime(int chosenStartTime)
	{
		assert chosenStartTime >= 0 : "start time is negative - start time: " + chosenStartTime;
	  this.starttime = chosenStartTime;
	}


	/**
	 * sort a list of tours by index
	 * 
	 * @param list
	 */
	public static void sortTourList(List<HTour> list)
	{
		assert list != null : "list to sort is empty";
			
	    Collections.sort(list, new Comparator<HTour>()
	    {
        @Override
        public int compare(HTour o1, HTour o2)
        {
            if(o1.getIndex() < o2.getIndex()) return -1;
            if(o1.getIndex() > o2.getIndex()) return 1;
            return 0;
        }
	    });
	}

	/**
	 * check if tour is free of time gaps, i.e., all trips and activities follow directly one after another
	 * 
	 * @return
	 */
	public boolean tourisFreeofGaps()
	{
		boolean gapfree = true;
		HActivity.sortActivityListbyIndices(activities);
		
		for (int i=0 ; i<activities.size()-1; i++)
		{
			HActivity actualact = activities.get(i);
			HActivity nextact = activities.get(i+1);
			
			if (actualact.getEndTimeWeekContext() != nextact.getTripStartTimeBeforeActivityWeekContext()) 
			{
				gapfree=false;
			}
		}	
		
		return gapfree;
	}

	@Override
  public String toString()
  {
		String tostring = "";
		if (this.isScheduled())
		{
			tostring = 	getDay().getIndex() + "/" + getIndex() + 
									" start: " + getStartTimeWeekContext() + 
    							" end: " + getEndTimeWeekContext() + 
    							" duration: " + getTourDuration();  
		}
		else
		{
			tostring = 	getDay().getIndex() + "/" + getIndex() + 
									" start: --- " + 
									" end: --- " +  
									" duration: " + getTourDuration(); 
		}
  	return 	tostring;
  }   
	
	/**
   * create start times for each activity of a tour 
   */
  public void createStartTimesforActivities()
  {
    // ! sorts the list permanently
    HActivity.sortActivityListbyIndices(getActivities());
    
    for (HActivity act : getActivities())
    {
    	// first activity: start time is given by tour start time
    	if (!act.startTimeisScheduled())
    	{
    		if (act.isActivityFirstinTour())
      	{
      		act.setStartTime(getStartTime() + act.getEstimatedTripTimeBeforeActivity());
      	}
      	// other activity: start time is given by end of previous activity
      	else
      	{
      		act.setStartTime(act.getPreviousActivityinTour().getEndTime() + act.getEstimatedTripTimeBeforeActivity());
      	}
    	}
    }
    if (!tourisFreeofGaps()) System.err.println("tour has gaps! " + this); 
  }
  
  
	public boolean isScheduled()
	{
		return starttime!=-1;
	}
	
	public boolean isMainTouroftheDay()
	{
		return this.getIndex()==0;
	}
	
	public boolean isFirstTouroftheDay()
	{
		return this.getIndex()==day.getLowestTourIndex();
	}
	

  
  /**
   * returns tour duration including default trip durations
   * 
   * @return
   */
  public int getTourDuration()
  {
  	return getActDuration() + getTripDuration();
  }
  
  /**
   * 
   * returns activity durations of this tour only (without default trip durations)
   * 
   * @return
   */
  public int getActDuration()
  {
    int sum = 0;
    for(HActivity act : activities)
    {       	
      sum += (act.durationisScheduled() ? act.getDuration() : 0);
    }
    return sum;
  }
  
  /**
   * 
   * returns trip durations of this tour only (without activity durations)
   *     
   * @return
   */
  public int getTripDuration()
  {
    int sum = 0;
    for(HActivity act : activities)
    {
    	sum += (act.tripBeforeActivityisScheduled() ? act.getEstimatedTripTimeBeforeActivity() : 0);
    	sum += (act.tripAfterActivityisScheduled() ? act.getEstimatedTripTimeAfterActivity() : 0);    	
    }
    return sum;
  }
  

  public int getEndTime()
  {
  	return getLastActivityInTour().getEndTime() + getLastActivityInTour().getEstimatedTripTimeAfterActivity();
  }

  /**
   * @param index
   * @return
   */
  public HActivity getActivity(int index)
  {
  	HActivity indexact = null;
  	for (HActivity activity : getActivities())
  	{
  		if (activity.getIndex()==index)
  		{
  			indexact = activity;
  		}
  	}
  	assert indexact != null : "could not find activity with index " + index;
  	return indexact;
  	
  }
  
  
  public int getLowestActivityIndex()
  {
      int min = +99;
      for(HActivity act : this.activities)
      {
          if(act.getIndex() < min) min = act.getIndex();
      }
      assert min<=0 : "minimum activity index of this tour is > 0 - index: " + min;
      return min;
  }
     
  public int getHighestActivityIndex()
  {
      int max = -99;
      for(HActivity act : this.activities)
      {
          if(act.getIndex() > max) 
          {
          	max = act.getIndex();
          }
      }
      assert max>=0 : "maximum activity index of this tour is < 0  - index: " + max;
      return max;
  }


  public HActivity getFirstActivityInTour()
  {
  	return getActivity(getLowestActivityIndex());    	
  }

  public HActivity getLastActivityInTour()
  {
  	return getActivity(getHighestActivityIndex());    	
  }
  
  public int getAmountOfActivities()
  {
  	return getActivities().size();
  }

  public int getStartTimeWeekContext()
  {
  	return (1440*getDay().getIndex()) + getStartTime();
  }
  
  public int getEndTimeWeekContext()
  {
  	return (1440*getDay().getIndex()) + getEndTime();
  }


	/**
	 * return the previous tour in the pattern
	 * (last tour of the previous day of previous tour of this day)
	 * 
	 * @return
	 */
	public HTour getPreviousTourinPattern()
	{
		HTour previousTour;
    if (index == day.getLowestTourIndex())
    {
    	HDay previousDaywithTour = day;
    	do 
  		{
    		previousDaywithTour = previousDaywithTour.getPreviousDay();	
  			if (previousDaywithTour!=null && !previousDaywithTour.isHomeDay()) break;
  		}
  		while(previousDaywithTour!=null);
    	
    	if (previousDaywithTour==null)
    	{
    		previousTour = null;
    	}
    	else
    	{
    		previousTour = previousDaywithTour.getLastTourOfDay();
    	}  	
    }
    else
    {
    	previousTour = day.getTour(index-1);
    }
    return previousTour;
	}
	
	/**
	 * 
	 * returns the next tour in the pattern
	 * (first tour of the next day or next tour of this day)
	 * 
	 * @return
	 */
	public HTour getNextTourinPattern()
	{
		HTour nextTour;
    if (index == day.getHighestTourIndex())
    {
    	HDay nextDaywithTour = day;
    	do 
  		{
    		nextDaywithTour = nextDaywithTour.getNextDay();	
  			if (nextDaywithTour!=null && !nextDaywithTour.isHomeDay()) break;
  		}
  		while(nextDaywithTour!=null);

    	if (nextDaywithTour==null)
    	{
    		nextTour = null;
    	}
    	else
    	{
    		nextTour = nextDaywithTour.getFirstTourOfDay();
    	}  	
    }
    else
    {
    	nextTour = day.getTour(index+1);
    }
    return nextTour;
	}
	
	/**
	 * 
	 * @param name specific attribute from map
	 * @return
	 */
	public double getAttributefromMap(String name) {
		return this.attributes.get(name);
	}

/**
 * 
 * @param name specific attribute for map
 * @param value
 */
	public void addAttributetoMap(String name, Double value) {
		this.attributes.put(name, value);
	}

	/**
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
	
}
