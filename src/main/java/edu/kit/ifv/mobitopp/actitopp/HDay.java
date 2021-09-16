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
public class HDay
{
	
	//stores all attributes that are not directly accessible by variables	
	private Map<String, Double> attributes;
	
	private HWeekPattern pattern;
  private int weekday;
  private List<HTour> tours;
    
   
  /**
   * 
   * Constructor
   * 
   * @param parent
   * @param weekday
   */
  public HDay(HWeekPattern parent, int weekday)
  {
		assert parent != null : "pattern is not initialized";
    this.pattern = parent;
    setWeekday(weekday);       
      
  	tours = new ArrayList<HTour>();
  	this.attributes = new HashMap<String, Double>();
  }
    
  public HWeekPattern getPattern() 
	{
		assert pattern != null : "pattern is not initialized";
		return pattern;
	}
  
  public ActitoppPerson getPerson()
  {
  	return getPattern().getPerson();
  }

  public List<HTour> getTours()
	{
		assert tours != null : "list of tours is not initialized";
	  return tours;
	}
  
  
  public void addTour(HTour tour)
  {
  	assert tour.getIndex()!=-99 : "index of the tour is not initialized";
  	boolean tourindexexisitiert = false;
  	for (HTour tmptour : tours)
  	{
  		if (tmptour.getIndex() == tour.getIndex()) tourindexexisitiert = true;
  	}
  	assert !tourindexexisitiert : "a tour using this index already exists";
  	tours.add(tour);
  }
	
	/**
	 * 
	 * returns the weekday number
	 * 1 - Monday
	 * 2 - Tuesday
	 * 3 - Wednesday
	 * 4 - Thursday
	 * 5 - Friday
	 * 6 - Saturday
	 * 7 - Sunday
	 * 
	 * @return
	 */
	public int getWeekday() 
	{
		assert this.weekday>=1 && this.weekday<=7 : "week day is out of range - weekday: " + weekday;
		return this.weekday;
	}


	public void setWeekday(int weekday) 
	{
		assert weekday>=1 && weekday<=7 : "week day is out of range - weekday: " + weekday;
		this.weekday = weekday;
	}


	@Override
  public String toString()
  {
  	return 	"Wochentag " + getWeekday() + 
  			" #Touren " + getAmountOfTours() + 
  			" Haupttour: " + getMainTourType(); 	
  }
    
	public String printDayPattern()
	{
		String result="Day " + getIndex() + " // ";
		
		for (HActivity tmpact : getAllActivitiesoftheDay())
		{
			result = result + " " + tmpact.getIndex() + " " + tmpact.getActivityType().getTypeasChar(); 
			if (tmpact.isActivityLastinTour()) result = result + " //";		
		}
		return result;
	}
	
  public boolean isHomeDay()
  {
  	return (getAmountOfTours()==0);
  }
  
  public List<HActivity> getAllActivitiesoftheDay()
  {
  	List<HActivity> allactivities = new ArrayList<HActivity>();
  	for (HTour tmptour : this.getTours())
  	{
  		allactivities.addAll(tmptour.getActivities());
  	}
  	return allactivities;
  }
    
  public ActivityType getMainTourType()
	{
		return this.getTour(0).getActivity(0).getActivityType();
	}


	/**
   * returns the amount of activities without home activities
   * 
   * @return
   */
  public int getTotalAmountOfActivitites()
  {
      int sum = 0;
      for(HTour tour : this.tours)
      {
          sum += tour.getAmountOfActivities();
      }
      
      return sum;
  }
   
  public int getTotalNumberOfActivitites(ActivityType acttype)
  {
      int sum = 0;
      for(HTour tour : this.tours)
      {
          for(HActivity act : tour.getActivities())
          {
          	if (act.activitytypeisScheduled() && act.getActivityType()== acttype) sum++;
          }
      }
      return sum;
  }
  
  public int getHighestTourIndex()
  {
  	int index=-99;
  	for (HTour tour : this.tours)
  	{
  		if (tour.getIndex()>index)
  		{
  			index = tour.getIndex();
  		}
  	}
  	assert index>=0 : "maximum tour index is below 0 - index: " + index;
  	return index;
  }

  
  public int getLowestTourIndex()
  {
  	int index=+99;
  	for (HTour tour : this.tours)
  	{
  		if (tour.getIndex()<index)
  		{
  			index = tour.getIndex();
  		}
  	}
  	assert index<=0 : "minimum tour index is over 0 - index: " + index;
  	return index;
  }
  
  public HTour getFirstTourOfDay()
  {
  	return getTour(getLowestTourIndex());
  }
  
  public HTour getLastTourOfDay()
  {
  	return getTour(getHighestTourIndex());
  }
  
