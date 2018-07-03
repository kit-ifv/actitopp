package edu.kit.ifv.mobitopp.actitopp;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Tim Hilgert
 * 
 * Coordinator-Klasse, die die Erstellung der Wochen-Aktivitätenpläne koordiniert.
 * Wird aufgerufen von ActiToppPerson zur Erstellung der Schedules
 *
 */
public class Coordinator
{

    ////////////////
    
    //	VARIABLEN-DEKLARATIONEN
    
    //////////////// 
	
    private ActitoppPerson person;
    private HWeekPattern pattern;

    private ModelFileBase fileBase;
    private RNGHelper randomgenerator;
   
    // important for Step8C: dtd tables must be modified after each MC-selection
    // process
    // After the first MC-selection we must these modified tables instead of the
    // original ones
    // each activity type gets one of these per category (1 table per (activity
    // type, week and person) * categories) -> WELST * 15
    private DiscreteTimeDistribution[][] modifiedActDurationDTDs;

    // start time for work and education categories: WE * 16
    private DiscreteTimeDistribution[][] modifiedTourStartDTDs;
    
    
    
    ////////////////
    
    //	KONSTANTEN
    
    ////////////////    
    
    // Konstante für die Nutzung der Zeitverteilungen der Aktivitätsdauern
    private static final int INDICATOR_ACT_DURATIONS = 0;
    // Konstante für die Nutzung der Zeitverteilungen der Startzeiten
    private static final int INDICATOR_TOUR_STARTTIMES = 1;



    /**
     * 
     * Konstruktor
     * 
     * @param person
     * @param personIndex
     * @param fileBase
     */
    public Coordinator(ActitoppPerson person, ModelFileBase fileBase, RNGHelper randomgenerator)
    {
    	assert person!=null : "Person nicht initialisiert";
    	assert fileBase!=null : "FileBase nicht initialisiert";
    	assert randomgenerator!=null : "Zufallszahlengenerator nicht initialisiert";
    	
    	this.person = person;
    	this.pattern = person.getWeekPattern();
      this.fileBase = fileBase;
      this.randomgenerator = randomgenerator;
         
      modifiedActDurationDTDs = new DiscreteTimeDistribution[Configuration.NUMBER_OF_ACTIVITY_TYPES][Configuration.NUMBER_OF_ACT_DURATION_CLASSES];
      modifiedTourStartDTDs = new DiscreteTimeDistribution[Configuration.NUMBER_OF_ACTIVITY_TYPES][Configuration.NUMBER_OF_MAIN_START_TIME_CLASSES];

    }
    
 
    
  /**
   * 
   * (Main-)Methode zur Koordination der einzelnen Modellschritte
   *
   * @return
   * @throws FileNotFoundException
   * @throws IOException
   * @throws PrerequisiteNotMetException
   * @throws InvalidPatternException
   */
  public void executeModel() throws FileNotFoundException, IOException, InvalidPatternException
  {
  	
  	// Durchführung der Modellschritte
  
    // Gemeinsame Aktivitäten
    if (Configuration.model_joint_actions) 
    {
    	addJointActivitiestoPattern();
    }
  	
  	
    executeStep1("1A", "anztage_w");
    executeStep1("1B", "anztage_e");
    executeStep1("1C", "anztage_l");
    executeStep1("1D", "anztage_s");
    executeStep1("1E", "anztage_t");
    executeStep1("1F", "anztage_immobil");
       
    executeStep2("2A");
    
    executeStep3("3A");
    executeStep3("3B");
    
    executeStep4("4A");
    
    executeStep5("5A");
    executeStep5("5B");
    
    executeStep6("6A");
    
    createTripTimesforActivities();
    
    executeStep7DC("7A", 'W');
    executeStep7DC("7B", 'E');
    executeStep7DC("7C", 'L');
    executeStep7DC("7D", 'S');
    executeStep7DC("7E", 'T');
    
    executeStep7MC("7K", 'W');
    executeStep7MC("7L", 'E');
    executeStep7MC("7M", 'L');
    executeStep7MC("7N", 'S');
    executeStep7MC("7O", 'T');
    
    executeStep8A("8A");
    executeStep8_MainAct("8B", "8C");
    executeStep8_MainAct("8D", "8E");
    executeStep8_NonMainAct("8J", "8K");

    executeStep9A("9A");
    
    executeStep10A("10A");
       
    // Variante 1 zur Generierung der Startzeiten
    /*       
    executeStep10B("10B");
    executeStep10C("10C");
    executeStep10D("10D");
    executeStep10E("10E");
    executeStep10GH();
    executeStep10JK();
    */			
    
    // Variante 2 zur Generierung der Startzeiten - bevorzugt
    
    createTourStartTimesDueToScheduledActivities();
    
    executeStep10("10M","10N", 1);
    executeStep10("10O","10P", 2);
    executeStep10("10Q","10R", 3);
    executeStep10ST();
    
   	// Erstelle Startzeiten für jede Aktivität 
    createStartTimesforActivities();
    
    // Gemeinsame Aktivitäten
    if (Configuration.model_joint_actions) 
    {
    	executeStep11("11");
  		// Bestimme, welche gemeinsamen Wege/Aktivitäten welche anderen Personen beeinflussen
  		generateJointActionsforOtherPersons();
  		
    }
    
					 
    // Finalisierung der Wochenaktivitätenpläne 
    
    // 2) Erstelle eine Liste mit allen Aktivitäten der Woche
    List<HActivity> allModeledActivities = pattern.getAllOutofHomeActivities();    	
    HActivity.sortActivityListInWeekOrder(allModeledActivities);
	
    // 3) Erzeuge Zuhause-Aktivitäten zwischen den Touren
    createHomeActivities(allModeledActivities);
    
    // 4) Wandel die Aktivitätenzwecke des Modells zurück in mobiTopp-Aktivitätenzwecke
    convertactiToppPurposesTomobiToppPurposeTypes(pattern.getAllActivities());
    
    // DEBUG
    if (Configuration.debugenabled)
    {
    	pattern.printAllActivitiesList();
    }
    
    // first sanity checks: check for overlapping activities. if found,
    // throw exception and redo activityweek
    checkOverlappingActivities(pattern);

  }
  
  
  /**
   * 
   * Fügt alle gemeinsamen Aktivitäten, die über andere Personen an die aktuelle zugewiesen 
   * worden sind in das Pattern ein bevor die eigentliche Modellierung beginnt
   * 
   */
  private void addJointActivitiestoPattern()
  {
  	//TODO doppelte Schleifenprüfung - auch in aufrufender Methode
  	if(Configuration.model_joint_actions)
		{
			for (HActivity tmpjointact : person.getAllJointActivitiesforConsideration())
  		{
				// Infos für die neue, gemeinsame Akt
				int indexday = tmpjointact.getIndexDay();
				int tourindex = tmpjointact.getTour().getIndex();
				
				int personindex_created = tmpjointact.getPerson().getPersIndex();
				
				int activityindex = tmpjointact.getIndex();
				char activitytype = tmpjointact.getType();
				int activityduration = tmpjointact.getDuration();
				int activitystarttime = tmpjointact.getStartTime();
				int activityjointStatus = tmpjointact.getJointStatus();
				int activitytripdurationbefore = tmpjointact.getEstimatedTripTimeBeforeActivity();
				
								
        // Hole Referenz auf Tour oder füge die Tour in das Pattern ein, falls sie noch nicht existiert
        HDay currentDay = pattern.getDay(indexday);
        
        HTour oneTour;
				if (currentDay.existsTour(tourindex))
				{
					oneTour = currentDay.getTour(tourindex);
				}
				else
				{
					oneTour = new HTour(currentDay, tourindex);
					currentDay.addTour(oneTour);
				}
                  
        // Füge die Aktivität in das Pattern ein
				HActivity activity = null;
				
				
				switch(activityjointStatus)
				{
					// Weg davor und Aktivität werden gemeinsam durchgeführt
					case 1:
					{
						activity = new HActivity(oneTour, activityindex, activitytype, activityduration, activitystarttime, activityjointStatus, activitytripdurationbefore);
						break;
					}
					// Nur Aktivität wird gemeinsam durchgeführt
					case 2:
					{
						activity = new HActivity(oneTour, activityindex, activitytype, activityduration, activitystarttime, activityjointStatus);
						break;
					}		
					// Weg davor wird gemeinsam durchgeführt
					case 3:
					{
						activity = new HActivity(oneTour, activityindex, activitystarttime, activityjointStatus, activitytripdurationbefore);
						break;
					}
				}			
				assert activity!=null : "Aktivität wurde nicht erzeugt";
				activity.addAttributetoMap("CreatorPersonIndex", (double) personindex_created); 
				
				
				/*
				 *  Prüfe, ob Aktivität konfliktfrei einfügbar ist in das Pattern
				 */
				boolean konfliktfrei = true;
				String reason="";
				
				// Aktivität mit diesem Index in dieser Tour existiert bereits
				if (currentDay.existsActivity(oneTour.getIndex(), activityindex)) 
				{
					konfliktfrei=false;
					reason = "Aktivität mit Index existiert bereits in Tour!";
				}
				
				// Aktivität passt zeitlich nicht an diese Position
				for (HActivity tmpact : currentDay.getAllActivitiesoftheDay())
				{
					if (
							(tmpact.getTour().getIndex() > activity.getTour().getIndex() && tmpact.getStartTime() < activity.getStartTime())
							||
							(tmpact.getTour().getIndex() == activity.getTour().getIndex() && tmpact.getIndex() > activity.getIndex() && tmpact.getStartTime() < activity.getStartTime())
						)
					{
						konfliktfrei = false;
						reason = "Aktivität passt zeitlich nicht an diese Position!";
						break;
					}
				}
								
				
				if (konfliktfrei)
				{
					oneTour.addActivity(activity);
				}
				else
				{
					System.err.println("gemeinsame Aktivität konnte nicht eingefügt werden! // " + reason);
					System.err.println("Aktivitiät Tag:" + currentDay.getIndex() + " Tour: " + activity.getTour().getIndex() + " Aktindex: " + activityindex + " Startzeit: " + activitystarttime + "(" + tmpjointact.getStartTimeWeekContext() + ")");
				}
  		}
		}
  }
  
    

  /**
   * 
   * @param id
   * @param variablenname
   */
	private void executeStep1(String id, String variablenname)
	{
		// AttributeLookup erzeugen
		AttributeLookup lookup = new AttributeLookup(person);
		
    // Step-Objekt erzeugen
    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
    step.doStep();
    
    // Ergebnis zu Personen-Map hinzufügen
    person.addAttributetoMap(variablenname, Double.parseDouble(step.getAlternativeChosen()));
	}

	/**
	 * 
	 * @param id
	 */
  private void executeStep2(String id)
  {
    // STEP 2A Main tour and main activity
    for (HDay currentDay : pattern.getDays())
    {
    	// Schritt wird ausgeführt, falls die Hauptaktivität noch nicht exisitiert oder noch keinen Aktivitätstyp hat
    	if(!currentDay.existsActivityTypeforActivity(0,0))
    	{
      	// AttributeLookup erzeugen
    		AttributeLookup lookup = new AttributeLookup(person, currentDay);   	
      	
  	    // Step-Objekt erzeugen
  	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
  	    
  	    // Falls es schon Touren gibt (aus gemeinsamen Akt), H als Aktivitätstyp ausschließen
  	    if (currentDay.getAmountOfTours()>0)
  	    {
  	    	step.limitUpperBoundOnly(step.alternatives.size()-2);
	    	}
  	    
  	    // Auswahl durchführen
  	    step.doStep();
  	    char activityType = step.getAlternativeChosen().charAt(0);
    		
  	    if (activityType!='H')
        {	
          // Füge die Tour in das Pattern ein, falls sie noch nicht existiert
  	    	HTour mainTour = null;
  	    	if (!currentDay.existsTour(0))
          {
          	mainTour = new HTour(currentDay, 0);
          	currentDay.addTour(mainTour);
          }
  	    	else
  	    	{
  	    		mainTour = currentDay.getTour(0);
  	    	}
  	    	
  	    	// Füge die Aktivität in das Pattern ein, falls sie noch nicht existiert
  	    	HActivity activity = null;
  	    	if (!currentDay.existsActivity(0,0))
          {
  	    		activity = new HActivity(mainTour, 0, activityType);
            mainTour.addActivity(activity);
          }
  	    	else
  	    	{
  	    		activity = currentDay.getTour(0).getActivity(0);
  	    		activity.setType(activityType);
  	    	}
        }
    	}		    
    }
  }
  
  /**
   * 
   * @param id
   */
	private void executeStep3(String id)
	{
    for (HDay currentDay : pattern.getDays())
    {   	
    	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgeführt
      if (currentDay.isHomeDay())
      {
      	continue;
      }
      
      // AttributeLookup erzeugen
  		AttributeLookup lookup = new AttributeLookup(person, currentDay);   	
    	
	    // Step-Objekt erzeugen
	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
	    
	    // Mindesttourzahl festlegen, falls es schon Touren aus gemeinsamen Aktivitäten gibt
	    int mindesttourzahl=0;
	    if (id.equals("3A")) mindesttourzahl = currentDay.getLowestTourIndex() * -1;
	    if (id.equals("3B")) mindesttourzahl = currentDay.getHighestTourIndex() * +1;
	    
	    // Alternativen limitieren basierend auf Mindestourzahl
	    step.limitLowerBoundOnly(mindesttourzahl);
	    
	    // Entscheidung durchführen
	    step.doStep();
            
      // Erstelle die weiteren Touren an diesem Tag basierend auf der Entscheidung und füge Sie in das Pattern ein, falls sie noch nicht existieren
      for (int j = 1; j <= step.getDecision(); j++)
      {
      	HTour tour = null;
      	// 3A - Touren vor der Haupttour
        if (id.equals("3A") && !currentDay.existsTour(-1*j)) tour = new HTour(currentDay, (-1) * j);
      	// 3B - Touren nach der Haupttour
        if (id.equals("3B") && !currentDay.existsTour(+1*j)) tour = new HTour(currentDay, (+1) * j);        
        
        if (tour!=null) currentDay.addTour(tour);
      }
    }
	}

