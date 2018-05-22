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
	
	// Enth�lt alle Attribute, die nicht direkt �ber Variablen ausgelesen werden k�nnen
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
   * erwartete Wegezeit nach der Aktivit�t
   * nur relevant, falls Aktivit�t die letzte auf der Tour ist.
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
	 * Konstruktor f�r Aktivit�ten, wo nur Akt gemeinsam durchgef�hrt wird
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
	 * Konstruktor f�r Aktivit�ten, wo Akt und Weg gemeinsam durchgef�hrt werden
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
	 * Konstruktor f�r Aktivit�ten, wo nur Weg dahin gemeinsam durchgef�hrt wird
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
	 * Konstruktor f�r Home-Aktivit�ten 
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
		assert Configuration.ACTIVITY_TYPES.contains(type) || type=='H' : "ung�ltige Aktivit�t - nicht intialisiert oder nicht in ACTIVITYTYPES enthalten aktueller Wert:" + type;
		return type;
	}

	public void setType(char type) 
	{
		assert Configuration.ACTIVITY_TYPES.contains(type) || type=='H' : "ung�ltige Aktivit�t - nicht in ACTIVITYTYPES enthalten - Typ: " + type; 
		this.type = type;
	}

	public int getDuration() 
	{
		assert duration != -1 : "Duration ist zum Zugriffszeitpunkt nicht initialisiert";
		return duration;
	}

	public void setDuration(int duration) 
	{
		assert duration>0 : "zu setzende Dauer ist nicht gr��er 0 - Dauer:" + duration;
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
		assert tripBeforeActivityisScheduled() : "Trip vor der Aktvitit�t ist zum Zugriffszeitpunkt nicht initialisiert";
		int tmptriptimebefore = tripbeforeactivity.getTripduration();
		return tmptriptimebefore;
	}
/*
	private void setEstimatedTripTime(int estimatedTripTime) 
	{
		assert estimatedTripTime>0 || (estimatedTripTime==0 && getType()=='H') : "zu setzende Wegzeit ist nicht gr��er 0";
		this.estimatedTripTime = estimatedTripTime;
	}
*/
	/**
	 * @return the estimatedTripTimeAfterActivity
	 */
	public int getEstimatedTripTimeAfterActivity() 
	{
		assert tripAfterActivityisScheduled() : "Trip nach der Aktvitit�t ist zum Zugriffszeitpunkt nicht initialisiert // nur bei letzter Aktivit�t in Tour gesetzt";
		int tmptriptimeafter = tripafteractivity.getTripduration();
		return tmptriptimeafter;
	}

