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
	
	// Enth�lt alle Attribute, die nicht direkt �ber Variablen ausgelesen werden k�nnen
	private Map<String, Double> attributes;
	
	private HWeekPattern pattern;
  private int weekday;
  private List<HTour> tours;
    
   
  /**
   * 
   * Konstruktor
   * 
   * @param parent
   * @param weekday
   */
  public HDay(HWeekPattern parent, int weekday)
  {
		assert parent != null : "Pattern nicht initialisiert";
    this.pattern = parent;
    setWeekday(weekday);       
      
  	tours = new ArrayList<HTour>();
  	this.attributes = new HashMap<String, Double>();
  }
    
  public HWeekPattern getPattern() 
	{
		assert pattern != null : "Pattern nicht initialisiert";
		return pattern;
	}
  
  public ActitoppPerson getPerson()
  {
  	return getPattern().getPerson();
  }

  public List<HTour> getTours()
	{
		assert tours != null : "Tourliste nicht initilaisiert";
	  return tours;
	}
  
  
  public void addTour(HTour tour)
  {
  	assert tour.getIndex()!=-99 : "Index der Tour nicht initialisiert";
  	boolean tourindexexisitiert = false;
  	for (HTour tmptour : tours)
  	{
  		if (tmptour.getIndex() == tour.getIndex()) tourindexexisitiert = true;
  	}
  	assert !tourindexexisitiert : "Es gibt bereits eine Tour mit diesem Index";
  	tours.add(tour);
  }
	
	/**
	 * 
	 * Gibt den Wochentag zur�ck
	 * 1 - Montag
	 * 2 - Dienstag
	 * 3 - Mittwoch
	 * 4 - Donnerstag
	 * 5 - Freitag
	 * 6 - Samstag
	 * 7 - Sonntag
	 * 
	 * @return
	 */
	public int getWeekday() 
	{
		assert weekday>=1 && weekday<=7 : "Wochentag nicht im richtigen Bereich - weekday: " + weekday;
		return weekday;
	}


	public void setWeekday(int weekday) 
	{
		assert weekday>=1 && weekday<=7 : "Wochentag nicht im richtigen Bereich - weekday: " + weekday;
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
	
	
  /**
   * 
   * Abfrage, ob der Tag ein HomeDay ist
   * 
   * @return
   */
  public boolean isHomeDay()
  {
  	return (getAmountOfTours()==0);
  }
  
  /**
   * 
   * Liste mit allen Akivitit�ten des Tages
   * 
   * @return
   */
  public List<HActivity> getAllActivitiesoftheDay()
  {
  	List<HActivity> allactivities = new ArrayList<HActivity>();
  	for (HTour tmptour : this.getTours())
  	{
  		allactivities.addAll(tmptour.getActivities());
  	}
  	return allactivities;
  }
    
  /**
   * 
   * Gibt den Haupttourtyp des Tages zur�ck
   * 
   * @return
   */
  public ActivityType getMainTourType()
	{
		return this.getTour(0).getActivity(0).getActivityType();
	}


	/**
   * 
   * Gibt die Anzahl an Aktivit�ten zur�ck - ohne Home-Aktivit�ten
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
    
  /**
   * 
   * Gibt die Anzahl an Aktivit�ten eines spezifischen Typs zur�ck
   * 
   * @param acttype
   * @return
   */
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
  	assert index>=0 : "maximaler  TourIndex des Tages ist kleiner 0 - index: " + index;
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
  	assert index<=0 : "minimaler TourIndex des Tages ist gr��er 0 - index: " + index;
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
   * Gibt explizit die Tour mit dem gesuchten Index zur�ck
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
  	assert indextour != null : "Tour konnte nicht gefunden werden - Index: " + index;
  	return indextour;
  }  
  
  
  /**
   * 
   * Pr�ft, ob Tour existiert
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
   * 
   * Pr�ft, ob Aktivit�t mit dem Tour- und Aktivit�tsindex als Objekt existiert
   * 
   * @param index
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
   * 
   * Pr�ft ob f�r die Aktivit�t mit dem Tour- und Aktivit�tsindex bereits ein Aktivit�tstyp festgelegt wurde.
   * Falls die Aktivit�t selbst noch nicht existiert, wird ebenfalls false zur�ckgegeben
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
  	assert tours != null : "Tourliste nicht initilaisiert";
      return tours.size();
  }

    
  /**
   * 
   * Gibt die gesamte Aktivit�tszeit aller Touren an diesem Tag zur�ck
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
   * 
   * Gibt die gesamte Wegzeit aller Touren an diesem Tag zur�ck
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
   * 
   * Gibt die Zeit f�r Touren bis zur letzten Tour des Tages zur�ck (inkl. default-Trip-Times)
   * 
   * @param referencePoint
   * @return
   */
  public int getTotalAmountOfRemainingTourTime(HTour referencePoint)
  {
    int totalTime = 0;
    
    // Starte mit der Referenztour und addiere alle Tourdauern bis zur letzten Tour des Tages
    for (int i= referencePoint.getIndex(); i<=getHighestTourIndex(); i++)
  	{
  		totalTime += this.getTour(i).getTourDuration();
  	}  
    return totalTime;   
  }
  
  /**
   * 
   * Gibt die Zeit f�r Touren bis zur Haupttour zur�ck (inkl. default-Trip-Times)
   * 
   * @param referencePoint
   * @return
   */
  public int getTotalAmountOfTourTimeUntilMainTour(HTour referencePoint)
  {
    int totalTime = 0;

    // Starte mit der Referenztour und addiere alle Tourdauern bis zur Haupttour
    for (int i= referencePoint.getIndex(); i<0; i++)
  	{
  		totalTime += this.getTour(i).getTourDuration();
  	}      
    return totalTime;        
  }
  
  
  /**
   * 
   * Gibt die Zeit f�r Touren bis zur letzten Tour des Tages zur�ck (reine Aktivit�tenzeit)
   * 
   * @param referencePoint
   * @return
   */
  public int getTotalAmountOfRemainingActivityTime(HTour referencePoint)
  {
    int totalTime = 0;
    
    // Starte mit der Referenztour und addiere alle Aktdauern bis zur letzten Tour des Tages
    for (int i= referencePoint.getIndex(); i<=getHighestTourIndex(); i++)
  	{
  		totalTime += this.getTour(i).getActDuration();
  	} 
    return totalTime;   
  }
  
  /**
   * 
   * Gibt die Zeit f�r Touren bis zur Haupttour zur�ck (reine Aktivit�tenzeit)
   * 
   * @param referencePoint
   * @return
   */
  public int getTotalAmountOfActivityTimeUntilMainTour(HTour referencePoint)
  {
    int totalTime = 0;

    // Starte mit der Referenztour und addiere alle Aktdauern bis zur Haupttour
    for (int i= referencePoint.getIndex(); i<0; i++)
  	{
  		totalTime += this.getTour(i).getActDuration();
  	}  
    return totalTime;        
  }


  /**
   * 
   * Gibt den Wochentag als Feldindex zur�ck
   * 0 - Montag
   * 1 - Dienstag
   * 2 - Mittwoch
   * 3 - Donnerstag
   * 4 - Freitag
   * 5 - Samstag
   * 6 - Sonntag
   * 
   * @return
   */
	public int getIndex() 
	{
		return getWeekday()-1;
	}
	
  /**
	 * 
	 * Gibt den vorherigen Tag als Objekt zur�ck
	 * 
	 * @return
	 */
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
	
	/**
	 * 
	 * Gibt den folgenden Tag als Objekt zur�ck
	 * 
	 * @return
	 */
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
	 * @param attributes spezifischesAttribut f�r Map
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

	/**
	 * Methode berechnet die Dauern aller Hauptaktivit�ten von Touren des Tages
	 * 
	 * @return
	 */
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