	/**
	 * 
	 * @param id
	 */
  private void executeStep4(String id)
  {
    // STEP 4A Main activity for all other tours
    for (HDay currentDay : pattern.getDays())
    {
    	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgeführt
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
                     
      for (HTour currentTour : currentDay.getTours())
      {
        /*
         * Ignoriere Touren, deren Hauptaktivität schon festgelegt ist
         * 	- Hauptouren des Tages (siehe Schritt 2)
         *  - andere Hauptaktivitäten, welche über gemeinsame Aktivität ins Pattern gekommen sind
         */
      	if(!currentDay.existsActivityTypeforActivity(currentTour.getIndex(),0))
        {
        	// AttributeLookup erzeugen
      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
        	
    	    // Step-Objekt erzeugen
    	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
    	    step.doStep();

          // Speichere gewählte Entscheidung für weitere Verwendung
          char chosenActivityType = step.getAlternativeChosen().charAt(0);
          
  	    	HActivity activity = null;
  	    	
  	    	// Falls die Aktivität existiert wird nur deren Typ bestimmt
  	    	if (currentDay.existsActivity(currentTour.getIndex(),0))
          {
  	    		activity = currentTour.getActivity(0);
  	    		activity.setType(chosenActivityType);
          }
  	    	// Erstelle die Aktivität mit entsprechendem Typ, falls Sie noch nicht exisitert
  	    	else
  	    	{ 	    		
  	    		activity = new HActivity(currentTour, 0, chosenActivityType);
  	    		currentTour.addActivity(activity);
  	    	}
        }
      }
    }
  }

  /**
   * 
   * @param id
   */
	private void executeStep5(String id)
	{
    for (HDay currentDay : pattern.getDays())
    {
    	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgeführt
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
        
      for (HTour currentTour : currentDay.getTours())
      {
      	// AttributeLookup erzeugen
    		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
      	
    	  // Step-Objekt erzeugen
  	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
    		
  	    // Mindesaktzahl festlegen, falls es schon Aktivitäten aus gemeinsamen Aktivitäten gibt
  	    int mindestaktzahl =0;
  	    if (id.equals("5A")) mindestaktzahl = currentTour.getLowestActivityIndex() * -1;
  	    if (id.equals("5B")) mindestaktzahl = currentTour.getHighestActivityIndex() * +1;
  	    
  	    // Alternativen limitieren basierend auf Mindesaktzahl
  	    step.limitLowerBoundOnly(mindestaktzahl);
  	    
  	    // Entscheidung durchführen
  	    step.doStep();    		

  	    // Erstelle die weiteren Aktivitäten in dieser Tour basierend auf der Entscheidung und füge Sie in das Pattern ein
        for (int j = 1; j <= step.getDecision(); j++)
        {
        	HActivity act = null;
        	// 5A - Touren vor der Haupttour
          if (id.equals("5A") && !currentDay.existsActivity(currentTour.getIndex(),-1*j)) act = new HActivity(currentTour, (-1) * j);
        	// 5B - Touren nach der Haupttour
          if (id.equals("5B") && !currentDay.existsActivity(currentTour.getIndex(),+1*j)) act = new HActivity(currentTour, (+1) * j);
          
          if (act!=null) currentTour.addActivity(act);
        }
      }
    }
	}

	/**
	 * 
	 * @param id
	 */
	private void executeStep6(String id)
	{
    // STEP 6A Non-Main-Activity Type Decision
    for (HDay currentDay : pattern.getDays())
    {
    	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgeführt
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
      
      for (HTour currentTour : currentDay.getTours())
      {
        for (HActivity currentActivity : currentTour.getActivities())
        {
        	// only use activities whose type has not been decided yet
          if (!currentActivity.activitytypeisScheduled())
          {
          	// AttributeLookup erzeugen
        		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
          	
      	    // Step-Objekt erzeugen
      	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
      	    step.doStep();

            // Aktivitätstyp festlegen
      	    currentActivity.setType(step.getAlternativeChosen().charAt(0));
          }
        }
      }
    }
	}

	/**
	 * 
	 * Festlegung von Default-Wegzeiten für alle Aktivitäten
	 * 
	 */
	private void createTripTimesforActivities() 
	{
	  for (HDay day : pattern.getDays())
	  {
	    for (HTour tour : day.getTours())
	    {
	    	for (HActivity act : tour.getActivities())
	      {
	    		act.calculateAndSetTripTimes();
	      }
	    }
	  }	
	}



	/**
	 * 
	 * @param id
	 * @param variablenname
	 */
	private void executeStep7DC(String id, char activitytype)
	{
		// Wird nur ausgeführt, wenn es zu dem Aktivitätentyp auch Aktivitäten gibt
	  if (pattern.countActivitiesPerWeek(activitytype)>0)
	  {
			// AttributeLookup erzeugen
			AttributeLookup lookup = new AttributeLookup(person);
			
	    // Step-Objekt erzeugen
	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
	    step.doStep();
	    
	    // Ergebnis als Index und Alternative zu Personen-Map hinzufügen für spätere Verwendung
	    person.addAttributetoMap(activitytype+"budget_category_index", (double) step.getDecision());
	    person.addAttributetoMap(activitytype+"budget_category_alternative", Double.parseDouble(step.getAlternativeChosen()));
	  }
	  
	  // special case: if there is exactly no activity allocated for work, than we must set cat to 0
	  // needed to achieve value for Attribute zeitbudget_work_ueber_kat2
    if (activitytype=='W' && pattern.countActivitiesPerWeek(activitytype)==0)
    {
    	person.addAttributetoMap(activitytype+"budget_category_alternative", 0.0d);
    } 
	}

	/**
	 * 
	 * @param id
	 * @param activitytype
	 */
	private void executeStep7MC(String id, char activitytype)
    {
	  	// Wird nur ausgeführt, wenn es zu dem Aktivitätentyp auch Aktivitäten gibt
	  	if (pattern.countActivitiesPerWeek(activitytype)>0)
      {
        // Entscheidung aus Schritt 7A-E ermitteln
        double chosenIndex = person.getAttributefromMap(activitytype+"budget_category_index");

        DefaultMCModelStep step = new DefaultMCModelStep(id + (int) chosenIndex, this);
        step.doStep();
        
        int chosenTime = step.getChosenTime();
        //Entscheidungsindex als Property speichern
        person.addAttributetoMap(activitytype+"budget_exact",(double) chosenTime);
      }
    }	
	
	/**
	 * 
	 * @param id
	 */
	private void executeStep8A(String id) 
	{
    // STEP8a: yes/no decision for "activity is in average time class xyz".
    // only applies to main activities
    for (HDay currentDay : pattern.getDays())
    {
    	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgeführt
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
  	
      for (HTour currentTour : currentDay.getTours())
      {
      	// Anwendung des Modellschritts nur auf Hauptaktivitäten
        HActivity currentActivity = currentTour.getActivity(0);
        
        // Schritt wird nur durchgeführt, falls Dauer der Aktivität noch nicht feststeht
        if(!currentActivity.durationisScheduled())
        {
        	// AttributeLookup erzeugen
      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
        	
    	    // Step-Objekt erzeugen
    	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
    	    step.doStep();

    	    // Eigenschaft abspeichern
    	    currentActivity.addAttributetoMap("standarddauer",(step.getAlternativeChosen().equals("yes") ? 1.0d : 0.0d));
    	    
    	    // Bei unkoordinierter Modellierung ohne Stabilitätsaspekte wird der Wert immer mit 0 überschrieben!
    	    if (!Configuration.coordinated_modelling) currentActivity.addAttributetoMap("standarddauer", 0.0d);
        }
      }
    }
	}


	/**
	 * 
	 * @param id_dc
	 * @param id_mc
	 */
	private void executeStep8_MainAct(String id_dc, String id_mc) throws InvalidPatternException
	{
		
		// Modifizierte Zeitverteilungen zur Modellierung von höheren Auswahlwahrscheinlichkeiten bereits gewählter Zeiten
	  modifiedActDurationDTDs = new DiscreteTimeDistribution[Configuration.NUMBER_OF_ACTIVITY_TYPES][Configuration.NUMBER_OF_ACT_DURATION_CLASSES];
		
	  for (HDay currentDay : pattern.getDays())
	  {
	  	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgeführt
	  	if (currentDay.isHomeDay())
	    {
	    	continue;
	    }
		
	  	// Anwendung des Modellschritts nur auf Hauptaktivitäten
			for (HTour currentTour : currentDay.getTours())
			{
				boolean running=false;
				if (id_dc.equals("8B") && currentTour.getIndex()==0) running=true;  // 8B gilt nur für Haupttouren (TourIndex=0)
				if (id_dc.equals("8D") && currentTour.getIndex()!=0) running=true;	// 8D gilt nur für NICHT-Haupttouren (TourIndex!=0)
					
				if (running)
				{
	        HActivity currentActivity = currentTour.getActivity(0);
	        
	  	    /*
	  	     * 
	  	     * DC-Schritt (8B, 8D)
	  	     * 
	  	     */
	        // Schritt nur durchführen, falls Dauer noch nicht festgelegt wurde
	        if (!currentActivity.durationisScheduled())
	        {
	          // AttributeLookup erzeugen
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
	        	
	    	    // Step-Objekt erzeugen
	    	    DefaultDCModelStep step_dc = new DefaultDCModelStep(id_dc, this, lookup);
	    	    
	    	    // Alternativen ggf. auf Standardzeitkategorie einschränken
	    	    modifyAlternativesDueTo8A(currentActivity, step_dc);  	    
	    	    
	    	    // Alternativen ggf. basierend auf bereits festgelgten Dauern beschränken
	    	    int maxduration = calculateMaxdurationDueToScheduledActivities(currentActivity);
	    	    int loc_upperbound = getDurationTimeClassforExactDuration(maxduration);
	    	   	   
	    	    if (loc_upperbound <= step_dc.getUpperBound()) step_dc.limitUpperBoundOnly(loc_upperbound); 
	    	    if (loc_upperbound <= step_dc.getLowerBound()) step_dc.limitLowerBoundOnly(loc_upperbound); 
	
	    	    // Wahlentscheidung durchführen
	    	    step_dc.doStep();
	
	    	    // Entscheidungsindex abspeichern
	    	    currentActivity.addAttributetoMap("actdurcat_index",(double) step_dc.getDecision()); 	
	    	    
	    	    /*
	    	     * 
	    	     * MC-Schritt (8C, 8E)
	    	     * 
	    	     */
	    	    // Schritt nur durchführen, falls Dauer noch nicht festgelegt wurde
	          if (!currentActivity.durationisScheduled())
		        {
	          	// Objekt basierend auf der gewählten Zeitkategorie initialisieren
				      double chosenTimeCategory = currentActivity.getAttributesMap().get("actdurcat_index");
				      DefaultMCModelStep step_mc = new DefaultMCModelStep(id_mc + (int) chosenTimeCategory, this);
				      step_mc.setModifiedDTDtoUse(currentActivity.getType(), (int) chosenTimeCategory);
				      
				      // angepasste Zeitverteilungen aus vorherigen Entscheidungen werden nur im koordinierten Fall verwendet
				      step_mc.setModifyDTDAfterStep(Configuration.coordinated_modelling);
				      step_mc.setDTDTypeToUse(INDICATOR_ACT_DURATIONS);
				      
				      // Limitiere die Obergrenze durch die noch verfügbare Zeit
				      step_mc.setRangeBounds(0, calculateMaxdurationDueToScheduledActivities(currentActivity));
				      
				      // Wahlentscheidung durchführen
				      step_mc.doStep();
				     
				      // Speichere Ergebnisse ab
				      currentActivity.setDuration(step_mc.getChosenTime());
		        } 
	        }
				}		
			}
	  }
	}



	/**
	 * 
	 * @param id_dc
	 * @param id_mc
	 */
	private void executeStep8_NonMainAct(String id_dc, String id_mc) throws InvalidPatternException
	{
		
		// Modifizierte Zeitverteilungen zur Modellierung von höheren Auswahlwahrscheinlichkeiten bereits gewählter Zeiten
	  modifiedActDurationDTDs = new DiscreteTimeDistribution[Configuration.NUMBER_OF_ACTIVITY_TYPES][Configuration.NUMBER_OF_ACT_DURATION_CLASSES];
		
	  for (HDay currentDay : pattern.getDays())
	  {
	  	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgeführt
	  	if (currentDay.isHomeDay())
	    {
	    	continue;
	    }
		
	  	for (HTour currentTour : currentDay.getTours())
	    {
	      for (HActivity currentActivity : currentTour.getActivities())
	      {
	  	    /*
	  	     * 
	  	     * DC-Schritt
	  	     * 
	  	     */
	      	// Schritt nur durchführen, falls keine Hauptaktivität und Dauer noch nicht festgelegt wurde
	        if (currentActivity.getIndex() != 0 && !currentActivity.durationisScheduled())
	        {   	     
	          // AttributeLookup erzeugen
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
	        	
	    	    // Step-Objekt erzeugen
	    	    DefaultDCModelStep step_dc = new DefaultDCModelStep(id_dc, this, lookup);
	    	   
	    	    // Alternativen ggf. basierend auf bereits festgelgten Dauern beschränken
	    	    int maxduration = calculateMaxdurationDueToScheduledActivities(currentActivity);
	    	    int loc_upperbound = getDurationTimeClassforExactDuration(maxduration);
	    		   
	    	    if (loc_upperbound <= step_dc.getUpperBound()) step_dc.limitUpperBoundOnly(loc_upperbound); 
	    	    if (loc_upperbound <= step_dc.getLowerBound()) step_dc.limitLowerBoundOnly(loc_upperbound); 
	
	    	    // Wahlentscheidung durchführen
	    	    step_dc.doStep();
	
	    	    // Entscheidungsindex abspeichern
	    	    currentActivity.addAttributetoMap("actdurcat_index",(double) step_dc.getDecision()); 	
	    	    
	    	    /*
	    	     * 
	    	     * MC-Schritt
	    	     * 
	    	     */
	    	    // Schritt nur durchführen, falls Dauer noch nicht festgelegt wurde
	    	    if (currentActivity.getIndex() != 0 && !currentActivity.durationisScheduled())
	          {
	          	// Objekt basierend auf der gewählten Zeitkategorie initialisieren
	          	double chosenTimeCategory = currentActivity.getAttributesMap().get("actdurcat_index");
	  		      DefaultMCModelStep step_mc = new DefaultMCModelStep(id_mc + (int) chosenTimeCategory, this);
	  		      step_mc.setModifiedDTDtoUse(currentActivity.getType(), (int) chosenTimeCategory);
	  		      
	  		      // angepasste Zeitverteilungen aus vorherigen Entscheidungen werden nur im koordinierten Fall verwendet
	  		      step_mc.setModifyDTDAfterStep(Configuration.coordinated_modelling);
	  		      step_mc.setDTDTypeToUse(INDICATOR_ACT_DURATIONS);
	
				      // Limitiere die Obergrenze durch die noch verfügbare Zeit
	  		      step_mc.setRangeBounds(0, calculateMaxdurationDueToScheduledActivities(currentActivity));
				      
				      // Wahlentscheidung durchführen
	  		      step_mc.doStep();
	  		     
	  		      // Speichere Ergebnisse ab
	  		      currentActivity.setDuration(step_mc.getChosenTime());
	          }
	        }
				}		
			}
	  }
	}