/*
	private void setEstimatedTripTimeAfterActivity(int estimatedTripTimeAfterActivity) 
	{
		assert (estimatedTripTimeAfterActivity==0 && !isActivityLastinTour()) || (estimatedTripTimeAfterActivity>0 && isActivityLastinTour()) : "zu setzende Wegzeit muss 0 (bei nicht letzter Aktivit�t in Tour) oder gr��er 0 (bei letzter Aktivit�t in der Tour) sein";
		this.estimatedTripTimeAfterActivity = estimatedTripTimeAfterActivity;
	}
*/
	
	public byte getMobiToppActType() 
	{
		assert Configuration.ACTIVITY_TYPES_mobiTopp.contains(mobiToppActType) : "ung�ltige Aktivit�t - nicht in ACTIVITYTYPES_mobiTopp enthalten"; 
		return mobiToppActType;
	}

	public void setMobiToppActType(byte mobiToppActType) 
	{
		assert Configuration.ACTIVITY_TYPES_mobiTopp.contains(mobiToppActType) : "ung�ltige Aktivit�t - nicht in ACTIVITYTYPES_mobiTopp enthalten"; 
		this.mobiToppActType = mobiToppActType;
	}
	
	/**
   * Sortiert eine Liste mit Aktivit�ten chronologisch im Wochenverlauf
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
              
              if(act1.getWeekDay() < act2.getWeekDay())
              {
                  return -1;
              }
              else if (act1.getWeekDay() == act2.getWeekDay())
              {
                  if(act1.getStartTime() < act2.getStartTime())
                  {
                      return -1;
                  }
                  else if(act1.getStartTime() == act2.getStartTime())
                  {
                      return 0;
                  }
                  else
                  {
                      return 1;
                  }
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
   * Methode zum Sortieren der Aktivit�tenliste nach Index
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
              if(o1.getIndex() < o2.getIndex()) return -1;
              if(o1.getIndex() >o2.getIndex()) return 1;
              return 0;
          }
      });
  }


  @Override
	public String toString()
	{
		return getIndexDay() + "/" + getTour().getIndex() + "/" + getIndex() + 	
				" Start " + getStartTimeWeekContext() + 
				" Ende " + getEndTimeWeekContext() + 
				" Dauer: " + this.duration + 
				" Typ: " + this.type + " (" + this.mobiToppActType + ")" + 
				" jointStatus: " + this.jointStatus
				;
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
	 * Bestimmt, ob vor der Aktivit�t ein Arbeitspendelweg von Zuhause stattfindet.
	 * Wird im Modellverlauf zur besseren Bestimmung von default-Wegezeiten genutzt
	 * Kann nur sicher belegt werden, ...
	 * 	... wenn es die erste Aktivit�t der Tour ist
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
	 * Bestimmt, ob nach der Aktivit�t ein Arbeitspendelweg nach Zuhause stattfindet.
	 * Wird im Modellverlauf zur besseren Bestimmung von default-Wegezeiten genutzt
	 * Kann nur sicher belegt werden, ...
	 * 	... wenn es die letzte Aktivit�t der Tour ist
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
	 * Bestimmt, ob vor der Aktivit�t ein Bildungspendelweg von Zuhause stattfindet.
	 * Wird im Modellverlauf zur besseren Bestimmung von default-Wegezeiten genutzt
	 * Kann nur sicher belegt werden, ...
	 * 	... wenn es die erste Aktivit�t der Tour ist
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
	 * Bestimmt, ob nach der Aktivit�t ein Bildungspendelweg nach Zuhause stattfindet.
	 * Wird im Modellverlauf zur besseren Bestimmung von default-Wegezeiten genutzt
	 * Kann nur sicher belegt werden, ...
	 * 	... wenn es die letzte Aktivit�t der Tour ist
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
	 * Gibt an, ob es sich bei der Aktivit�t um eine Home-Aktivit�t handelt
	 * 
	 * @return
	 */
	public boolean isHomeActivity()
	{
		return getType()=='H';
	}

	/**
	 * 
	 * Gibt an, ob es sich um die Hauptaktivit�t der Tour handelt
	 * 
	 * @return
	 */
	public boolean isMainActivityoftheTour()
	{
		return this.getIndex()==0;
	}
	
	/**
	 * 
	 * Gibt an, ob es sich um die Hauptaktivit�t des Tages handelt
	 * 
	 * @return
	 */
	public boolean isMainActivityoftheDay()
	{
		return isMainActivityoftheTour() && getTour().isMainTouroftheDay();
	}
	
	/**
	 * 
	 * Gibt die vorherige Aktivit�t auf der Tour zur�ck
	 * 
	 * @return
	 */
	public HActivity getPreviousActivityinTour()
	{
		HActivity previousActivity;
		
		//Pr�fe, ob die Aktivt�t die erste in der Tour ist
	    if (getIndex() != getTour().getLowestActivityIndex())
	    {
	    	previousActivity = getTour().getActivity(getIndex()-1);
	    }
	    // Falls die Aktivit�t die erste ist, gibt es keinen Vorg�nger in der Tour
	    else
	    {
	    	previousActivity = null;
	    }
	    return previousActivity;
	}

	/**
	 * 
	 * Gibt den Endzeitpunkt der Aktivit�t zur�ck
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

	public int getTripStartTime()
	{
		// return getStartTime() - getEstimatedTripTime();
		return getStartTime() - tripbeforeactivity.getTripduration();
	}

	public int getTripStartTimeWeekContext()
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
	 * Berechnet mittlere Wegzeiten je nach Aktivit�t und setzt TripTime Werte
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
	    
	    // Verbessere die Annahme der Wegzeit auf dem letzten Weg der Tour nach der Aktivit�t, falls Pendelweg vorliegt
	    if (hasWorkCommutingTripafterActivity()) actualTripTime_afterTrip = getPerson().getCommutingDuration_work();
	    if (hasEducationCommutingTripafterActivity()) actualTripTime_afterTrip = getPerson().getCommutingDuration_education();

	    tripafteractivity = new HTrip(this, actualTripTime_afterTrip);
		}

		
//    setEstimatedTripTime(actualTripTime_beforeTrip);
//    setEstimatedTripTimeAfterActivity(actualTripTime_afterTrip);		
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

}
