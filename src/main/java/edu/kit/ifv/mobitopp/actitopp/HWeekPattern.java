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
   * Konstruktor
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
   * Gibt den spezifischen Tag mit dem Index zurück
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
   * Gibt Anzahl an Touren an einem spezifischen Tag zurück
   * 
   * @param day
   * @return
   */
  public int getAmountOfToursForASpecificDay(int day)
  {
    return days.get(day).getAmountOfTours();
  }

  /**
   * 
   * Gibt Gesamtzahl der Aktivitäten in der Woche an.
   * Ohne Home-Aktivitäten
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
   * Gibt alle außer-Haus Aktivitäten der Woche in einer Liste zurück
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
  
  public List<HActivity> getAllJointActivities()
  {
  	List<HActivity> tmpliste = new ArrayList<HActivity>();
  	for (HActivity act : getAllActivities())
  	{
  		if (act.getJointStatus()!=4) tmpliste.add(act);
  	}
  	return tmpliste;
  }
  
  /**
   * 
   * @return
   */
  public List<HActivity> getAllHomeActivities()
  {
  	return homeactivitities;
  }

  
  /**
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
   * Gibt alle Touren der Woche in einer Liste zurück
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
   * Zählt die Anzahl an Aktivitäten eines Typs in der Woche
   * 
   * @param activityType
   * @return
   */
  public int countActivitiesPerWeek(char activityType)
  {
  	int ctr=0;
  	for(HDay day : this.getDays())
	  {
	    for(HTour tour : day.getTours())
	    {
	      for(HActivity act : tour.getActivities())
	      {
	        if(act.getType()==activityType)ctr++;;
	      }
	    }
	  }
	  return ctr;  	
  }
  
  /**
   * 
   * Zählt die Anzahl an Touren eines Typs in der Woche
   * 
   * @param activityType
   * @return
   */
  public int countToursPerWeek(char activityType)
  {
  	
  	int ctr=0;
  	for(HDay day : this.getDays())
	  {
	    for(HTour tour : day.getTours())
	    {
	      if(tour.getActivity(0).getType()==activityType)ctr++;;
	    }
	  }
	  return ctr;  	
  }
  
  /**
   * 
   * Zählt die Tage, an denen eine spezifischer Aktivitätentyp auftritt
   * 
   * @param activityType
   * @return
   */
  public int countDaysWithSpecificActivity(char activityType)
  {
  	int ctr=0;  	
  	for (HDay currentDay : getDays())
		{
			if (currentDay.getTotalAmountOfActivitites(activityType)>0) ctr++; 
		}
  	return ctr;
  }
  
	
	public void printOutofHomeActivitiesList()
  {  	
		List<HActivity> listenkopie = new ArrayList<HActivity>();
		listenkopie = getAllOutofHomeActivities();
		
  	HActivity.sortActivityListbyWeekStartTimes(listenkopie);

  	System.out.println("");
  	System.out.println(" -------------- AKTIVITÄTENLISTE --------------");
  	System.out.println("");
 	
  	for (int i=0 ; i< listenkopie.size() ; i++)
  	{
  		HActivity act = listenkopie.get(i);   		
  		if (act.getEstimatedTripTimeBeforeActivity()!=0)
  		{
  			System.out.println(i + " Weg : Start " + act.getTripStartTimeBeforeActivityWeekContext() + " Ende " + (act.getTripStartTimeBeforeActivityWeekContext()+act.getEstimatedTripTimeBeforeActivity()));
  		}
  		System.out.println(i + " Akt : " + act);
  	}
  }
	
	public void printAllActivitiesList()
  {  	
		List<HActivity> listenkopie = new ArrayList<HActivity>();
		listenkopie = getAllActivities();
		
  	HActivity.sortActivityListbyWeekStartTimes(listenkopie);

  	System.out.println("");
  	System.out.println(" -------------- AKTIVITÄTENLISTE --------------");
  	System.out.println("");
 	
  	for (int i=0 ; i< listenkopie.size() ; i++)
  	{
  		HActivity act = listenkopie.get(i);   		
  		if (!act.isHomeActivity() && act.getEstimatedTripTimeBeforeActivity()!=0)
  		{
  			System.out.println(i 		+ " Weg : Start " + act.getTripStartTimeBeforeActivityWeekContext() 
  															+ " Ende " + (act.getTripStartTimeBeforeActivityWeekContext()+act.getEstimatedTripTimeBeforeActivity())
  															+ " Dauer " + act.getEstimatedTripTimeBeforeActivity()
  												);
  		}
  		
  		System.out.println(i + " Akt : " + act);
  		
  		if (!act.isHomeActivity() && act.isActivityLastinTour())
  		{
  			System.out.println(i 		+ " Weg (letzter in Tour) : Start " + act.getTripStartTimeAfterActivityWeekContext() 
  															+ " Ende " + (act.getTripStartTimeAfterActivityWeekContext()+act.getEstimatedTripTimeAfterActivity())
  															+ " Dauer " + act.getEstimatedTripTimeAfterActivity()
  												);
  		}
  	}
  }
	
	
	public void printJointActivitiesList()
  {  	
		List<HActivity> listenkopie = new ArrayList<HActivity>();
		listenkopie = getAllJointActivities();
		
  	HActivity.sortActivityListbyWeekStartTimes(listenkopie);

  	System.out.println("");
  	System.out.println(" -------------- LISTE gemeinsamer AKTIVITÄTEN --------------");
  	System.out.println("");
 	
  	for (int i=0 ; i< listenkopie.size() ; i++)
  	{
  		HActivity act = listenkopie.get(i);   		
  		System.out.println(i + " Akt : " + act + " // Creator: " + act.getCreatorPersonIndex()); 
  	}
  }
  
	/**
	 * 
	 * @param act
	 */
	public void addHomeActivity(HActivity act){
		assert act.getType()=='H' : "keine Heimaktivität";
		homeactivitities.add(act);
	}
	
	/**
	 * 
	 * Prüft, ob WeekPattern überlappende Aktivitäten enthält
	 * 
	 * @param weekpattern
	 * @return
	 * @throws InvalidPersonPatternException
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