	/**
	 * 
	 * @param id
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private void executeStep8_MainAct_DC(String id)
	{
		for (HDay currentDay : pattern.getDays())
    {
    	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgeführt
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
  	
    	// Anwendung des Modellschritts nur auf Hauptaktivitäten
  		for (HTour currentTour : currentDay.getTours())
  		{
  			boolean running=false;
  			if (id.equals("8B") && currentTour.getIndex()==0) running=true; // 8B gilt nur für Haupttouren (TourIndex=0)
  			if (id.equals("8D") && currentTour.getIndex()!=0) running=true;	// 8D gilt nur für NICHT-Haupttouren (TourIndex!=0)
  				
  			if (running)
  			{
          HActivity currentActivity = currentTour.getActivity(0);
          
          // Schritt nur durchführen, falls Dauer noch nicht festgelegt wurde
          if (!currentActivity.durationisScheduled())
	        {
	          // AttributeLookup erzeugen
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
	        	
	    	    // Step-Objekt erzeugen
	    	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
	    	    
	    	    // Alternativen ggf. auf Standardzeitkategorie einschränken
	    	    modifyAlternativesDueTo8A(currentActivity, step);  	    
	    	    
	    	    // Alternativen ggf. basierend auf bereits festgelgten Dauern beschränken
	    	    int loc_upperbound = calculateUpperBoundDurationTimeClassDueToPlannedDurations(currentDay);
	    	    	    	    
	    	    if (loc_upperbound <= step.getUpperBound()) step.limitUpperBoundOnly(loc_upperbound); 
	    	    if (loc_upperbound <= step.getLowerBound()) step.limitLowerBoundOnly(loc_upperbound); 

	    	    // Wahlentscheidung durchführen
	    	    step.doStep();
	
	    	    // Entscheidungsindex abspeichern
	    	    currentActivity.addAttributetoMap("actdurcat_index",(double) step.getDecision()); 	
	        }
  			}		
  		}
    }			
  }

	/**
	 * 
	 * @param id
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private void executeStep8_MainAct_MC(String id)
	{
		// Modifizierte Zeitverteilungen zur Modellierung von höheren Auswahlwahrscheinlichkeiten bereits gewählter Zeiten
    modifiedActDurationDTDs = new DiscreteTimeDistribution[Configuration.NUMBER_OF_ACTIVITY_TYPES][Configuration.NUMBER_OF_ACT_DURATION_CLASSES];
    for (HDay currentDay : pattern.getDays())
    {
      if (currentDay.isHomeDay())
      {
      	continue;
      }
      
      // Anwendung des Modellschritts nur auf Hauptaktivitäten
      for (HTour currentTour : currentDay.getTours())
  		{
      	boolean running=false;
  			if (id.equals("8C") && currentTour.getIndex()==0) running=true;
  			if (id.equals("8E") && currentTour.getIndex()!=0) running=true;
  				
  			if (running)
  			{
		      HActivity currentActivity = currentTour.getActivity(0);
		    
          // Schritt nur durchführen, falls Dauer noch nicht festgelegt wurde
          if (!currentActivity.durationisScheduled())
	        {
          	// Objekt basierend auf der gewählten Zeitkategorie initialisieren
			      double chosenTimeCategory = currentActivity.getAttributesMap().get("actdurcat_index");
			      DefaultMCModelStep step = new DefaultMCModelStep(id + (int) chosenTimeCategory, this);
			      step.setModifiedDTDtoUse(currentActivity.getType(), (int) chosenTimeCategory);
			      
			      // angepasste Zeitverteilungen aus vorherigen Entscheidungen werden nur im koordinierten Fall verwendet
			      step.setModifyDTDAfterStep(Configuration.coordinated_modelling);
			      step.setDTDTypeToUse(INDICATOR_ACT_DURATIONS);
			      
			      // Limitiere die Obergrenze durch die noch verfügbare Zeit
			      step.setRangeBounds(0, 1440 - (currentDay.getTotalAmountOfActivityTime() + currentDay.getTotalAmountOfTripTime()));
			      
			      // Wahlentscheidung durchführen
			      step.doStep();
			     
			      // Speichere Ergebnisse ab
			      currentActivity.setDuration(step.getChosenTime());
	        }
  			}
  		}
    }
	}
	
	
	
	/**
	 * 
	 * @param id
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private void executeStep8_NonMainAct_DC(String id)
	{
		// STEP8J: TIME CLASS Step for OTHER activities
	  for (HDay currentDay : pattern.getDays())
	  {
      if (currentDay.isHomeDay())
      {
      	continue;
      }
      for (HTour currentTour : currentDay.getTours())
      {
        for (HActivity currentActivity : currentTour.getActivities())
        {
        	// Schritt nur durchführen, falls keine Hauptaktivität und Dauer noch nicht festgelegt wurde
          if (currentActivity.getIndex() != 0 && !currentActivity.durationisScheduled())
          {
          	 // AttributeLookup erzeugen
        		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
          	
      	    // Step-Objekt erzeugen
      	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
      	    
      	    // Alternativen ggf. basierend auf bereits festgelgten Dauern beschränken
	    	    int loc_upperbound = calculateUpperBoundDurationTimeClassDueToPlannedDurations(currentDay);
	    	    if (loc_upperbound <= step.getUpperBound()) step.limitUpperBoundOnly(loc_upperbound); 
	    	    if (loc_upperbound <= step.getLowerBound()) step.limitLowerBoundOnly(loc_upperbound); 
	    	    
      	    step.doStep();

      	    // Entscheidungsindex abspeichern
      	    currentActivity.addAttributetoMap("actdurcat_index",(double) step.getDecision()); 	          
          }
        }
      }
    }
	}

	/**
	 * 
	 * @param id
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private void executeStep8_NonMainAct_MC(String id)
	{
		// STEP 8K, determine other activities exact duration
		// Modifizierte Zeitverteilungen zur Modellierung von höheren Auswahlwahrscheinlichkeiten bereits gewählter Zeiten
    modifiedActDurationDTDs = new DiscreteTimeDistribution[Configuration.NUMBER_OF_ACTIVITY_TYPES][Configuration.NUMBER_OF_ACT_DURATION_CLASSES];
    for (HDay currentDay : pattern.getDays())
    {
      if (currentDay.isHomeDay())
      {
      	continue;
      }
      for (HTour currentTour : currentDay.getTours())
      {
        for (HActivity currentActivity : currentTour.getActivities())
        {
        	// Schritt nur durchführen, falls keine Hauptaktivität und Dauer noch nicht festgelegt wurde
          if (currentActivity.getIndex() != 0 && !currentActivity.durationisScheduled())
          {
          	// Objekt basierend auf der gewählten Zeitkategorie initialisieren
          	double chosenTimeCategory = currentActivity.getAttributesMap().get("actdurcat_index");
  		      DefaultMCModelStep step = new DefaultMCModelStep(id + (int) chosenTimeCategory, this);
  		      step.setModifiedDTDtoUse(currentActivity.getType(), (int) chosenTimeCategory);
  		      
  		      // angepasste Zeitverteilungen aus vorherigen Entscheidungen werden nur im koordinierten Fall verwendet
  		      step.setModifyDTDAfterStep(Configuration.coordinated_modelling);
  		      step.setDTDTypeToUse(INDICATOR_ACT_DURATIONS);

			      // Limitiere die Obergrenze durch die noch verfügbare Zeit
			      step.setRangeBounds(0, 1440 - (currentDay.getTotalAmountOfActivityTime() + currentDay.getTotalAmountOfTripTime()));
			      
			      // Wahlentscheidung durchführen
			      step.doStep();
  		     
  		      // Speichere Ergebnisse ab
  		      currentActivity.setDuration(step.getChosenTime());
          }
        }
      }
	  }
	}


	/**
	 * 
	 * @param id
	 */
	private void executeStep9A(String id)
	{
    // Step 9A: standard start time category for main tours during the week
    	
    if (person.isPersonWorkerAndWorkMainToursAreScheduled())
    {
    	 // AttributeLookup erzeugen
  		AttributeLookup lookup = new AttributeLookup(person);   	
    	
	    // Step-Objekt erzeugen
	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
	    step.doStep();

	    // Eigenschaft abspeichern
	    person.addAttributetoMap("main_tours_default_start_cat",(double) step.getDecision());
	   }
	}
	
	

	/**
	 * 
	 * @param id
	 */
	private void executeStep10A(String id)
	{
    // Step 10a: check if main tour for work/edu lies within standard start time (applies only to work/edu persons)
    if (person.isPersonWorkerAndWorkMainToursAreScheduled())
    {
	    for (HDay currentDay : pattern.getDays())
	    {
	      if (currentDay.isHomeDay())
	      {
	      	continue;
	      }
	      
	      // Bestimme Haupttour und deren Tourtyp
	      HTour currentTour = currentDay.getTour(0);
      	char tourtype = currentTour.getActivity(0).getType();
        if (tourtype == 'W' || tourtype == 'E')
        {
        	// AttributeLookup erzeugen
      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
        	
    	    // Step-Objekt erzeugen
    	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
    	    step.doStep();

    	    // Eigenschaft abspeichern
    	    currentTour.addAttributetoMap("default_start_cat_yes",(step.getAlternativeChosen().equals("yes") ? 1.0d : 0.0d));
        }
      }
    }
	}


