package edu.kit.ifv.mobitopp.actitopp;


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
	
	// Enthält alle Attribute, die nicht direkt über Variablen ausgelesen werden können
	private Map<String, Double> attributes;
		
	private HDay day;	
	private HTour tour;
	
  private int index								= -99;
  private char type								= 'x';
  private int duration 						= -1;
  private int starttime 					= -1;
  
  
  private HTrip tripbeforeactivity;
  private HTrip tripafteractivity;
  
  
//  private int estimatedTripTime 	= -1;
  
  private int jointStatus					= -1;

  /*
   * erwartete Wegezeit nach der Aktivität
   * nur relevant, falls Aktivität die letzte auf der Tour ist.
   */
//  private int estimatedTripTimeAfterActivity = -1;
  
  private byte mobiToppActType		= -1;
           

	/**
   * 
   * Konstruktor
   * 
   * @param parent
   * @param index
   */
	public HActivity(HTour parent, int index)
	{
		assert parent!=null : "Tour nicht initialisiert";
    this.tour = parent;
    
    this.day = parent.getDay();
    setIndex(index);
    
    this.attributes = new HashMap<String, Double>();
	}  
	
	/**
	 * 
	 * Konstruktor
	 * 
	 * @param parent
	 * @param index
	 * @param type
	 */
	public HActivity(HTour parent, int index, char type)
	{
	    this(parent, index);
	    setType(type);
	}  
	
	/**
	 * 
	 * Konstruktor für Aktivitäten, wo nur Akt gemeinsam durchgeführt wird
	 *
	 * @param parent
	 * @param index
	 * @param type
	 * @param duration
	 * @param starttime
	 */
	public HActivity(HTour parent, int index, char type, int duration, int starttime, int jointStatus)
	{
	    this(parent, index, type);
	    setDuration(duration);
	    setStartTime(starttime);
	    setJointStatus(jointStatus);
	}  
	
	/**
	 * 
	 * Konstruktor für Aktivitäten, wo Akt und Weg gemeinsam durchgeführt werden
	 *
	 * @param parent
	 * @param index
	 * @param type
	 * @param duration
	 * @param starttime
	 * @param jointStatus
	 * @param tripdurationbefore
	 */
	public HActivity(HTour parent, int index, char type, int duration, int starttime, int jointStatus, int tripdurationbefore)
	{
	    this(parent, index, type, duration, starttime, jointStatus);
	    tripbeforeactivity = new HTrip(this, tripdurationbefore);
	}  
	
	/**
	 * 
	 * Konstruktor für Aktivitäten, wo nur Weg dahin gemeinsam durchgeführt wird
	 *
	 * @param parent
	 * @param index
	 * @param starttime
	 * @param jointStatus
	 * @param tripdurationbefore
	 */
	public HActivity(HTour parent, int index, int starttime, int jointStatus, int tripdurationbefore)
	{
	    this(parent, index);
	    setStartTime(starttime);
	    setJointStatus(jointStatus);
	    tripbeforeactivity = new HTrip(this, tripdurationbefore);
	}  
	
	

	/**
	 * 
	 * Konstruktor für Home-Aktivitäten 
	 * 
	 * @param parent
	 * @param ActType
	 * @param duration
	 * @param starttime
	 */
	public HActivity(HDay parent, char ActType, int duration, int starttime)
	{
		assert parent!=null : "Tag nicht initialisiert";
		this.day = parent;
	  setType(ActType);
		setDuration(duration);
		setStartTime(starttime);
		setJointStatus(4);
	}



	public HTour getTour() 
	{
		return tour;
	}
	
	public HDay getDay() 
	{
		assert day != null : "Tag nicht initialisiert";
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
		assert index != -99 : "Index ist zum Zugriffszeitpunkt nicht initialisiert";
	    return index;
	}

	public void setIndex(int index)
	{
	    this.index = index;
	}

	public char getType() 
	{
		assert Configuration.ACTIVITY_TYPES.contains(type) || type=='H' : "ungültige Aktivität - nicht intialisiert oder nicht in ACTIVITYTYPES enthalten aktueller Wert:" + type;
		return type;
	}

	public void setType(char type) 
	{
		assert Configuration.ACTIVITY_TYPES.contains(type) || type=='H' : "ungültige Aktivität - nicht in ACTIVITYTYPES enthalten - Typ: " + type; 
		this.type = type;
	}

	public int getDuration() 
	{
		assert duration != -1 : "Duration ist zum Zugriffszeitpunkt nicht initialisiert";
		return duration;
	}

	public void setDuration(int duration) 
	{
		assert duration>0 : "zu setzende Dauer ist nicht größer 0 - Dauer:" + duration;
		this.duration = duration;
	}

	public int getStartTime() 
	{
		assert starttime != -1 : "Startzeit ist zum Zugriffszeitpunkt nicht initialisiert - Startzeit: " + starttime;
		return starttime;
	}

	public void setStartTime(int starttime) 
	{
		assert starttime>=0 : "zu setzende Startzeit ist negativ- Startzeit: " + starttime;
		this.starttime = starttime;
	}

	public int getJointStatus() {
		assert jointStatus!=-1 : "jointStatus not set";
		return jointStatus;
	}

	public void setJointStatus(int jointStatus) {
		assert jointStatus>=1 && jointStatus<=4 : "invalid value for jointStatus - actual value: " + jointStatus;
		this.jointStatus = jointStatus;
	}

	public int getEstimatedTripTimeBeforeActivity() 
	{
		assert tripBeforeActivityisScheduled() : "Trip vor der Aktvitität ist zum Zugriffszeitpunkt nicht initialisiert";
		int tmptriptimebefore = tripbeforeactivity.getTripduration();
		return tmptriptimebefore;
	}
