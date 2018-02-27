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
	// Enthält alle Attribute, die nicht direkt über Variablen ausgelesen werden können
	private Map<String, Double> attributes;
	
  private HDay day;
  private List<HActivity> activities;
  private int index		= -99;
  private int starttime 	= -1;

   
  /**
   * 
   * Konstruktor
   *   
   * @param parent
   * @param index
   */
  public HTour(HDay parent, int index)
  {
  	assert parent!=null : "Tag nicht initialisiert";
    this.day = parent;
    setIndex(index);  
    this.activities = new ArrayList<HActivity>();
    
    this.attributes = new HashMap<String, Double>();
  }
    

    
  public HDay getDay()
	{
		assert day != null : "Tag nicht initialisiert";
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
		assert activities != null : "Aktivitätenliste nicht initialisiert";
	    return activities;
	}

  public void addActivity(HActivity act)
  {
  	assert act.getIndex()!=-99 : "Index der Tour nicht initialisiert";
  	boolean actindexexisitiert = false;
  	for (HActivity tmpact : activities)
  	{
  		if (tmpact.getIndex() == act.getIndex()) actindexexisitiert = true;
  	}
  	assert !actindexexisitiert : "Es gibt bereits eine Tour mit diesem Index";
  	activities.add(act);
  }

	public int getIndex()
	{
		assert index != -99 : "Index nicht initilialisiert";
	    return index;
	}


	public void setIndex(int index)
	{
	    this.index = index;
	}


	public int getStartTime()
	{
		assert starttime >= 0 : "Startzeit negativ - Startzeit: " + starttime;
	    return starttime;
	}


	public void setStartTime(int chosenStartTime)
	{
		assert chosenStartTime >= 0 : "Startzeit negativ - Startzeit: " + chosenStartTime;
	    this.starttime = chosenStartTime;
	}


	/**
	 * 
	 * Sortierung einer Tourliste nach Index
	 * 
	 * @param list
	 */
	public static void sortTourList(List<HTour> list)
	{
		assert list != null : "Liste zum Sortieren ist leer";
		
	    Collections.sort(list, new Comparator<HTour>()
	    {
	        @Override
	        public int compare(HTour o1, HTour o2)
	        {
	            if(o1.getIndex() < o2.getIndex()) return -1;
	            if(o1.getIndex() >o2.getIndex()) return 1;
	            return 0;
	        }
	    });
	}


	@Override
    public String toString()
    {
			String tostring = "";
			if (this.isScheduled())
			{
				tostring = 	"Start " + getStartTimeWeekContext() + 
	    							" Ende " + getEndTimeWeekContext() + 
	    							" Dauer: " + getTourDuration();  
			}
			else
			{
				tostring = 	"Start --- " + 
										" Ende --- " +  
										" Dauer: " + getTourDuration(); 
			}
    	return 	tostring;
    }   
	
	/**
	 * 
	 * Prüft, ob die Startzeit der Tour bereits festgelegt wurde
	 * 
	 * @return
	 */
	public boolean isScheduled()
	{
		return starttime!=-1;
	}
	
    
    /**
     * 
     * Gibt die Tourduration inkl. default Trip times zurück
     * (Dauer_Akt) + (Anz_Akt+1)*DefaultTripTime
     * 
     * @return
     */
    public int getTourDuration()
    {
        int sum = 0;
        for(HActivity act : activities)
        {       	
            sum += act.getDuration() + act.getEstimatedTripTime();
        }
        sum+= getActivity(getHighestActivityIndex()).getEstimatedTripTimeAfterActivity();
        
        return sum;
    }
    
    /**
     * 
     * Gibt die reine Aktivitätenzeit auf der Tour zurück
     * 
     * @return
     */
    public int getActDuration()
    {
        int sum = 0;
        for(HActivity act : activities)
        {       	
            sum += act.getDuration();
        }
        
        return sum;
    }
    
    /**
     * 
     * Gibt die Endzeit der Tour zurück
     * 
     * @return
     */
    public int getEndTime()
    {
    	return getStartTime() + getTourDuration();
    }

    /**
     * 
     * Gibt explizit die Aktivität mit dem gesuchten Index zurück
     * 
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
    	assert indexact != null : "Aktivität konnte nicht gefunden werden";
    	return indexact;
    	
    }
    
    /**
     *
     * Gibt den Index der erste Aktivität der Tour zurück
     * 
     * @return
     */
    public int getLowestActivityIndex()
    {
        int min = +99;
        for(HActivity act : this.activities)
        {
            if(act.getIndex() < min) min = act.getIndex();
        }
        assert min<=0 : "minimaler AktIndex der Tour ist größer 0 - index: " + min;
        return min;
    }
    
    
    /**
    *
    * Gibt den Index der letzten Aktivität der Tour zurück
    * 
    * @return
    */
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
        assert max>=0 : "maximaler AktIndex der Tour ist kleiner 0 - index: " + max;
        return max;
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
	 * 
	 * Gibt die vorherige Tour im Pattern zurück
	 * (letzte Tour des Vortags oder vorherige Tour des aktuellen Tags)
	 * 
	 * @return
	 */
	public HTour getPreviousTour()
	{
		HTour previousTour;
		//Prüfe, ob die Tour die erste Tour des Tages ist
	    if (index == day.getLowestTourIndex())
	    {
	    	// Tour ist die erste des Tages, gib also die letzte Tour des vorherigen Tages zurück
	    	HDay previousDay = day.getPreviousDay();
	    	// Wenn der vorherige Tag eine HomeDay war oder es keinen vorherigen Tag gibt, gib Null zurück
	    	if (previousDay==null || previousDay.isHomeDay())
	    	{
	    		previousTour = null;
	    	}
	    	else
	    	{
	    		previousTour = previousDay.getLastTourOfDay();
	    	}
	    }
	    // Falls nicht wird die vorherige Tour dieses Tages zurückgegeben
	    else
	    {
	    	previousTour = day.getTour(index-1);
	    }
	    return previousTour;
	}

	/**
	 * @param attributes spezifischesAttribut für Map
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
	
}