	/**
	 * 
	 * @param id
	 * @throws InvalidPatternException
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private void executeStep10B(String id) throws InvalidPatternException
	{
	  // STEP 10b: determine time class for the start of a work/edu tour
		modifiedTourStartDTDs = new DiscreteTimeDistribution[Configuration.NUMBER_OF_ACTIVITY_TYPES][Configuration.NUMBER_OF_MAIN_START_TIME_CLASSES];
	    if (person.isPersonWorkerAndWorkMainToursAreScheduled())
	    {
	      for (HDay currentDay : pattern.getDays())
	      {
		      if (currentDay.isHomeDay())
		      {
		      	continue;
		      }
		      // Bestimme Haupttour
		      HTour currentTour = currentDay.getTour(0);
		    	char tourtype = currentTour.getActivity(0).getType();
		    	if (tourtype == 'W' || tourtype == 'E')
		      {
		    		// AttributeLookup erzeugen
		    		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
		      	
		  	    // Step-Objekt erzeugen
		  	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
		  	     		
		        // Bestimme Ober- und Untergrenze und schränke Alternativenmenge ein
		  	    modifyAlternativesForStep10B(currentDay, currentTour, step);
		  	    
		  	    // Führe Entscheidungswahl durch
		  	    step.doStep();

		  	    // Eigenschaft abspeichern
		  	    currentTour.addAttributetoMap("tourStartCat_index",(double) step.getDecision());
		      }
	      }
	    }
	}

	/**
	 * 
	 * @param id
	 * @throws InvalidPatternException
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private void executeStep10C(String id) throws  InvalidPatternException
	{
	    // Step 10c: exact start time for the work/edu tour
	    if (person.isPersonWorkerAndWorkMainToursAreScheduled())
	    {
        for (HDay currentDay : pattern.getDays())
        {

        	if (currentDay.isHomeDay())
	        {
	        	continue;
	        }
        	// Bestimme Haupttour
          HTour currentTour = currentDay.getTour(0);
        	char tourtype = currentTour.getActivity(0).getType();
        	if (tourtype == 'W' || tourtype == 'E')
          {
        		// Ermittle Entscheidung aus Schritt DC-Modellschritt  		
            double chosenStartCategory = (double) currentTour.getAttributesMap().get("tourStartCat_index");
            
            // Vorbereitungen und Objekte erzeugen
            String stepID = id + (int) chosenStartCategory;
            DefaultMCModelStep step = new DefaultMCModelStep(stepID, this);
            char mainActivityTypeInTour = currentTour.getActivity(0).getType();
            step.setModifiedDTDtoUse(mainActivityTypeInTour, (int) chosenStartCategory);
            step.setModifyDTDAfterStep(true);
            //step.setOutPropertyName("tourStartTime");
            step.setDTDTypeToUse(INDICATOR_TOUR_STARTTIMES);
            int[] bounds = calculateStartingBoundsForTours(currentDay, currentTour, false);
            step.setRangeBounds(bounds[0], bounds[1]);
            
            // Entscheidung durchführen
            step.doStep();
            
            // Speichere Ergebnisse ab
            int chosenStartTime = step.getChosenTime();
            currentTour.setStartTime(chosenStartTime);   
          }
        }
	    }
	}

	/**
	 * 
	 * @param id
	 * @throws InvalidPatternException
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private void executeStep10D(String id) throws InvalidPatternException
	{
	    // STEP 10d: determine time class for the start of all other main tours
		for (HDay currentDay : pattern.getDays())
    {
      if (currentDay.isHomeDay())
      {
      	continue;
      }
    	
      // Bestimme Haupttour
      HTour currentTour = currentDay.getTour(0);
    	
    	// Führe Schritt nur für Haupttouren aus, die noch keine festgelegte Startzeit haben
    	if (!currentTour.isScheduled())
      {
    		// AttributeLookup erzeugen
    		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
      	
  	    // Step-Objekt erzeugen
  	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
  	     		
        // Bestimme Ober- und Untergrenze und schränke Alternativenmenge ein
  	    int bounds[] = calculateStartingBoundsForMainTours(currentDay, true);
  	    int lowerbound = bounds[0];
  	    int upperbound = bounds[1];
  	    step.limitAlternatives(lowerbound, upperbound);
  	    
  	    // Führe Entscheidungswahl durch
  	    step.doStep();

  	    // Eigenschaft abspeichern
  	    currentTour.addAttributetoMap("tourStartCat_index",(double) step.getDecision());
      }
    }
	}

	/**
	 * 
	 * @param id
	 * @throws InvalidPatternException
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private void executeStep10E(String id) throws InvalidPatternException
	{
		// Step 10e: exact start time for other main tours
		modifiedTourStartDTDs = new DiscreteTimeDistribution[Configuration.NUMBER_OF_ACTIVITY_TYPES][Configuration.NUMBER_OF_MAIN_START_TIME_CLASSES];
    for (HDay currentDay : pattern.getDays())
    {
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
    	
      // Bestimme Haupttour
      HTour currentTour = currentDay.getTour(0);
    	
    	// Führe Schritt nur für Haupttouren aus, die noch keine festgelegte Startzeit haben
    	if (!currentTour.isScheduled())
      {
    		// Ermittle Entscheidung aus Schritt DC-Modellschritt  		
        double chosenStartCategory = (double) currentTour.getAttributesMap().get("tourStartCat_index");
        
        // Vorbereitungen und Objekte erzeugen
        String stepID = id + (int) chosenStartCategory;
        DefaultMCModelStep step = new DefaultMCModelStep(stepID, this);
        char mainActivityTypeInTour = currentTour.getActivity(0).getType();
        step.setModifiedDTDtoUse(mainActivityTypeInTour, (int) chosenStartCategory);
        step.setModifyDTDAfterStep(true);
        //step.setOutPropertyName("tourStartTime");
        step.setDTDTypeToUse(INDICATOR_TOUR_STARTTIMES);
        int[] bounds = calculateStartingBoundsForTours(currentDay, currentTour, false);
        step.setRangeBounds(bounds[0], bounds[1]);
        
        // Entscheidung durchführen
        step.doStep();
        
        // Speichere Ergebnisse ab
        int chosenStartTime = step.getChosenTime();
        currentTour.setStartTime(chosenStartTime);   
      }
    }
  }	


	/**
	 * 
	 * @throws InvalidPatternException
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private void executeStep10GH() throws InvalidPatternException
	{
    // Step 10g and Step10h: determine start time class for tours PRIOR to main tour and determine the exact start time
    // the tours MUST be picked in a certain order: first one (earliest tour) first.

    // reset tour start dtds
    modifiedTourStartDTDs = new DiscreteTimeDistribution[Configuration.NUMBER_OF_ACTIVITY_TYPES][Configuration.NUMBER_OF_MAIN_START_TIME_CLASSES];
    for (HDay currentDay : pattern.getDays())
    {
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
      for (int j=currentDay.getLowestTourIndex(); j<0; j++)
      {
      	HTour currentTour = currentDay.getTour(j);
      	
      	// Wenn noch keine Startzeit festgelegt wurde und die Tour vor der Haupttour liegt (Index <0)
        if (!currentTour.isScheduled())
        {
        	// 10G
        	
	      		// AttributeLookup erzeugen
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
	        	
	    	    // Step-Objekt erzeugen
	    	    DefaultDCModelStep dcstep = new DefaultDCModelStep("10G", this, lookup);
	    	     		
	          // Bestimme Ober- und Untergrenze und schränke Alternativenmenge ein
	    	    int dcbounds[] = calculateStartingBoundsForPreTours(currentDay, currentTour, true);
	    	    int lowerbound = dcbounds[0];
	    	    int upperbound = dcbounds[1];
	    	    dcstep.limitAlternatives(lowerbound, upperbound);
	    	    
	    	    // Führe Entscheidungswahl durch
	    	    dcstep.doStep();
	
	    	    // Eigenschaft abspeichern
	    	    int chosenStartCategory = dcstep.getDecision();
        	
	    	  // 10H
	    	    
	    	    // Vorbereitungen und Objekte erzeugen
	          String stepID = "10H" + (int) chosenStartCategory;
	          DefaultMCModelStep mcstep = new DefaultMCModelStep(stepID, this);
	          char mainActivityTypeInTour = currentTour.getActivity(0).getType();
	          mcstep.setModifiedDTDtoUse(mainActivityTypeInTour, (int) chosenStartCategory);
	          mcstep.setModifyDTDAfterStep(true);
	          mcstep.setDTDTypeToUse(INDICATOR_TOUR_STARTTIMES);
	          int[] mcbounds = calculateStartingBoundsForPreTours(currentDay, currentTour, false);
	          mcstep.setRangeBounds(mcbounds[0], mcbounds[1]);
	          
	          // Entscheidung durchführen
	          mcstep.doStep();
	          
	          // Speichere Ergebnisse ab
	          int chosenStartTime = mcstep.getChosenTime();
	          currentTour.setStartTime(chosenStartTime);   
        }
      }
    }
	}

	
	/**
	 * 
	 * @throws InvalidPatternException
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private void executeStep10JK() throws InvalidPatternException
  {
  	// Step 10j and Step 10k: determine start time class for tours POST to main tour and determine the exact start time
    // the tours MUST be picked in a certain order: first one after main tour first.

    // reset tour start dtds
    modifiedTourStartDTDs = new DiscreteTimeDistribution[Configuration.NUMBER_OF_ACTIVITY_TYPES][Configuration.NUMBER_OF_MAIN_START_TIME_CLASSES];
    for (HDay currentDay : pattern.getDays())
    {
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
      for (int j=1; j<=currentDay.getHighestTourIndex(); j++)
      {
      	HTour currentTour = currentDay.getTour(j);
      	// Wenn noch keine Startzeit festgelegt wurde
      	if (!currentTour.isScheduled())
        {          	
        	// 10J
    		
	      		// AttributeLookup erzeugen
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
	        	
	    	    // Step-Objekt erzeugen
	    	    DefaultDCModelStep dcstep = new DefaultDCModelStep("10J", this, lookup);
	    	     		
	          // Bestimme Ober- und Untergrenze und schränke Alternativenmenge ein
	    	    int dcbounds[] = calculateStartingBoundsForPostTours(currentDay, currentTour, true);
	    	    int lowerbound = dcbounds[0];
	    	    int upperbound = dcbounds[1];
	    	    dcstep.limitAlternatives(lowerbound, upperbound);
	    	    
	    	    // Führe Entscheidungswahl durch
	    	    dcstep.doStep();
	
	    	    // Eigenschaft abspeichern
	    	    int chosenStartCategory = dcstep.getDecision();

	    	  // 10K
	    	    
	    	    // Vorbereitungen und Objekte erzeugen
	          String stepID = "10K" + (int) chosenStartCategory;
	          DefaultMCModelStep mcstep = new DefaultMCModelStep(stepID, this);
	          char mainActivityTypeInTour = currentTour.getActivity(0).getType();
	          mcstep.setModifiedDTDtoUse(mainActivityTypeInTour, (int) chosenStartCategory);
	          mcstep.setModifyDTDAfterStep(true);
	          mcstep.setDTDTypeToUse(INDICATOR_TOUR_STARTTIMES);
	          int[] mcbounds = calculateStartingBoundsForPostTours(currentDay, currentTour, false);
	          mcstep.setRangeBounds(mcbounds[0], mcbounds[1]);
	          
	          // Entscheidung durchführen
	          mcstep.doStep();
	          
	          // Speichere Ergebnisse ab
	          int chosenStartTime = mcstep.getChosenTime();
	          currentTour.setStartTime(chosenStartTime);   
        }
      }
    } 	
  }

	/**
	 * 
	 * Legt die Startzeiten für Touren fest bei denen es bereits festgelegte Startzeiten für Aktivitäten gibt 
	 * 
	 */
	private void createTourStartTimesDueToScheduledActivities()
	{
		for (HDay currentDay : pattern.getDays())
	  {
			// Falls zu wenig Touren oder ein Heimtag vorliegt, wird der Tag übersprungen
	    if (currentDay.isHomeDay())
	    {
	    	continue;
	    }
	  	
	    for (HTour currentTour : currentDay.getTours())
	    {
	    		    
		  	// Führe Schritt nur für Touren aus, die noch keine festgelegte Startzeit haben
		  	if (!currentTour.isScheduled())
		    {
		  		
		  		// Prüfe, ob es eine Aktivität in der Tour gibt, deren Startzeit bereits festgelegt wurde (bspw. durch gemeinsame Aktivitäten
		  		int startTimeDueToScheduledActivities=-1;
		  		
	  			int tripdurations=0;
	  			int activitydurations=0;
	  			
		  		HActivity.sortActivityList(currentTour.getActivities());
		  		for (HActivity tmpact : currentTour.getActivities())
		  		{
	
		  			if (tmpact.startTimeisScheduled())
		  			{
		  				startTimeDueToScheduledActivities= tmpact.getTripStartTimeBeforeActivity() - tripdurations - activitydurations;
		  				break;
		  			}
		  			else
		  			{
		  				tripdurations += tmpact.getEstimatedTripTimeBeforeActivity();
		  				activitydurations += tmpact.getDuration();
		  			}
		  		}
		  		
		  		// Lege Startzeit fest falls durch bereits festgelegte Aktivitäten bestimmt 
		  		if (startTimeDueToScheduledActivities!=-1)
		  		{
		  			currentTour.setStartTime(startTimeDueToScheduledActivities);   
		  		}
		    }
	    }
	  }
	}

  /**
	 * 
	 * @param id
	 * @param tournrdestages
	 * @throws InvalidPatternException
	 */
	private void executeStep10(String id_dc, String id_mc, int tournrdestages) throws InvalidPatternException
	{
		// Step 10: exact start time for x tour of the day
		modifiedTourStartDTDs = new DiscreteTimeDistribution[Configuration.NUMBER_OF_ACTIVITY_TYPES][Configuration.NUMBER_OF_MAIN_START_TIME_CLASSES];
			
	  // STEP 10: determine time class for the start of the x tour of the day
		for (HDay currentDay : pattern.getDays())
	  {
			// Falls zu wenig Touren oder ein Heimtag vorliegt, wird der Tag übersprungen
	    if (currentDay.isHomeDay()|| currentDay.getAmountOfTours()<tournrdestages)
	    {
	    	continue;
	    }
	  	
	    // Bestimme x-te Tour des Tages
	    HTour currentTour = currentDay.getTour(currentDay.getLowestTourIndex()+(tournrdestages-1));
	  	
	  	// Führe Schritt nur für Touren aus, die noch keine festgelegte Startzeit haben
	  	if (!currentTour.isScheduled())
	    {
  		
  			/*
  			 * 
  			 * DC-Schritt
  			 * 
  			 */
  		
    		// AttributeLookup erzeugen
    		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
      	
  	    // Step-Objekt erzeugen
  	    DefaultDCModelStep step_dc = new DefaultDCModelStep(id_dc, this, lookup);
  	     		
        // Bestimme Ober- und Untergrenze und schränke Alternativenmenge ein
        int bounds_dc[] = calculateStartingBoundsForTours(currentTour, true);
  	    int lowerbound = bounds_dc[0];
  	    int upperbound = bounds_dc[1];
  	    step_dc.limitAlternatives(lowerbound, upperbound);
  	    
  	    // Führe Entscheidungswahl durch
  	    step_dc.doStep();

  	    // Eigenschaft abspeichern
  	    currentTour.addAttributetoMap("tourStartCat_index",(double) step_dc.getDecision());
  	    
  	    
  	    /*
  	     * 
  	     * MC-Schritt
  	     * 
  	     */
  	    
  	    // Ermittle Entscheidung aus Schritt DC-Modellschritt  		
        double chosenStartCategory = (double) currentTour.getAttributesMap().get("tourStartCat_index");
        
        // Vorbereitungen und Objekte erzeugen
        String stepID = id_mc + (int) chosenStartCategory;
        DefaultMCModelStep step_mc = new DefaultMCModelStep(stepID, this);
        char mainActivityTypeInTour = currentTour.getActivity(0).getType();
        step_mc.setModifiedDTDtoUse(mainActivityTypeInTour, (int) chosenStartCategory);
        // angepasste Zeitverteilungen aus vorherigen Entscheidungen werden nur im koordinierten Fall verwendet
        step_mc.setModifyDTDAfterStep(Configuration.coordinated_modelling);
        //step.setOutPropertyName("tourStartTime");
        step_mc.setDTDTypeToUse(INDICATOR_TOUR_STARTTIMES);
        int[] bounds_mc = calculateStartingBoundsForTours(currentTour, false);
        step_mc.setRangeBounds(bounds_mc[0], bounds_mc[1]);
        
        // Entscheidung durchführen
        step_mc.doStep();
        
        // Speichere Ergebnisse ab
        int chosenStartTime = step_mc.getChosenTime();
        currentTour.setStartTime(chosenStartTime);   	  		
		  }	       
	  }
	}