  /**
   * 
   * @param index
   * @return
   */
  public HTour getTour(int index)
  {
  	HTour indextour = null;
  	for (HTour tour : this.tours)
  	{
  		if (tour.getIndex()==index)
  		{
  			indextour = tour;
  		}
  	}
  	assert indextour != null : "could not determine tour - index: " + index;
  	return indextour;
  }  
  
  
  /**
   * 
   * @param index
   * @return
   */
  public boolean existsTour(int index)
  {
  	boolean result = false;
  	HTour indextour = null;
  	for (HTour tour : this.tours)
  	{
  		if (tour.getIndex()==index)
  		{
  			indextour = tour;
  		}
  	}
  	if (indextour != null) result=true;
  	return result;
  }  
  
  
  /**
   * check if activity given tour and activity index exists on that day
   * 
   * @param tourindex
   * @param activityindex
   * @return
   */
  public boolean existsActivity(int tourindex, int activityindex)
  {
  	boolean result = false;  	
  	if (existsTour(tourindex)) 
  	{
  		HActivity indexact = null;
    	for (HActivity act : getTour(tourindex).getActivities())
    	{
    		if (act.getIndex()==activityindex)
    		{
    			indexact = act;
    		}
    	}
    	if (indexact != null) result=true;
  	}  	
  	return result;
  }  
  
  /**
   * check if activity given tour and activity index exists on that day and has a scheduled activity type
   * 
   * @param tourindex
   * @param activityindex
   * @return
   */
  public boolean existsActivityTypeforActivity(int tourindex, int activityindex)
	{
		boolean result = false;
		if (existsActivity(tourindex, activityindex) && getTour(tourindex).getActivity(activityindex).activitytypeisScheduled()) result=true;		
		return result;
	}

	public int getAmountOfTours()
  {
  	assert tours != null : "list of tour is not initialized";
    return tours.size();
  }

    
  /**
   * returns total activity duration on that day
   * 
   * @return
   */
  public int getTotalAmountOfActivityTime()
  {
    int totalTime = 0;
    for(HTour tour: this.tours)
    {
      totalTime += tour.getActDuration();
    }
    return totalTime;
  }
  
  /**
   * returns total trip duration on that day
   * 
   * @return
   */
  public int getTotalAmountOfTripTime()
  {
    int totalTime = 0;
    for(HTour tour: this.tours)
    {
      totalTime += tour.getTripDuration();
    }
    return totalTime;
  }
  
   
  /**
   * returns total tour time (activities + trips) from reference tour until last tour of the day 
   * 
   * @param referencePoint
   * @return
   */
  public int getTotalAmountOfRemainingTourTime(HTour referencePoint)
  {
    int totalTime = 0;

    for (int i= referencePoint.getIndex(); i<=getHighestTourIndex(); i++)
  	{
  		totalTime += this.getTour(i).getTourDuration();
  	}  
    return totalTime;   
  }
  
  /**
   * returns total tour time (activities + trips) from reference tour until main tour of the day 
   * 
   * @param referencePoint
   * @return
   */
  public int getTotalAmountOfTourTimeUntilMainTour(HTour referencePoint)
  {
    int totalTime = 0;

    for (int i= referencePoint.getIndex(); i<0; i++)
  	{
  		totalTime += this.getTour(i).getTourDuration();
  	}      
    return totalTime;        
  }
  
  
  /**
   * 
   * returns total activity time (without trips) from reference tour until last tour of the day 
   * 
   * @param referencePoint
   * @return
   */
  public int getTotalAmountOfRemainingActivityTime(HTour referencePoint)
  {
    int totalTime = 0;

    for (int i= referencePoint.getIndex(); i<=getHighestTourIndex(); i++)
  	{
  		totalTime += this.getTour(i).getActDuration();
  	} 
    return totalTime;   
  }
  
  /**
   * 
   * returns total activity time (without trips) from reference tour until main tour of the day
   * 
   * @param referencePoint
   * @return
   */
  public int getTotalAmountOfActivityTimeUntilMainTour(HTour referencePoint)
  {
    int totalTime = 0;

    for (int i= referencePoint.getIndex(); i<0; i++)
  	{
  		totalTime += this.getTour(i).getActDuration();
  	}  
    return totalTime;        
  }


  /**
   * returns the weekday index
   * 
	 * 0 - Monday
	 * 1 - Tuesday
	 * 2 - Wednesday
	 * 3 - Thursday
	 * 4 - Friday
	 * 5 - Saturday
	 * 6 - Sunday
   * 
   * @return
   */
	public int getIndex() 
	{
		return getWeekday()-1;
	}
	
	public HDay getPreviousDay()
	{
		HDay previousDay;
		
		if (getIndex()==0)
		{
			previousDay = null;
		}
		else
		{
			previousDay = getPattern().getDay(getIndex()-1);
		}
		
		return previousDay;
	}
	
	public HDay getNextDay()
	{
		HDay nextDay;
		
		if (getIndex()==6)
		{
			nextDay = null;
		}
		else
		{
			nextDay = getPattern().getDay(getIndex()+1);
		}
		
		return nextDay;
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
	 * @return the attributes
	 */
	public Map<String, Double> getAttributesMap() {
		return attributes;
	}

	public int calculatedurationofmainactivitiesonday()
	{
		int totalActivityTime = 0;
    for(HTour tour : this.getTours())
    {
    	totalActivityTime+=tour.getActivity(0).getDuration();
    }
    return totalActivityTime;
	}

}