/*
	private void setEstimatedTripTime(int estimatedTripTime) 
	{
		assert estimatedTripTime>0 || (estimatedTripTime==0 && getType()=='H') : "zu setzende Wegzeit ist nicht größer 0";
		this.estimatedTripTime = estimatedTripTime;
	}
*/
	/**
	 * @return the estimatedTripTimeAfterActivity
	 */
	public int getEstimatedTripTimeAfterActivity() 
	{
		assert tripAfterActivityisScheduled() : "Trip nach der Aktvitität ist zum Zugriffszeitpunkt nicht initialisiert // nur bei letzter Aktivität in Tour gesetzt";
		int tmptriptimeafter = tripafteractivity.getTripduration();
		return tmptriptimeafter;
	}

/*
	private void setEstimatedTripTimeAfterActivity(int estimatedTripTimeAfterActivity) 
	{
		assert (estimatedTripTimeAfterActivity==0 && !isActivityLastinTour()) || (estimatedTripTimeAfterActivity>0 && isActivityLastinTour()) : "zu setzende Wegzeit muss 0 (bei nicht letzter Aktivität in Tour) oder größer 0 (bei letzter Aktivität in der Tour) sein";
		this.estimatedTripTimeAfterActivity = estimatedTripTimeAfterActivity;
	}
*/
	
	public byte getMobiToppActType() 
	{
		assert Configuration.ACTIVITY_TYPES_mobiTopp.contains(mobiToppActType) : "ungültige Aktivität - nicht in ACTIVITYTYPES_mobiTopp enthalten"; 
		return mobiToppActType;
	}

	public void setMobiToppActType(byte mobiToppActType) 
	{
		assert Configuration.ACTIVITY_TYPES_mobiTopp.contains(mobiToppActType) : "ungültige Aktivität - nicht in ACTIVITYTYPES_mobiTopp enthalten"; 
		this.mobiToppActType = mobiToppActType;
	}
	
	/**
   * Sortiert eine Liste mit Aktivitäten chronologisch im Wochenverlauf
   * 
   * @param actList
   */
  public static void sortActivityListInWeekOrder(List<HActivity> actList)
  {
  	assert actList != null : "Liste zum Sortieren ist leer";
  	
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
   * Methode zum Sortieren der Aktivitätenliste nach Indizes
   * 
   * @param list
   */
  public static void sortActivityList(List<HActivity> list)
  {
  	assert list != null : "Liste zum Sortieren ist leer";
  		
      Collections.sort(list, new Comparator<HActivity>()
      {
        @Override
        public int compare(HActivity o1, HActivity o2)
        {
        	int result = 99;
        	if			(o1.getIndexDay() <  o2.getIndexDay()) result = -1;
        	else if	(o1.getIndexDay() >  o2.getIndexDay()) result = +1;
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
        
/*        
          @Override
          public int compare(HActivity o1, HActivity o2)
          {
              if(o1.getIndex() < o2.getIndex()) return -1;
              if(o1.getIndex() >o2.getIndex()) return 1;
              return 0;
          }
*/
      });
  }
  
  /**
   * Vergleicht zwei Aktivitäten in ihrer Reihenfolge
   * 
   * 0  - beide Aktivitäten sind gleich
   * 1  - übergebene Aktivität liegt NACH der anderen Aktivität (höherer Tag, Tour oder Aktindex)
   * -1 - übergebene Aktivität liegt VOR der anderen Aktivität
   * 
   * @param acttocompare
   * @return
   */
  public int compareTo(HActivity acttocompare) {
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
  		result= getIndexDay() + 	
  				" Start " + (startTimeisScheduled() ? getStartTimeWeekContext() : "n.a.") + 
  				" Ende " + (startTimeisScheduled() && durationisScheduled() ? getEndTimeWeekContext() : "n.a.") + 
  				" Dauer: " + (durationisScheduled() ? this.duration : "n.a.") + 
  				" Typ: " + (activitytypeisScheduled() ? this.type : "n.a.") + " (" + this.mobiToppActType + ")" + 
  				" jointStatus: " + this.jointStatus
  				;  		
  	}
  	else
  	{
  		result= getIndexDay() + "/" + getTour().getIndex() + "/" + getIndex() + 	
		  				" Start " + (startTimeisScheduled() ? getStartTimeWeekContext() : "n.a.") + 
		  				" Ende " + (startTimeisScheduled() && durationisScheduled() ? getEndTimeWeekContext() : "n.a.") + 
		  				" Dauer: " + (durationisScheduled() ? this.duration : "n.a.") + 
		  				" Typ: " + (activitytypeisScheduled() ? this.type : "n.a.") + " (" + this.mobiToppActType + ")" + 
		  				" jointStatus: " + this.jointStatus +
		  				" Weg hin: " + (tripBeforeActivityisScheduled() ? getEstimatedTripTimeBeforeActivity() : "n.a.") + 
		  				" Weg rück: " + (tripAfterActivityisScheduled() ? getEstimatedTripTimeAfterActivity() : "n.a.")
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
	 * 
	 * Bestimmt, ob vor der Aktivität ein Arbeitspendelweg von Zuhause stattfindet.
	 * Wird im Modellverlauf zur besseren Bestimmung von default-Wegezeiten genutzt
	 * Kann nur sicher belegt werden, ...
	 * 	... wenn es die erste Aktivität der Tour ist
	 *  ... der Zweck "W" ist
	 *  ... die Person eine Arbeitspendelentfernung hat
	 * 
	 * @return
	 */
	public boolean hasWorkCommutingTripbeforeActivity()
	{
		return ((isActivityFirstinTour() && getType()=='W' && (getPerson().getCommutingdistance_work() != 0.0)) ? true : false); 
	}
	
	/**
	 * 	 
	 * Bestimmt, ob nach der Aktivität ein Arbeitspendelweg nach Zuhause stattfindet.
	 * Wird im Modellverlauf zur besseren Bestimmung von default-Wegezeiten genutzt
	 * Kann nur sicher belegt werden, ...
	 * 	... wenn es die letzte Aktivität der Tour ist
	 *  ... der Zweck "W" ist
	 *  ... die Person eine Arbeitspendelentfernung hat
	 * 
	 * @return
	 */
	public boolean hasWorkCommutingTripafterActivity()
	{
		return ((isActivityLastinTour() && getType()=='W' && (getPerson().getCommutingdistance_work() != 0.0)) ? true : false); 
	}
	
	/**
	 * 
	 * Bestimmt, ob vor der Aktivität ein Bildungspendelweg von Zuhause stattfindet.
	 * Wird im Modellverlauf zur besseren Bestimmung von default-Wegezeiten genutzt
	 * Kann nur sicher belegt werden, ...
	 * 	... wenn es die erste Aktivität der Tour ist
	 *  ... der Zweck "E" ist
	 *  ... die Person eine Bildungspendelentfernung hat
	 *  
	 * @return
	 */
	public boolean hasEducationCommutingTripbeforeActivity()
	{
		return ((isActivityFirstinTour() && getType()=='E' && (getPerson().getCommutingdistance_education() != 0.0)) ? true : false); 
	}
	
	/**
	 * 	 
	 * Bestimmt, ob nach der Aktivität ein Bildungspendelweg nach Zuhause stattfindet.
	 * Wird im Modellverlauf zur besseren Bestimmung von default-Wegezeiten genutzt
	 * Kann nur sicher belegt werden, ...
	 * 	... wenn es die letzte Aktivität der Tour ist
	 *  ... der Zweck "E" ist
	 *  ... die Person eine Bildungspendelentfernung hat
	 * 
	 * @return
	 */
	public boolean hasEducationCommutingTripafterActivity()
	{
		return ((isActivityLastinTour() && getType()=='E' && (getPerson().getCommutingdistance_education() != 0.0)) ? true : false); 
	}
	
	/**
	 * 
	 * Gibt an, ob es sich bei der Aktivität um eine Home-Aktivität handelt
	 * 
	 * @return
	 */
	public boolean isHomeActivity()
	{
		boolean ishome = false;
		if (activitytypeisScheduled() && getType()=='H') ishome = true;
		return ishome;
	}

	/**
	 * 
	 * Gibt an, ob es sich um die Hauptaktivität der Tour handelt
	 * 
	 * @return
	 */
	public boolean isMainActivityoftheTour()
	{
		return this.getIndex()==0;
	}
	
	/**
	 * 
	 * Gibt an, ob es sich um die Hauptaktivität des Tages handelt
	 * 
	 * @return
	 */
	public boolean isMainActivityoftheDay()
	{
		return isMainActivityoftheTour() && getTour().isMainTouroftheDay();
	}
	
	/**
	 * 
	 * Gibt die vorherige Aktivität auf der Tour zurück
	 * 
	 * @return
	 */
	public HActivity getPreviousActivityinTour()
	{
		HActivity previousActivity;
		
		//Prüfe, ob die Aktivtät die erste in der Tour ist
    if (getIndex() != getTour().getLowestActivityIndex())
    {
    	previousActivity = getTour().getActivity(getIndex()-1);
    }
    // Falls die Aktivität die erste ist, gibt es keinen Vorgänger in der Tour
    else
    {
    	previousActivity = null;
    }
    return previousActivity;
	}
	
	/**
	 * 
	 * Gibt die nachfolgende Aktivität auf der Tour zurück
	 * 
	 * @return
	 */
	public HActivity getNextActivityinTour()
	{
		HActivity nextActivity;
		
		//Prüfe, ob die Aktivtät die letzte in der Tour ist
    if (getIndex() != getTour().getHighestActivityIndex())
    {
    	nextActivity = getTour().getActivity(getIndex()+1);
    }
    // Falls die Aktivität die letzte ist, gibt es keinen Nachfolger in der Tour
    else
    {
    	nextActivity = null;
    }
    return nextActivity;
	}
	
	
	/**
	 * 
	 * Gibt die vorherige Außer-Haus-Aktivität des Patterns zurück.
	 * 
	 * @return
	 */
	public HActivity getPreviousOutOfHomeActivityinPattern()
	{
		HActivity previousact=null;
		
		// Aktivität ist die erste in der Tour, das heißt ermittel letzte Aktivität aus vorheriger Tour
		if (isActivityFirstinTour())
		{
			HTour vorherigeTour = getTour().getPreviousTourinPattern();
			if (vorherigeTour!=null) previousact = vorherigeTour.getLastActivityInTour();
		}
		else
		{
			previousact = getPreviousActivityinTour();
		}
		return previousact;
	}

	/**
	 * 
	 * Gibt die nachfolgende Außer-Haus-Aktivität des Patterns zurück.
	 * 
	 * @return
	 */
	public HActivity getNextOutOfHomeActivityinPattern()
	{
		HActivity nextact=null;
		
		// Aktivität ist die letzte in der Tour, das heißt ermittel erste Aktivität aus nachfolgender Tour
		if (isActivityLastinTour())
		{
			HTour naechsteTour = getTour().getNextTourinPattern();
			if (naechsteTour!=null) nextact = naechsteTour.getFirstActivityInTour();
		}
		else
		{
			nextact = getNextActivityinTour();
		}
		return nextact;
	}

	
	
	/**
	 * 
	 * Gibt den Endzeitpunkt der Aktivität zurück
	 * starttime + duration
	 * 
	 * @return
	 */
	public int getEndTime()
	{
		return getStartTime() + getDuration(); 
	}

	/**
	 * 
	 * @return
	 */
	public int getWeekDay()
	{
		int daynumber = -1;
		if (isHomeActivity())
		{
			daynumber = getDay().getWeekday();
		}
		else
		{
			daynumber = getTour().getDay().getWeekday();
		}
		return daynumber;
	}

	/**
	 * 
	 * @return
	 */
	public int getIndexDay()
	{
		int daynumber = -1;
		if (isHomeActivity())
		{
			daynumber = getDay().getIndex();
		}
		else
		{
			//daynumber = getTour().getDay().getIndex();
			daynumber = getDay().getIndex();
		}
		return daynumber;
	}

	public int getStartTimeWeekContext()
	{
		return 1440*getIndexDay() + getStartTime();
	}

	public int getEndTimeWeekContext()
	{
		return getStartTimeWeekContext() + getDuration();
	}

	public int getTripStartTimeBeforeActivity()
	{
		// return getStartTime() - getEstimatedTripTime();
		return getStartTime() - tripbeforeactivity.getTripduration();
	}

	public int getTripStartTimeBeforeActivityWeekContext()
	{
		// return getStartTimeWeekContext() - getEstimatedTripTime();
		return getStartTimeWeekContext() - tripbeforeactivity.getTripduration();
	}
	
	public int getTripStartTimeAfterActivity()
	{
		return getEndTime();
	}
	
	public int getTripStartTimeAfterActivityWeekContext()
	{
		return getEndTimeWeekContext();
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
	 * Mean-Time-Berechnung
	 * 
	 * @return
	 */
	public int calculateMeanTime()
	{
		double timebudget = getPerson().getAttributefromMap(getType() + "budget_exact");
		double daysWithAct = getWeekPattern().countDaysWithSpecificActivity(getType());
		double specificActivitiesForCurrentDay = getDay().getTotalAmountOfActivitites(getType());
		
		// Mean-Time-Berechnung (zwischen 1 und 1440)
		double meantime;
		meantime = (int) Math.max((timebudget / daysWithAct) * (1 / specificActivitiesForCurrentDay),1.0);
		meantime = Math.min(meantime, 1440.0);
		
		return (int) meantime;
	}
	
	/**
	 * 
	 * Mean-Time-Kategorie-Berechnung
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
		assert meantimecategory!=-99 : "Konnte Kategorie nicht ermitteln!";
		return meantimecategory;
	}
	
	/**
	 * 
	 * Berechnet mittlere Wegzeiten je nach Aktivität und setzt TripTime Werte
	 * 
	 */
	public void calculateAndSetTripTimes()
	{
		if (tripbeforeactivity==null)
		{
	    int actualTripTime_beforeTrip = Configuration.FIXED_TRIP_TIME_ESTIMATOR;

	    // Verbessere die Annahme der Wegezeit, falls ein Pendelweg vorliegt
	    if (hasWorkCommutingTripbeforeActivity()) actualTripTime_beforeTrip = getPerson().getCommutingDuration_work();
	    if (hasEducationCommutingTripbeforeActivity()) actualTripTime_beforeTrip = getPerson().getCommutingDuration_education();
	    
			tripbeforeactivity = new HTrip(this, actualTripTime_beforeTrip);
		}

		if (tripafteractivity==null && isActivityLastinTour())
		{
	    int actualTripTime_afterTrip = isActivityLastinTour() ? Configuration.FIXED_TRIP_TIME_ESTIMATOR : 0;
	    
	    // Verbessere die Annahme der Wegzeit auf dem letzten Weg der Tour nach der Aktivität, falls Pendelweg vorliegt
	    if (hasWorkCommutingTripafterActivity()) actualTripTime_afterTrip = getPerson().getCommutingDuration_work();
	    if (hasEducationCommutingTripafterActivity()) actualTripTime_afterTrip = getPerson().getCommutingDuration_education();

	    tripafteractivity = new HTrip(this, actualTripTime_afterTrip);
		}
	}
	
	public boolean isScheduled()
	{
		return this.duration!=-1 && this.starttime!=-1 && this.type!='x' && this.jointStatus!=-1;
	}
	
	public boolean activitytypeisScheduled()
	{
		return type!='x';
	}
	
	public boolean tripBeforeActivityisScheduled()
	{
		return tripbeforeactivity!=null && tripbeforeactivity.getTripduration()!=-1;
	}
	
	public boolean tripAfterActivityisScheduled()
	{
		return tripafteractivity!=null && tripafteractivity.getTripduration()!=-1;
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
	 * @param attributes spezifischesAttribut für Map
	 */
	public void addAttributetoMap(String name, Double value) {
		assert !attributes.containsKey(name) : "Attribut ist bereits in Map enthalten!";
		this.attributes.put(name, value);
	}
	
	/**
	 * Liefert gewünschtes Attribute aus der Map
	 * 
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
	 * Gibt den Personenindex der Person an, durch welche diese Aktivität erstmalig erstellt wurde
	 * Bei gemeinsamen Aktivitäten, die von anderen Personen übernommen wurden entspricht dies dem Index 
	 * der Person, die diese Akt erstmals festgelegt hat
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
		assert result!=-1 : "CreatorPersonIndex konnte nicht bestimmt werden!";
		return result;
	}
	
	/**
	 * Prüft, ob sich zwei Aktivitäten in ihren Zeitintervallen überlagern
	 * false = Sie überlagern sich nicht
	 * true = Sie überlagern sich
	 * 
	 * @param tmpact
	 * @return
	 */
	public boolean checkOverlappingtoOtherActivity(HActivity tmpact)
	{
		boolean result = false;
		
		// Zeitbelegung der aktuellen Aktivität ermitteln
		int starttime = getStartTimeWeekContext() - (tripBeforeActivityisScheduled() ? getEstimatedTripTimeBeforeActivity() : 0);
		int endtime = (durationisScheduled() ? getEndTimeWeekContext() : getStartTimeWeekContext()) + (tripAfterActivityisScheduled() ? getEstimatedTripTimeAfterActivity() : 0);

		
		// Zeitbelegung der anderen Aktivität ermitteln
		int starttime_other = tmpact.getStartTimeWeekContext() - (tmpact.tripBeforeActivityisScheduled() ? tmpact.getEstimatedTripTimeBeforeActivity() : 0);
		int endtime_other = (tmpact.durationisScheduled() ? tmpact.getEndTimeWeekContext() : tmpact.getStartTimeWeekContext()) + (tmpact.tripAfterActivityisScheduled() ? tmpact.getEstimatedTripTimeAfterActivity() : 0);
		
		if (
					(starttime < starttime_other && starttime_other < endtime) ||
					(starttime < endtime_other   && endtime_other   < endtime)
			 )
		{
			result=true;
		}
		
		return result;
	}


}