	/**
	 * 
	 * @param id
	 * @param tournrdestages
	 * @throws InvalidPatternException
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private void executeStep10DC(String id, int tournrdestages) throws InvalidPatternException
	{
	  // STEP 10m: determine time class for the start of the x tour of the day
		for (HDay currentDay : pattern.getDays())
	  {
			// Falls zu wenig Touren oder ein Heimtag vorliegt, wird der Tag übersprungen
	    if (currentDay.isHomeDay()|| currentDay.getAmountOfTours()<tournrdestages)
	    {
	    	continue;
	    }
	  	
	    // Bestimme x-te Tour des Tages
	    HTour currentTour = currentDay.getTour(currentDay.getLowestTourIndex()+(tournrdestages-1));
	  	
	  	// Führe Schritt nur für Touren aus, die noch keine festgelegte Startzeit haben
	  	if (!currentTour.isScheduled())
	    {
	  		// AttributeLookup erzeugen
	  		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
	    	
		    // Step-Objekt erzeugen
		    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
		     		
	      // Bestimme Ober- und Untergrenze und schränke Alternativenmenge ein
	      int bounds[] = calculateStartingBoundsForTours(currentDay, currentTour, true);
		    int lowerbound = bounds[0];
		    int upperbound = bounds[1];
		    step.limitAlternatives(lowerbound, upperbound);
		    
		    // Führe Entscheidungswahl durch
		    step.doStep();
	
		    // Eigenschaft abspeichern
		    currentTour.addAttributetoMap("tourStartCat_index",(double) step.getDecision());
		  }	       
	  }
	}



	/**
	 * 
	 * @param id
	 * @param tournrdestages
	 * @throws InvalidPatternException
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private void executeStep10MC(String id, int tournrdestages) throws InvalidPatternException
	{
		// Step 10n: exact start time for x tour of the day
		modifiedTourStartDTDs = new DiscreteTimeDistribution[Configuration.NUMBER_OF_ACTIVITY_TYPES][Configuration.NUMBER_OF_MAIN_START_TIME_CLASSES];
    
		for (HDay currentDay : pattern.getDays())
    {
			// Falls zu wenig Touren oder ein Heimtag vorliegt, wird der Tag übersprungen
      if (currentDay.isHomeDay()|| currentDay.getAmountOfTours()<tournrdestages)
      {
      	continue;
      }
    	
      // Bestimme x-te Tour des Tages
      HTour currentTour = currentDay.getTour(currentDay.getLowestTourIndex()+(tournrdestages-1));
    	
    	// Führe Schritt nur für Touren aus, die noch keine festgelegte Startzeit haben
    	if (!currentTour.isScheduled())
      {
    		// Ermittle Entscheidung aus Schritt DC-Modellschritt  		
        double chosenStartCategory = (double) currentTour.getAttributesMap().get("tourStartCat_index");
        
        // Vorbereitungen und Objekte erzeugen
        String stepID = id + (int) chosenStartCategory;
        DefaultMCModelStep step = new DefaultMCModelStep(stepID, this);
        char mainActivityTypeInTour = currentTour.getActivity(0).getType();
        step.setModifiedDTDtoUse(mainActivityTypeInTour, (int) chosenStartCategory);
        // angepasste Zeitverteilungen aus vorherigen Entscheidungen werden nur im koordinierten Fall verwendet
	      step.setModifyDTDAfterStep(Configuration.coordinated_modelling);
        //step.setOutPropertyName("tourStartTime");
        step.setDTDTypeToUse(INDICATOR_TOUR_STARTTIMES);
        int[] bounds = calculateStartingBoundsForTours(currentDay, currentTour, false);
        step.setRangeBounds(bounds[0], bounds[1]);
        
        // Entscheidung durchführen
        step.doStep();
        
        // Speichere Ergebnisse ab
        int chosenStartTime = step.getChosenTime();
        currentTour.setStartTime(chosenStartTime);   
      }
    }
  }	
  
  	 
    
	/**
	 * 
	 * @throws InvalidPatternException
	 */
	private void executeStep10ST() throws InvalidPatternException
	{
    // Step 10s and Step10t: determine home time before tour starts and then define tour start time
		//											 only for the fourth tour if the day and following

    // reset tour start dtds
    modifiedTourStartDTDs = new DiscreteTimeDistribution[Configuration.NUMBER_OF_ACTIVITY_TYPES][Configuration.NUMBER_OF_MAIN_START_TIME_CLASSES];
    for (HDay currentDay : pattern.getDays())
    {
    	if (currentDay.isHomeDay() || currentDay.getAmountOfTours()< 4)
      {
      	continue;
      }
      for (int j=currentDay.getLowestTourIndex()+3; j<=currentDay.getHighestTourIndex(); j++)
      {
      	HTour currentTour = currentDay.getTour(j);
      	// Bestimme Heimzeit vor Tour
        if (!currentTour.isScheduled())
        {
        	// 10S
        	
	        	// AttributeLookup erzeugen
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
	        	
	    	    // Step-Objekt erzeugen
	    	    DefaultDCModelStep dcstep = new DefaultDCModelStep("10S", this, lookup);
	    	     		
	          // Bestimme Ober- und Untergrenze und schränke Alternativenmenge ein
	          int dcbounds[] = calculateBoundsForHomeTime(currentTour, true);
	    	    int lowerbound = dcbounds[0];
	    	    int upperbound = dcbounds[1];
	    	    dcstep.limitAlternatives(lowerbound, upperbound);
	    	    
	    	    // Führe Entscheidungswahl durch
	    	    dcstep.doStep();
	
	    	    // Eigenschaft abspeichern
	    	    int chosenHomeTimeCategory = dcstep.getDecision();
        	
	    	    // 10T
	    	    
	    	    // Vorbereitungen und Objekte erzeugen
	          String stepID = "10T" + (int) chosenHomeTimeCategory;
	          DefaultMCModelStep mcstep = new DefaultMCModelStep(stepID, this);
	          char mainActivityTypeInTour = currentTour.getActivity(0).getType();
	          mcstep.setModifiedDTDtoUse(mainActivityTypeInTour, (int) chosenHomeTimeCategory);
	          // angepasste Zeitverteilungen aus vorherigen Entscheidungen werden nur im koordinierten Fall verwendet
	  	      mcstep.setModifyDTDAfterStep(Configuration.coordinated_modelling);
	          //step.setOutPropertyName("tourStartTime");
	          mcstep.setDTDTypeToUse(INDICATOR_ACT_DURATIONS);
	          int[] mcbounds = calculateBoundsForHomeTime(currentTour, false);
	          mcstep.setRangeBounds(mcbounds[0], mcbounds[1]);
	          
	          // Entscheidung durchführen
	          mcstep.doStep();
	          
	          // Speichere Ergebnisse ab
	          int starttimetour = currentDay.getTour(currentTour.getIndex()-1).getEndTime() + mcstep.getChosenTime();
	          currentTour.setStartTime(starttimetour);
        }
      }
    }
	}    
    
	/**
	 * 
	 * @param id
	 */
	private void executeStep11(String id)
	{
		
    // STEP 11 - Decision on joint activities
    for (HDay currentDay : pattern.getDays())
    {
    	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgeführt
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
      
      for (HTour currentTour : currentDay.getTours())
      {
        for (HActivity currentActivity : currentTour.getActivities())
        {
        	/* 
        	 * Falls die Aktivität nicht von der Person selbst erzeugt wurde sondern von einer anderen Person stammt
        	 * und als gemeinsame Aktivität übernommen wurde, wird der Schritt übersprungen
        	 */
        	if (currentActivity.getCreatorPersonIndex() != person.getPersIndex())
        	{
        		continue;
        	}
        	
        	
        	/*
      		 * Schritte nur durchführen, falls Person nicht als letzte Person eines Haushalts modelliert wird
      		 * Bei letzter Person im Haushalt können keine weiteren neuen gemeinsamen Aktivitäten mehr erzeugt werden!
      		 */
      		if ((int) person.getAttributefromMap("numbermodeledinhh") != person.getHousehold().getNumberofPersonsinHousehold())
      		{
	        	// AttributeLookup erzeugen
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
	        	
	    	    // Step-Objekt erzeugen
	    	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
	    	    step.doStep();
	
	          // Status festlegen
	    	    currentActivity.setJointStatus(Integer.parseInt(step.getAlternativeChosen()));
      		}
    	    else
    	    {
    	    	// Falls letzte Person, sind keine weiteren gemeinsamen Aktionen möglich
	    	    currentActivity.setJointStatus(4);
    	    }
        }
      }
    }
	}

    
    
	/**
	 * 
	 * Bestimmung des genaueren Aktivitätenzwecks für den Zweck Shopping 'S', 'L' und 'W'
	 * 
	 * @param activity
	 * @param id
	 */
	private void executeStep98(HActivity activity, String id)
	{
	  // STEP 98A-C Verfeinerung Aktivitätenzweck SHOPPING, LEISURE und WORK
	  
		HDay currentDay = activity.getDay();
		HTour currentTour = activity.getTour();
	
		// AttributeLookup erzeugen
		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, activity);   	
		
	  // Step-Objekt erzeugen
	  DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
	  step.doStep();
	
	  // Speichere gewählte Entscheidung für weitere Verwendung
	  int chosenActivityType = Integer.parseInt(step.getAlternativeChosen());
	  
	  // Aktivitätstyp festlegen
	  activity.setMobiToppActType((byte) chosenActivityType);          
	}



	/**
	 * 
	 * limits the range of alternatives if "standarddauer == 1" in step 8a. See documentation for further details
	 * 
	 * @param activity
	 * @param step
	 */
	private void modifyAlternativesDueTo8A(HActivity activity, DefaultDCModelStep step)
	{
    // Limitiere die Alternativen, falls Ergebnis von 8A YES ist
    if (activity.getAttributesMap().get("standarddauer") == 1.0d)
    {
    	// Ermittle die Standard-Zeitkategorie für den Tag und den Zweck
      int timeCategory = activity.calculateMeanTimeCategory();
      	        
      int from = timeCategory - 1;
      int to = timeCategory + 1;
        
      // Behandlung der Sonderfälle
      
      // untere Grenze liegt in Zeitklasse 0
      if (from<0)
      {
      	from=0;
      }
      // obere Grenze liegt in letzter Zeitklasse
      if (to>Configuration.NUMBER_OF_ACT_DURATION_CLASSES-1)
      {
      	to=Configuration.NUMBER_OF_ACT_DURATION_CLASSES-1;
      }
        
      step.limitAlternatives(from, to);
      // add utility bonus of 10% to average time class (middle of the 3 selected)
      step.applyUtilityModification(timeCategory, 1.10);
    }
	}


	/**
	 * 
	 * Bestimmt die Obergrenze für die Aktivitätendauern auf Basis bereits geplanter Aktivitäten.
	 * 
	 * @param act
	 * @return
	 * @throws InvalidPatternException
	 */
	private int calculateMaxdurationDueToScheduledActivities(HActivity act) throws InvalidPatternException
	{
		// Suche die nächste nachfolgende Aktivität, deren Startzeit bereits festgelegt ist
		HDay dayofact = act.getDay();
		
		HActivity last_act_scheduled = null;
		HActivity next_act_scheduled = null;

		for (HActivity tmpact : dayofact.getAllActivitiesoftheDay())
		{
			
			// Suche nach letzter im Tagesverlauf bereits festgelegter Startzeit einer Aktivität
			if(act.compareTo(tmpact)==-1)		// Findet alle früheren Aktivität als die Aktivität selbst	
			{
				//System.out.println(tmpact.getTour().getIndex() + "/" + tmpact.getIndex());
				if(tmpact.startTimeisScheduled() && (last_act_scheduled==null || tmpact.getStartTime()>last_act_scheduled.getStartTime())) last_act_scheduled = tmpact;
			}	
			
			// Suche nach nächster im Tagesverlauf bereits festgelegter Startzeit einer Aktivität
			if(act.compareTo(tmpact)==+1)
			{
				//System.out.println(tmpact.getTour().getIndex() + "/" + tmpact.getIndex());
				if(tmpact.startTimeisScheduled() && (next_act_scheduled==null || tmpact.getStartTime()<next_act_scheduled.getStartTime())) next_act_scheduled = tmpact;
			}	
		}
				
		// Addiere alle vorher festgelegten Weg- und Aktivitätendauern
		int activitydurationsincelastscheduled = countActivityDurationsbetweenActivitiesofOneDay(last_act_scheduled, act);
		int tripdurationssincelastscheduled = countTripDurationsbetweenActivitiesofOneDay(last_act_scheduled, act);
		int activitydurationuntilnextscheduled = countActivityDurationsbetweenActivitiesofOneDay(act, next_act_scheduled);
		int tripdurationsuntilnextscheduled = countTripDurationsbetweenActivitiesofOneDay(act, next_act_scheduled);
		
		int endtimelastscheduled=-1;
		if (last_act_scheduled!=null)
		{
			endtimelastscheduled = last_act_scheduled.getStartTime() + (last_act_scheduled.durationisScheduled() ?  last_act_scheduled.getDuration() : Configuration.FIXED_ACTIVITY_TIME_ESTIMATOR); 
		}
		else
		{
			endtimelastscheduled = 0;
		}
		assert endtimelastscheduled!=-1 : "endtimelastscheduled konnte nicht bestimmt werden!";
		
		int starttimenextscheduled = (next_act_scheduled == null) ? 1440 : next_act_scheduled.getStartTime();
		
		// Bestimme obere und untere Schranken
		int lowerbound = endtimelastscheduled + activitydurationsincelastscheduled + tripdurationssincelastscheduled;
		int upperbound = starttimenextscheduled - activitydurationuntilnextscheduled - tripdurationsuntilnextscheduled;
		
		int maxduration = upperbound - lowerbound;
    // Fehlerbehandlung, falls UpperBound kleiner ist als LowerBound
    if (upperbound<lowerbound)
    {
    	String errorMsg = "Duration Bounds incompatible Tour " + act.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
    	throw new InvalidPatternException(pattern, errorMsg);
    }
		
		return maxduration;
	
	}
	
	private int getDurationTimeClassforExactDuration (int maxduration)
	{
    int timeClass=-1;
		
		// Bestimme die daraus resultierende Zeitklasse
    for (int i = 0; i < Configuration.NUMBER_OF_ACT_DURATION_CLASSES; i++)
    {
        if (maxduration >= Configuration.ACT_TIME_TIMECLASSES_LB[i] && maxduration <= Configuration.ACT_TIME_TIMECLASSES_UB[i])
        {
        	timeClass = i;
        }
    }  
    assert timeClass!=-1 : "TimeClass konnte nicht bestimmt werden!";
    return timeClass;
	}
	
	/**
	 * 
	 * Bestimmt die Aktivitätendauern zwischen zwei Aktivitäten eines Tages
	 * 
	 * @param actfrom
	 * @param actto
	 * @return
	 */
	private int countActivityDurationsbetweenActivitiesofOneDay(HActivity actfrom, HActivity actto) 
	{
		int result=0;
		List<HActivity> tagesaktliste;
		if (actfrom==null)
		{
			tagesaktliste = actto.getDay().getAllActivitiesoftheDay();
		}
		else 
		{
			tagesaktliste = actfrom.getDay().getAllActivitiesoftheDay();
		}
		
		for (HActivity tmpact : tagesaktliste)
		{
			// Suche alle Aktivitäten die zwischen from und to liegen und addiere die Aktivitätszeit auf das Ergebnis
			if (	 (actfrom== null && actto!= null 																&& actto.compareTo(tmpact)<0)
					|| (actfrom!= null && actto!= null && actfrom.compareTo(tmpact)>0	&& actto.compareTo(tmpact)<0)
					|| (actfrom!= null && actto== null && actfrom.compareTo(tmpact)>0															)
					)
			{
				if (tmpact.durationisScheduled())
				{
					result += tmpact.getDuration();
				}
				else
				{
					result += Configuration.FIXED_ACTIVITY_TIME_ESTIMATOR;
				}
			}
		}
		return result;
	}
	
	/**
	 * 
	 * Bestimmt die Wegdauern zwischen zwei Aktivitäten eines Tages
	 * 
	 * @param actfrom
	 * @param actto
	 * @return
	 */
	private int countTripDurationsbetweenActivitiesofOneDay(HActivity actfrom, HActivity actto) 
	{
		int result=0;		
		List<HActivity> tagesaktliste;
		if (actfrom==null)
		{
			tagesaktliste = actto.getDay().getAllActivitiesoftheDay();
		}
		else 
		{
			tagesaktliste = actfrom.getDay().getAllActivitiesoftheDay();
		}
		
		for (HActivity tmpact : tagesaktliste)
		{
			// Suche alle Aktivitäten die zwischen from und to (inkl. to) liegen und addiere die Wegzeiten auf das Ergebnis
			if (	 (actfrom== null && actto!= null 																  && actto.compareTo(tmpact)<=0)
					|| (actfrom!= null && actto!= null && actfrom.compareTo(tmpact)>=0	&& actto.compareTo(tmpact)<=0)
					|| (actfrom!= null && actto== null && actfrom.compareTo(tmpact)>=0															 )
				 )
			{
				if (actto != null && actto.compareTo(tmpact)==0)
				{
					result += tmpact.getEstimatedTripTimeBeforeActivity();
				}
				else if (actfrom != null && actfrom.compareTo(tmpact)==0)
				{
					if (tmpact.isActivityLastinTour()) result += tmpact.getEstimatedTripTimeAfterActivity();
				}
				else
				{
					result += tmpact.getEstimatedTripTimeBeforeActivity();
					if (tmpact.isActivityLastinTour()) result += tmpact.getEstimatedTripTimeAfterActivity();
				}
			}
		}
		return result;
	}
	

	/**
	 * 
	 * @param day
	 * @param tour
	 * @param categories
	 * @return
	 * @throws InvalidPatternException
	 */
	private int[] calculateBoundsForHomeTime(HTour tour, boolean categories) throws InvalidPatternException
  {
		HDay tourday = tour.getDay();
		
  	// lowerbound startet mit 1 - upperbound mit 1440 (maximale Heimzeit)
    int lowerbound = 1;
    int upperbound = 1440;
    
    int lowercat = -1;
    int uppercat = -1;   
    
    // Bestimme obere Grenze basierend auf bereits festgelegten Startzeitpunkten der im weiteren Tagesverlauf folgenden Touren
 	  int tmptourdurations = 0;
 	  for (int i = tour.getIndex(); i <= tourday.getHighestTourIndex(); i++)
 	  {
 	  	HTour tmptour = tourday.getTour(i);
 	  	
 	  	// Sobald eine bereits geplante Tour gefunden wurde wird von diesem Punkt ausgegangen die obere Grenze berechnet
 	  	if (tmptour.isScheduled())
 	  	{
 	  		upperbound = tmptour.getStartTime() - tmptourdurations;
 	  		break;
 	  	}
 	  	// Sollte die Tour noch nicht verplant sein wird die Dauer der Tour in die Grenzenberechnung mit einbezogen
 	  	else
 	  	{
 	  		// +1 um jeweils nach der Tour noch eine Heimaktivität von min. einer Minute zu ermöglichen
 	  		tmptourdurations += tmptour.getTourDuration() + 1;
 	  	}
 	  	// Falls Schleife bis zur letzten Tour läuft gibt es keine festgelegten Startzeiten und die Obergrenze kann basierend auf den Tourdauern bestimmt werden
 	  	if (tmptour.getIndex()==tourday.getHighestTourIndex())
 	  	{
 	  		upperbound -= tmptourdurations;
 	  	}
 	  }
 	  
 	  // Upperbound wird zusätzlich durch das Ende der vorherigen Tour (= schon verbrauchte Zeit) bestimmt
    upperbound -= tourday.getTour(tour.getIndex()-1).getEndTime();
    
    
    
    
    
    
    
    
    
    
    
    
    
    
          
    // Fehlerbehandlung, falls UpperBound kleiner ist als LowerBound
    if (upperbound<lowerbound)
    {
    	String errorMsg = "HomeTime Tour " + tour.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
    	throw new InvalidPatternException(pattern, errorMsg);
    }

    // Zeitklassen falls erforderlich
    if(categories)
    {
      // Setze die Zeiten in Kategorien um
      for (int i=0; i<Configuration.NUMBER_OF_HOME_DURATION_CLASSES; i++)
      {
      	if (lowerbound>=Configuration.HOME_TIME_TIMECLASSES_LB[i] && lowerbound<=Configuration.HOME_TIME_TIMECLASSES_UB[i])
      	{
      		lowercat =i;
      	}
      	if (upperbound>=Configuration.HOME_TIME_TIMECLASSES_LB[i] && upperbound<=Configuration.HOME_TIME_TIMECLASSES_UB[i])
      	{
      		uppercat =i;
      	}
      }
    }
            
    // Fehlerbehandlung, falls Kategorien nicht gesetzt werden konnten
    if(categories)
    {
	    if (uppercat==-1 || lowercat==-1)
	    {
	    	String errorMsg = "HomeTime Tour " + tour.getIndex() + " : Could not identify categories - UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	    	throw new InvalidPatternException(pattern, errorMsg);
	    }
    }
      
    int[] bounds = new int[2];
    if (categories)
    {
    	bounds[0] = lowercat;
    	bounds[1] = uppercat;
    }
    if (!categories)
    {
    	bounds[0] = lowerbound;
    	bounds[1] = upperbound;
    }
    return bounds;
  }
	
	
	
	/**
	 * 
	 * Bestimmt die Ober- und Untergrenze der Startzeiten für Touren basierend auf möglichen schon festgelegten Startzeiten und Dauern
	 * Boolean-Wert categories bestimmt, ob die Zeitkategorien oder die konkreten Grenzwerte zurückgegeben werden
	 * 
	 * @param categories
	 * @param tour
	 * @return
	 * @throws InvalidPatternException
	 */
	private int[] calculateStartingBoundsForTours(HTour tour, boolean categories) throws InvalidPatternException
	{
		// lowerbound startet mit 1 - upperbound mit 1440 (0 Uhr nächster Tag)
	  int lowerbound = 1;
	  int upperbound = 1440;
	  
	  int lowercat = -1;
	  int uppercat = -1;
	  
	  HDay tourday = tour.getDay();
	          
	  // Falls es sich nicht um die erste Tour des Tages handelt, wird lowerbound durch das Ende der vorhergehenden Tour bestimmt
	  if (tour.getIndex() != tourday.getLowestTourIndex())
	  {
	  	lowerbound = tourday.getTour(tour.getIndex()-1).getEndTime() + 1;
	  }
	  
	  
	  // Bestimme obere Grenze basierend auf bereits festgelegten Startzeitpunkten der im weiteren Tagesverlauf folgenden Touren
	  int tmptourdurations = 0;
	  for (int i = tour.getIndex(); i <= tourday.getHighestTourIndex(); i++)
	  {
	  	HTour tmptour = tourday.getTour(i);
	  	
	  	// Sobald eine bereits geplante Tour gefunden wurde wird von diesem Punkt ausgegangen die obere Grenze berechnet
	  	if (tmptour.isScheduled())
	  	{
	  		upperbound = tmptour.getStartTime() - tmptourdurations;
	  		break;
	  	}
	  	// Sollte die Tour noch nicht verplant sein wird die Dauer der Tour in die Grenzenberechnung mit einbezogen
	  	else
	  	{
	  		// +1 um jeweils nach der Tour noch eine Heimaktivität von min. einer Minute zu ermöglichen
	  		tmptourdurations += tmptour.getTourDuration() + 1;
	  	}
	  	// Falls Schleife bis zur letzten Tour läuft gibt es keine festgelegten Startzeiten und die Obergrenze kann basierend auf den Tourdauern bestimmt werden
	  	if (tmptour.getIndex()==tourday.getHighestTourIndex())
	  	{
	  		upperbound = upperbound - tmptourdurations;
	  	}
	  }
	  
	        
	  // Fehlerbehandlung, falls UpperBound kleiner ist als LowerBound
	  if (upperbound<lowerbound)
	  {
	  	String errorMsg = "TourStartTimes Tour " + tour.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	  	throw new InvalidPatternException(pattern, errorMsg);
	  }
	
	  // Zeitklassen für erste Tour des Tages
	  if(categories && tour.getIndex()== tourday.getLowestTourIndex())
	  {
	    // Setze die Zeiten in Kategorien um
	      for (int i=0; i<Configuration.NUMBER_OF_MAIN_START_TIME_CLASSES; i++)
	      {
	      	if (lowerbound>=Configuration.MAIN_TOUR_START_TIMECLASSES_LB[i] && lowerbound<=Configuration.MAIN_TOUR_START_TIMECLASSES_UB[i])
	      	{
	      		lowercat =i;
	      	}
	      	if (upperbound>=Configuration.MAIN_TOUR_START_TIMECLASSES_LB[i] && upperbound<=Configuration.MAIN_TOUR_START_TIMECLASSES_UB[i])
	      	{
	      		uppercat =i;
	      	}
	      }
	    }
	
	    // Zeitklassen für zweite und dritte Tour des Tages
	  if(categories && tour.getIndex()!= tourday.getLowestTourIndex())
	  {
	    // Setze die Zeiten in Kategorien um
	    for (int i=0; i<Configuration.NUMBER_OF_SECTHR_START_TIME_CLASSES ; i++)
	    {
	    	if (lowerbound>=Configuration.SECTHR_TOUR_START_TIMECLASSES_LB[i] && lowerbound<=Configuration.SECTHR_TOUR_START_TIMECLASSES_UB[i])
	    	{
	    		lowercat =i;
	    	}
	    	if (upperbound>=Configuration.SECTHR_TOUR_START_TIMECLASSES_LB[i] && upperbound<=Configuration.SECTHR_TOUR_START_TIMECLASSES_UB[i])
	    	{
	    		uppercat =i;
	    	}
	    }
	  }
	          
	  // Fehlerbehandlung, falls Kategorien nicht gesetzt werden konnten
	  if(categories)
	  {
	    if (uppercat==-1 || lowercat==-1)
	    {
	    	String errorMsg = "TourStartTimes Tour " + tour.getIndex() + " : Could not identify categories - UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	    	throw new InvalidPatternException(pattern, errorMsg);
	    }
	  }
	    
	  int[] bounds = new int[2];
	  if (categories)
	  {
	  	bounds[0] = lowercat;
	  	bounds[1] = uppercat;
	  }
	  if (!categories)
	  {
	  	bounds[0] = lowerbound;
	  	bounds[1] = upperbound;
	  }
	  return bounds;
	}



	/**
	 * 
	 * max time class to be chosen= min(1440-totalDailyTripTime-durationOfMainActs, mainActDur-1))
	 * 
	 * Vereinfacht durch einheitlichere Methode calculateUpperBoundDurationTimeClassDueToPlannedDurations
	 * 
	 * @param day
	 * @param acttour
	 * @param step8j
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private void modifyAlternativesForStep8J(HDay day, HTour acttour, DefaultDCModelStep step8j)
	{
	  //Optimierungspotential - die Aktivität darf nicht länger sein als die noch übrige Zeit - 
	  //nicht nur abhängig von den Hauptaktivitäten sondern auch den restlichen bisher festgelegten Aktivitäten
	  
		// Obergrenze 1 - bisher festgelegte "verbrauchte" Zeiten am Tag
		int totalMainActivityTime = 0;
		// Alle Hauptaktivitäten + zugehörige Wege + letzter Weg am Ende der Tour
	    for (HTour tour : day.getTours())
	    {
	    	totalMainActivityTime += tour.getActivity(0).getDuration() + tour.getActivity(0).getEstimatedTripTimeBeforeActivity() + tour.getLastActivityInTour().getEstimatedTripTimeAfterActivity();
	    }
	    // Obergrenze 1
	    int remainingTimeUpperBound = 1440 - totalMainActivityTime;
	  
	  // Obergrenze 2 - Aktivität muss kürzer sein als Hauptaktivität auf Tour 
	
	    // Tim (08.11.2016) - Obergrenze default auf 99999 Minuten gesetzt, das heißt ohne Wirkung, da sonst zu kurze Aktivitäten)
	    // int maxVNActTimeUpperBound = acttour.getActivity(0).getDuration() - 1;
	    int maxVNActTimeUpperBound = 9999;
	
	  int maxTimeForActivity = Math.min(remainingTimeUpperBound, maxVNActTimeUpperBound);
	
	  // Bestimme die daraus resultierende Zeitklasse
	  int maxTimeClass = 0;
	  for (int i = 1; i < Configuration.NUMBER_OF_ACT_DURATION_CLASSES; i++)
	  {
	      if (maxTimeForActivity >= Configuration.ACT_TIME_TIMECLASSES_LB[i] && maxTimeForActivity <= Configuration.ACT_TIME_TIMECLASSES_UB[i])
	      {
	          maxTimeClass = i;
	      }
	  }
	  step8j.limitAlternatives(0, maxTimeClass);
	
	}



	/**
	 * 
	 * limits the logit alternatives for step10b and calculates upper and lower bounds
	 * 
	 * @param day
	 * @param tour
	 * @param modelstep
	 * @throws InvalidPatternException
	 */
	@Deprecated
	private void modifyAlternativesForStep10B(HDay day, HTour tour, DefaultDCModelStep modelstep) throws InvalidPatternException
	{
		// Bestimme die Grenzen auf Basis bereits festgelegter Aktivitätszeiten
	  int bounds[] = calculateStartingBoundsForMainTours(day, true);
	  int lowerbound = bounds[0];
	  int upperbound = bounds[1];
	
	  // Prüfe, ob die Tour im Standard-Startzeitraum liegt
	  boolean tourInStdStartTime = false;
	
	  double val = tour.getAttributesMap().get("default_start_cat_yes");
	  tourInStdStartTime = (val >= 1.0) ? true : false;
	
	  if (tourInStdStartTime)
	  {
	  	int default_start_category = (int) person.getAttributefromMap("main_tours_default_start_cat");
	  	// Standard-Startzeitraum liegt innerhalb der Grenzen
	  	if (default_start_category >= lowerbound && default_start_category<= upperbound)
	  	{
	  		lowerbound = default_start_category;
	  		upperbound = default_start_category;
	  	}
	  	// Standard-Startzeitraum liegt unterhalb der Untergrenze
	  	if (default_start_category < lowerbound)
	  	{
	  		// lowerbound = lowerbound;
	  		upperbound = lowerbound;
	  	}
	  	// Standard-Startzeitraum liegt oberhalb der Obergrenze
	  	if (default_start_category < lowerbound)
	  	{
	  		lowerbound = upperbound;
	  		// upperbound = upperbound;
	  	}
	  }
	  // Schränke die Alternativen entsprechend der Grenzen ein
	  modelstep.limitAlternatives(lowerbound, upperbound);
	
	}



	@Deprecated
	private int calculateUpperBoundDurationTimeClassDueToPlannedDurations(HDay day)
	{
		// verbleibende Zeit am Tag für Aktivitäten
		int remainingTimeonDay = 1440 - (day.getTotalAmountOfActivityTime() + day.getTotalAmountOfTripTime());
		
		// Obergrenze 2 (für NICHT-Hauptaktivitäten) - Aktivität muss kürzer sein als Hauptaktivität auf Tour 
		// Tim (08.11.2016) - Obergrenzeinaktiv gesetzt, das heißt ohne Wirkung, da sonst zu kurze Aktivitäten
		
	  // Bestimme die daraus resultierende Zeitklasse
	  int maxTimeClass = 0;
	  for (int i = 1; i < Configuration.NUMBER_OF_ACT_DURATION_CLASSES; i++)
	  {
	      if (remainingTimeonDay >= Configuration.ACT_TIME_TIMECLASSES_LB[i] && remainingTimeonDay <= Configuration.ACT_TIME_TIMECLASSES_UB[i])
	      {
	          maxTimeClass = i;
	      }
	  }  
	  return maxTimeClass;
	}



	/**
   * 
   * Bestimmt die Ober- und Untergrenze der Startzeiten für Touren
   * Boolean-Wert categories bestimmt, ob die Zeitkategorien oder die konkreten Grenzwerte zurückgegeben werden
   * 
   * @param categories
   * @param day
   * @param tour
   * @return
   * @throws InvalidPatternException
   */
	@Deprecated
  private int[] calculateStartingBoundsForTours(HDay day, HTour tour, boolean categories) throws InvalidPatternException
  {
  	// lowerbound startet mit 1 - upperbound mit 1440 (0 Uhr nächster Tag)
    int lowerbound = 1;
    int upperbound = 1440;
    
    int lowercat = -1;
    int uppercat = -1;
         
    // Falls es sich nicht um die erste Tour des Tages handelt, wird lowerbound durch das Ende der vorhergehenden Tour bestimmt
    if (tour.getIndex() != day.getLowestTourIndex())
    {
    	lowerbound = day.getTour(tour.getIndex()-1).getEndTime() + 1;
    }
    
    // Gehe alle Touren des Tages durch und berücksichtige bereits feststehende Dauern für die Festlegung der Grenzen
    // +1 um jeweils nach der Tour noch eine Heimaktivität von min. einer Minute zu ermöglichen
    for (int i = tour.getIndex(); i <= day.getHighestTourIndex(); i++)
    {
    	upperbound -= day.getTour(i).getTourDuration() + 1;
    }
          
    // Fehlerbehandlung, falls UpperBound kleiner ist als LowerBound
    if (upperbound<lowerbound)
    {
    	String errorMsg = "TourStartTimes Tour " + tour.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
    	throw new InvalidPatternException(pattern, errorMsg);
    }

    // Zeitklassen für erste Tour des Tages
    if(categories && tour.getIndex()== day.getLowestTourIndex())
    {
      // Setze die Zeiten in Kategorien um
        for (int i=0; i<Configuration.NUMBER_OF_MAIN_START_TIME_CLASSES; i++)
        {
        	if (lowerbound>=Configuration.MAIN_TOUR_START_TIMECLASSES_LB[i] && lowerbound<=Configuration.MAIN_TOUR_START_TIMECLASSES_UB[i])
        	{
        		lowercat =i;
        	}
        	if (upperbound>=Configuration.MAIN_TOUR_START_TIMECLASSES_LB[i] && upperbound<=Configuration.MAIN_TOUR_START_TIMECLASSES_UB[i])
        	{
        		uppercat =i;
        	}
        }
	    }
 
	    // Zeitklassen für zweite und dritte Tour des Tages
    if(categories && tour.getIndex()!= day.getLowestTourIndex())
    {
      // Setze die Zeiten in Kategorien um
      for (int i=0; i<Configuration.NUMBER_OF_SECTHR_START_TIME_CLASSES ; i++)
      {
      	if (lowerbound>=Configuration.SECTHR_TOUR_START_TIMECLASSES_LB[i] && lowerbound<=Configuration.SECTHR_TOUR_START_TIMECLASSES_UB[i])
      	{
      		lowercat =i;
      	}
      	if (upperbound>=Configuration.SECTHR_TOUR_START_TIMECLASSES_LB[i] && upperbound<=Configuration.SECTHR_TOUR_START_TIMECLASSES_UB[i])
      	{
      		uppercat =i;
      	}
      }
    }
            
    // Fehlerbehandlung, falls Kategorien nicht gesetzt werden konnten
    if(categories)
    {
	    if (uppercat==-1 || lowercat==-1)
	    {
	    	String errorMsg = "TourStartTimes Tour " + tour.getIndex() + " : Could not identify categories - UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	    	throw new InvalidPatternException(pattern, errorMsg);
	    }
    }
      
    int[] bounds = new int[2];
    if (categories)
    {
    	bounds[0] = lowercat;
    	bounds[1] = uppercat;
    }
    if (!categories)
    {
    	bounds[0] = lowerbound;
    	bounds[1] = upperbound;
    }
    return bounds;
  }
  
  
  /**
   * 
   * Bestimmt die Ober- und Untergrenze der Startzeiten für Haupttouren
   * Boolean-Wert categories bestimmt, ob die Zeitkategorien oder die konkreten Grenzwerte zurückgegeben werden
   * 
   * @param categories
   * @param day
   * @return
   * @throws InvalidPatternException
   */
  @Deprecated
  private int[] calculateStartingBoundsForMainTours(HDay day, boolean categories) throws InvalidPatternException
  {
  	// lowerbound startet mit 0 - upperbound mit 1619 (2h59 nachts nächster Tag)
    int lowerbound = 0;
    int upperbound = 1619;
    
    int lowercat = -1;
    int uppercat = -1;
    
    // Gehe alle Touren des Tages durch und berücksichtige bereits feststehende Dauern für die Festlegung der Grenzen
    for (HTour tour : day.getTours())
    {
    	// Touren vor der Haupttour
        if (tour.getIndex() < 0)
        {
        	lowerbound += tour.getTourDuration();
        }
        // Haupttour und Touren nach der Haupptour
        if (tour.getIndex() >= 0)
        {
            upperbound -= tour.getTourDuration();
        }
    }
    
    // Auf die untere Grenze kommt noch die Zeit für den Startweg der aktuellen Tour dazu
    lowerbound += Configuration.FIXED_TRIP_TIME_ESTIMATOR;
    
    
    // Die obere Grenze wird auf maximal 23.59 Uhr nachts gesetzt für den Startzeitpunkt der Tour
    upperbound = Math.min(upperbound, 1439);
    
    // Fehlerbehandlung, falls UpperBound kleiner ist als LowerBound
    if (upperbound<lowerbound)
    {
    	String errorMsg = "MainTours: UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
    	throw new InvalidPatternException(pattern, errorMsg);
    }

    if(categories)
    {
      // Setze die Zeiten in Kategorien um
      for (int i=0; i<Configuration.NUMBER_OF_MAIN_START_TIME_CLASSES; i++)
      {
      	if (lowerbound>=Configuration.MAIN_TOUR_START_TIMECLASSES_LB[i] && lowerbound<=Configuration.MAIN_TOUR_START_TIMECLASSES_UB[i])
      	{
      		lowercat =i;
      	}
      	if (upperbound>=Configuration.MAIN_TOUR_START_TIMECLASSES_LB[i] && upperbound<=Configuration.MAIN_TOUR_START_TIMECLASSES_UB[i])
      	{
      		uppercat =i;
      	}
      }
      
      // Fehlerbehandlung, falls Kategorien nicht gesetzt werden konnten
	    if (uppercat==-1 || lowercat==-1)
	    {
	    	String errorMsg = "MainTours: Could not identify categories - UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	    	throw new InvalidPatternException(pattern, errorMsg);
	    }
    }
    
    int[] bounds = new int[2];
    if (categories)
    {
    	bounds[0] = lowercat;
    	bounds[1] = uppercat;
    }
    if (!categories)
    {
    	bounds[0] = lowerbound;
    	bounds[1] = upperbound;
    }
    return bounds;
  }


	/**
	 * 
	 * Bestimmt die Ober- und Untergrenze der Startzeiten für Touren vor der Haupttour
	 * Boolean-Wert categories bestimmt, ob die Zeitkategorien oder die konkreten Grenzwerte zurückgegeben werden
	 * 
	 * @param day
	 * @param tour
	 * @param categories
	 * @return
	 * @throws InvalidPatternException
	 */
  @Deprecated
	private int[] calculateStartingBoundsForPreTours(HDay day, HTour tour, boolean categories) throws InvalidPatternException
	{  
		HTour previousTour = tour.getPreviousTour();
    int lowerbound = 0;
    int upperbound = 0;
    int preTourTime = 0;
    
    int lowercat = -1;
    int uppercat = -1;

    if (previousTour != null)
    {
    	// Berechne den Endzeitpunkt der vorherigen Tour
    	preTourTime = previousTour.getStartTime() + previousTour.getTourDuration();

    	// Sonderbehandlung, wenn die Tour am Vortag stattgefunden hat
    	if(previousTour.getDay().getIndex()<tour.getDay().getIndex())
    	{
    		if (preTourTime > 1440)
	        {
	            preTourTime = preTourTime % 1440;
	        }
    		else
    		{
    			preTourTime = 0;
    		}
    	}        
    }
    preTourTime += Configuration.FIXED_TRIP_TIME_ESTIMATOR; // trip time to the first activity in THIS tour

    lowerbound = preTourTime;

    // UpperBound berechnen
    int preMainTourActivityDurationSum = day.getTotalAmountOfTourTimeUntilMainTour(tour);
    upperbound = day.getTour(0).getStartTime() - preMainTourActivityDurationSum;

      // Die obere Grenze wird auf maximal 23.59 Uhr nachts gesetzt für den Startzeitpunkt der Tour
      upperbound = Math.min(upperbound, 1439);
    
    // Fehlerbehandlung, falls UpperBound kleiner ist als LowerBound
    if (upperbound<lowerbound)
    {
    	String errorMsg = "PreTours: UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
    	throw new InvalidPatternException(pattern, errorMsg);
    }
    
    // Setze die Zeiten in Kategorien um
    if (categories)
    {
    	for (int i=0; i<Configuration.NUMBER_OF_PRE_START_TIME_CLASSES; i++)
	    {
	    	if (lowerbound>=Configuration.PRE_TOUR_START_TIMECLASSES_LB[i] && lowerbound<=Configuration.PRE_TOUR_START_TIMECLASSES_UB[i])
	    	{
	    		lowercat =i;
	    	}
	    	if (upperbound>=Configuration.PRE_TOUR_START_TIMECLASSES_LB[i] && upperbound<=Configuration.PRE_TOUR_START_TIMECLASSES_UB[i])
	    	{
	    		uppercat =i;
	    	}
	    }
	    
	    // Fehlerbehandlung, falls Kategorien nicht gesetzt werden konnten
	    if (uppercat==-1 || lowercat==-1)
	    {
	    	String errorMsg = "PreTours: Could not identify categories - UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	    	throw new InvalidPatternException(pattern, errorMsg);
	    }
    }
	    
    int[] bounds = new int[2];
    if (categories)
    {
    	bounds[0] = lowercat;
    	bounds[1] = uppercat;
    }
    if (!categories)
    {
    	bounds[0] = lowerbound;
    	bounds[1] = upperbound;
    }
    return bounds;
	}


	/**
	 * 
	 * Bestimmt die Ober- und Untergrenze der Startzeiten für Touren nach der Haupttour
	 * Boolean-Wert categories bestimmt, ob die Zeitkategorien oder die konkreten Grenzwerte zurückgegeben werden
	 * 
	 * @param day
	 * @param tour
	 * @param categories
	 * @return
	 * @throws InvalidPatternException
	 */
  @Deprecated
	private int[] calculateStartingBoundsForPostTours(HDay day, HTour tour, boolean categories) throws InvalidPatternException
	{		
		HTour previousTour = tour.getPreviousTour();
    int lowerbound = 0;
    int upperbound = 0;
    int preTourTime = 0;

    int lowercat = -1;
    int uppercat = -1;
	    
		// Berechne den Endzeitpunkt der vorherigen Tour - LowerBound
		preTourTime = previousTour.getStartTime() + previousTour.getTourDuration();
		lowerbound = preTourTime;
		
	  // UpperBound berechnen
    int totalRemainingActivityTime = day.getTotalAmountOfRemainingTourTime(tour);
    upperbound = 1619 - totalRemainingActivityTime;
    
    // Die obere Grenze wird auf maximal 23.59 Uhr nachts gesetzt für den Startzeitpunkt der Tour
    upperbound = Math.min(upperbound, 1439);
      
    // Fehlerbehandlung, falls UpperBound kleiner ist als LowerBound
    if (upperbound<lowerbound)
    {
    	String errorMsg = "PostTours: UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
    	throw new InvalidPatternException(pattern, errorMsg);
    }

    // Setze die Zeiten in Kategorien um
    if (categories)
    {
    	for (int i=0; i<Configuration.NUMBER_OF_POST_START_TIME_CLASSES; i++)
	    {
	    	if (lowerbound>=Configuration.POST_TOUR_START_TIMECLASSES_LB[i] && lowerbound<=Configuration.POST_TOUR_START_TIMECLASSES_UB[i])
	    	{
	    		lowercat =i;
	    	}
	    	if (upperbound>=Configuration.POST_TOUR_START_TIMECLASSES_LB[i] && upperbound<=Configuration.POST_TOUR_START_TIMECLASSES_UB[i])
	    	{
	    		uppercat =i;
	    	}
	    }
	    
	    // Fehlerbehandlung, falls Kategorien nicht gesetzt werden konnten
	    if (uppercat==-1 || lowercat==-1)
	    {
	    	String errorMsg = "PostTours: Could not identify categories - UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	    	throw new InvalidPatternException(pattern, errorMsg);
	    }
    }
    
    int[] bounds = new int[2];
    if (categories)
    {
    	bounds[0] = lowercat;
    	bounds[1] = uppercat;
    }
    if (!categories)
    {
    	bounds[0] = lowerbound;
    	bounds[1] = upperbound;
    }
    return bounds;
	}

 
  /**
	 * 
	 * @param day
	 * @param tour
	 * @param categories
	 * @return
	 * @throws InvalidPatternException
	 */
	@Deprecated
	@SuppressWarnings({"unused"})
	private int[] calculateBoundsForHomeTime(HDay day, HTour tour, boolean categories) throws InvalidPatternException
	{
		// lowerbound startet mit 1 - upperbound mit 1440 (maximale Heimzeit)
	  int lowerbound = 1;
	  int upperbound = 1440;
	  
	  int lowercat = -1;
	  int uppercat = -1;
	  
	  // Upperbound bestimmt sich aus dem Ende der vorherigen Tour (= schon verbrauchte Zeit) - Dauer der verbleibenden Touren
	  upperbound = 1440 - day.getTour(tour.getIndex()-1).getEndTime();
	  
	  // Gehe alle verbleibenden Touren des Tages durch und berücksichtige bereits feststehende Dauern für die Festlegung der Grenzen
	  for (int i = tour.getIndex(); i <= day.getHighestTourIndex(); i++)
	  {
	  	upperbound -= day.getTour(i).getTourDuration();
	  }
	  
	  
	  
	  
	  
	  
	  
	  
	  
	        
	  // Fehlerbehandlung, falls UpperBound kleiner ist als LowerBound
	  if (upperbound<lowerbound)
	  {
	  	String errorMsg = "HomeTime Tour " + tour.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	  	throw new InvalidPatternException(pattern, errorMsg);
	  }
	
	  // Zeitklassen falls erforderlich
	  if(categories)
	  {
	    // Setze die Zeiten in Kategorien um
	    for (int i=0; i<Configuration.NUMBER_OF_HOME_DURATION_CLASSES; i++)
	    {
	    	if (lowerbound>=Configuration.HOME_TIME_TIMECLASSES_LB[i] && lowerbound<=Configuration.HOME_TIME_TIMECLASSES_UB[i])
	    	{
	    		lowercat =i;
	    	}
	    	if (upperbound>=Configuration.HOME_TIME_TIMECLASSES_LB[i] && upperbound<=Configuration.HOME_TIME_TIMECLASSES_UB[i])
	    	{
	    		uppercat =i;
	    	}
	    }
	  }
	          
	  // Fehlerbehandlung, falls Kategorien nicht gesetzt werden konnten
	  if(categories)
	  {
	    if (uppercat==-1 || lowercat==-1)
	    {
	    	String errorMsg = "HomeTime Tour " + tour.getIndex() + " : Could not identify categories - UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	    	throw new InvalidPatternException(pattern, errorMsg);
	    }
	  }
	    
	  int[] bounds = new int[2];
	  if (categories)
	  {
	  	bounds[0] = lowercat;
	  	bounds[1] = uppercat;
	  }
	  if (!categories)
	  {
	  	bounds[0] = lowerbound;
	  	bounds[1] = upperbound;
	  }
	  return bounds;
	}



	/**
   * 
   * Erstellt Startzeiten für jede Aktivität
   * 
   */
  private void createStartTimesforActivities()
  {
    for (HDay day : pattern.getDays())
    {
      // ! sorts the list permanently
      HTour.sortTourList(day.getTours());
      for (HTour tour : day.getTours())
      {
        // ! sorts the list permanently
        HActivity.sortActivityList(tour.getActivities());
        for (HActivity act : tour.getActivities())
        {
        	// Bei erster Aktivität in Tour wird die Startzeit durch den Beginn der Tour bestimmt
        	if (!act.startTimeisScheduled())
        	{
        		if (act.isActivityFirstinTour())
          	{
          		act.setStartTime(tour.getStartTime() + act.getEstimatedTripTimeBeforeActivity());
          	}
          	// Ansonsten durch das Ende der vorherigen Aktivität
          	else
          	{
          		act.setStartTime(act.getPreviousActivityinTour().getEndTime() + act.getEstimatedTripTimeBeforeActivity());
          	}
        	}
        }
      }
    }
  }

  /**
   * 
   * Methode erzeugt Home-Aktivitäten zwischen den Touren
   * 
   */
  private void createHomeActivities(List<HActivity> allmodeledActivities)
  {
  	char homeact = 'H';
  	
  	if(allmodeledActivities.size()!=0)
  	{	
    	// Erstelle zunächst eine Home-Aktivität vor Beginn der ersten Tour
    	int duration1 = allmodeledActivities.get(0).getTripStartTimeBeforeActivityWeekContext();

    	// Es muss immer eine H-Akt zu Beginn möglich sein
    	assert duration1>0 : "Fehler - keine Home-Aktivität zu Beginn möglich!";
    	if (duration1>0)
    	{
    		pattern.addHomeActivity(new HActivity(pattern.getDay(0), homeact, duration1, 0));
    	}
    	
    	// Durchlaufe alle Aktivitäten in der Woche - bis auf die letzte
    	for (int i=0; i<allmodeledActivities.size()-1; i++)
    	{
    		HActivity act = allmodeledActivities.get(i);
    		// Wenn Aktivität die letzte der Tour ist, erzeuge Heimaktivität
    		if (act.isActivityLastinTour())
    		{
    			int ende_tour = act.getTour().getEndTimeWeekContext();
    			int start_next_tour = allmodeledActivities.get(i+1).getTour().getStartTimeWeekContext();
    			// Bestimme Puffer
    			int duration2 = start_next_tour - ende_tour;
    			assert duration2>0 : "Fehler - keine Home-Aktivität nach Ende der Tour möglich! - " + start_next_tour + " // " + ende_tour;
    			// Bestimme zugehörigen Tag zu der Heimaktivität
    			int day = (int) ende_tour/1440;
    			// Füge Heimaktivität in Liste hinzu
    			if (duration2>0)
    			{
    				pattern.addHomeActivity(new HActivity(pattern.getDay(day), homeact, duration2, ende_tour%1440));
    			}
    		}
    	}
    	
    	// Prüfe, ob nach der letzten Aktivität noch Zeit für Heim-Aktivität ist
    	HActivity lastact = allmodeledActivities.get(allmodeledActivities.size()-1);
    	int ende_lastTour = lastact.getTour().getEndTimeWeekContext();
    	if (ende_lastTour<10080)
    	{
    		// Bestimme Puffer
    		int duration3 = 10080 - ende_lastTour;
    		// Bestimme zugehörigen Tag zu der Heimaktivität
    		int day = (int) ende_lastTour/1440;
    		// Füge Heimaktivität in Liste hinzu
    		pattern.addHomeActivity(new HActivity(pattern.getDay(day), homeact, duration3, ende_lastTour%1440));
    	}
  	}
  	// In diesem Fall ist die Aktivitätenliste komplett leer - erzeuge eine Heimaktivität für die ganze Woche
    else
    {
    	pattern.addHomeActivity(new HActivity(pattern.getDay(0), homeact, 10080, 0));
    }
  }
  
  /**
   * 
   * Methode bestimmt, mit welchen anderen Personen, bisher im Haushalt noch nicht modellierten Personen
   * gemeinsame Aktivitäten und Wege durchgeführt werden und fügt diese den Listen der Personen hinzu
   * 
   */
	private void generateJointActionsforOtherPersons() 
	{

		for (HActivity tmpactivity : pattern.getAllOutofHomeActivities()) 
		{
			/*
			 * Aktivität in die Liste gemeinsamer Aktivitäten anderer Personen hinzufügen, falls
			 * die Aktivität gemeinsam ist UND nicht von einer anderen Person ursprünglich erzeugt wurde (das heißt keine gemeinsame Aktivität 
			 * des Ursprungs einer anderen Person ist)
			 * 
			 */
			if (tmpactivity.getJointStatus()!=4 && tmpactivity.getCreatorPersonIndex()==person.getPersIndex()) 
			{
				
				//TODO Hier Code einfügen, der bestimmt mit welcher weiteren Person die Aktivität durchgeführt wird
				/*
				 * Vereinfachung: Zufällige Auswahl einer anderen Person aus dem Haushalt
				 */
				
				// Erstelle Map mit allen anderen Personennummern im Haushalt, die noch nicht modelliert wurden und wähle zufällig eine
				Map<Integer,ActitoppPerson> otherunmodeledpersinhh = new HashMap<Integer, ActitoppPerson>();
				// Füge zunächst alle Personen des Haushalts hinzu
				otherunmodeledpersinhh.putAll(person.getHousehold().getHouseholdmembers());				
				// Entferne nacheinander alle Personen, die bereits modelliert wurden (= WeekPattern haben) oder die Person selbst sind
				List<Integer> keyValues = new ArrayList<>(otherunmodeledpersinhh.keySet());
				for (Integer key : keyValues) 
				{
					ActitoppPerson tmpperson = otherunmodeledpersinhh.get(key);
					if (tmpperson.getWeekPattern()!=null || tmpperson.getPersIndex()==person.getPersIndex()) 
					{
						otherunmodeledpersinhh.remove(key);
					}
				}
				
				if (otherunmodeledpersinhh.size()>0)
				{
					// Bestimme, mit wievielen Personen die Aktivität durchgeführt wird
					int anzahlgemeinsamerpers = 1;//(int) randomgenerator.getRandomValueBetween(1, otherunmodeledpersinhh.size(), 1);
					//TODO Wahrscheinlichkeiten für die Anzahl an Personen, die an Akt teilnehmen bestimmen!
					for (int i=1 ; i<= anzahlgemeinsamerpers; i++)
					{
						// Wähle eine zufällige Nummer der verbleibenden Personen
						List<Integer> keys = new ArrayList<Integer>(otherunmodeledpersinhh.keySet());
						Integer randomkey = keys.get(randomgenerator.getRandomPersonKey(keys.size()));
						
						// Aktivität zur Berücksichtigung bei anderer Person aufnehmen
						ActitoppPerson otherperson = otherunmodeledpersinhh.get(randomkey);
						otherperson.addJointActivityforConsideration(tmpactivity);
						
						// Diese Person aus der Liste entfernen und ggf. noch andere Personen in Akt mit aufnehmen
						otherunmodeledpersinhh.remove(randomkey);
					}
				}
			}
		}
	}
  

  /**
   * 
   * @param allActivities_inclHome
   */
	private void convertactiToppPurposesTomobiToppPurposeTypes(List<HActivity> allActivities_inclHome) 
	{
		for (HActivity act : allActivities_inclHome)
		{
			switch (act.getType())
			{
				case 'W':
					//DECISION notwendig - 1 oder 2
					executeStep98(act, "98C");
					break;
				case 'E':
					act.setMobiToppActType((byte) 3);
					break;
				case 'S':
					//DECISION notwendig - 11 oder 41 oder 42	
					executeStep98(act, "98A");
					break;
				case 'L':
					//DECISION notwendig - 12 oder 51 oder 52 oder 53 oder 77
					executeStep98(act, "98B");
					break;
				case 'T':
					act.setMobiToppActType((byte) 6);
					break;
				case 'H':
					act.setMobiToppActType((byte) 7);
					break;
				default:
					System.err.println("Ungültiger Modellaktivitätentyp");
			}
		}
	}

	/**
	 * 
	 * Methode zum Überprüfen für Überlappende Aktivitäten
	 * 
	 * @param weekpattern
	 * @return
	 * @throws InvalidPatternException
	 */
	private boolean checkOverlappingActivities(HWeekPattern weekpattern) throws InvalidPatternException
	{
			List<HActivity> allActivities = weekpattern.getAllActivities();
			HActivity.sortActivityListInWeekOrder(allActivities);
	    int lastStartTime = -1;

	    for (int i = 0; i < allActivities.size(); i++)
	    {
	        HActivity activity = allActivities.get(i);
	        int currentStartTime = activity.getStartTimeWeekContext();

	        if (i != 0)
	        {
	            HActivity involved[] = {allActivities.get(i-1),activity};
	            if (currentStartTime < lastStartTime) throw new InvalidPatternException(involved, weekpattern, "activity start order not ascending " + currentStartTime + " vs " + lastStartTime);
	        }
	
	        lastStartTime = currentStartTime;
	    }
	
	    return true;
	}
	
	
	public ModelFileBase getFileBase()
	{
	    return fileBase;
	}
	
	public RNGHelper getRandomGenerator()
	{
	    return randomgenerator;
	}

  public DiscreteTimeDistribution[][] getModifiedDTDs(int type)
  {
      switch (type)
      {
          case (INDICATOR_ACT_DURATIONS):
              return this.modifiedActDurationDTDs;
          case (INDICATOR_TOUR_STARTTIMES):
              return this.modifiedTourStartDTDs;
      }

      return null;

  }

}
