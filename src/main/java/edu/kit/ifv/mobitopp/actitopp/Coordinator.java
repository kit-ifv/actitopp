package edu.kit.ifv.mobitopp.actitopp;

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
    private DebugLoggers debugloggers;
   
    // important for Step8C: dtd tables must be modified after each MC-selection
    // process
    // After the first MC-selection we must these modified tables instead of the
    // original ones
    // each activity type gets one of these per category (1 table per (activity
    // type, week and person) * categories) -> WELST * 15
    private DiscreteTimeDistribution[][] modifiedActDurationDTDs;

    // start time for work and education categories: WE * 16
    private DiscreteTimeDistribution[][] modifiedTourStartDTDs;
    
    
    // Important for modeling joint actions
    
   	int[] numberofactsperday_lowerboundduetojointactions = {0,0,0,0,0,0,0};
  	int[] numberoftoursperday_lowerboundduetojointactions = {0,0,0,0,0,0,0};
    
    
    
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
     * Konstruktor
     * 
     * @param person
     * @param personIndex
     * @param fileBase
     */
    public Coordinator(ActitoppPerson person, ModelFileBase fileBase, RNGHelper randomgenerator, DebugLoggers debugloggers)
    {
    	this(person, fileBase, randomgenerator);
    	this.debugloggers = debugloggers;    	
    }
    
 
    
  /**
   * 
   * (Main-)Methode zur Koordination der einzelnen Modellschritte
   *
   * @return
   * @throws InvalidPatternException
   */
  public void executeModel() throws InvalidPatternException
  {
  	
  	// Durchführung der Modellschritte
  
    if (Configuration.model_joint_actions) 
    {
    	determineMinimumTourActivityBounds();
    }
  	
    executeStep1("1A", "anztage_w");
    executeStep1("1B", "anztage_e");
    executeStep1("1C", "anztage_l");
    executeStep1("1D", "anztage_s");
    executeStep1("1E", "anztage_t");
    executeStep1("1F", "anztage_immobil");
    
    executeStep1("1K", "anztourentag_mean");
    executeStep1("1L", "anzakttag_mean");
       
    executeStep2("2A");
    
    executeStep3("3A");
    executeStep3("3B");
    
    executeStep4("4A");
    
    executeStep5("5A");
    executeStep5("5B");
    
    executeStep6("6A");
    
    createTripTimesforActivities();

    // Gemeinsame Aktivitäten
    if (Configuration.model_joint_actions) 
    {
    	placeJointActivitiesIntoPattern();
    }

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
       
    createTourStartTimesDueToScheduledActivities();
    
    executeStep10("10M","10N", 1);
    executeStep10("10O","10P", 2);
    executeStep10("10Q","10R", 3);
    executeStep10ST();
    
    if (Configuration.model_joint_actions) 
    {
    	executeStep11("11");
  		// Bestimme, welche gemeinsamen Wege/Aktivitäten mit welchen anderen Personen durchgeführt werden sollen
  		selectWithWhomforJointActions();		
    }
    
					 
    // Finalisierung der Wochenaktivitätenpläne 
    
    // 1) Erstelle eine Liste mit allen Aktivitäten der Woche
    List<HActivity> allModeledActivities = pattern.getAllOutofHomeActivities();    	
    HActivity.sortActivityListbyWeekStartTimes(allModeledActivities);
	
    // 2) Erzeuge Zuhause-Aktivitäten zwischen den Touren
    createHomeActivities(allModeledActivities);
    
    // 3) Wandel die Aktivitätenzwecke des Modells zurück in mobiTopp-Aktivitätenzwecke
    convertactiToppPurposesTomobiToppPurposeTypes(pattern.getAllActivities());
    
    // DEBUG
    if (Configuration.debugenabled)
    {
    	pattern.printAllActivitiesList();
    }
    
    // first sanity checks: check for overlapping activities. if found,
    // throw exception and redo activityweek
    pattern.weekPatternisFreeofOverlaps();

  }
  
  /**
   * Bestimmt untere Grenzen für die Anzahl an Touren und Aktivitäten an jedem Tag
   * Basierend auf der Liste gemeinsamer Aktivitäten wird vorgegeben, wieviele Touren bzw. Aktivitäten 
   * an dem jeweiligen Tag mindestens vorhanden sein müssen.
   */
  private void determineMinimumTourActivityBounds()
  {
  	
  	/*
  	 * Idee:
  	 * 
  	 * - Bestimme untere Grenzen für die Anzahl an Touren und Aktivitäten an jedem Tag basierend auf der Liste gemeinsamer Aktivitäten
  	 * 
  	 * - Modelliere Anzahl an Touren und Aktivitäten (siehe Schritte 1-6)
  	 * 		- Es muss an jedem Tag mit gemeinsamen Aktivitäten mindestens eine Tour geben!
  	 * 		- Bei mehr als 2 gemeinsamen Aktivitäten mindestens 2 Touren!
  	 * 		- Es gibt für Haupt- und Vorheraktivitätenzahl keine Mindestanzahlen. Aktualisiere die Zahl der bereits modellierten Aktivitäten
  	 * 		  und lege die Grenze nur bei den Nachheraktivitäten fest. Bei mehreren Touren auf jede Tour aufteilen!
  	 * 
  	 * - Nach Schritt 6: Aktivitäten platzieren
  	 * 
  	 */
  	
  	/*
  	 * Bestimme Mindestzahl an Touren und Aktivitäten basierend auf den bereits vorhandenen gemeinsamen Aktivitäten
  	 */
  	  	
  	for (HActivity act : person.getAllJointActivitiesforConsideration())
  	{
  		// Zähle Anzahl der Aktivitäten hoch
  		numberofactsperday_lowerboundduetojointactions[act.getDayIndex()] += 1;
  		
  		// Bestimme Mindestzahl an Touren
  		// Bei max. 2 Aktivitäten nur eine Tour mindestens
  		if (numberofactsperday_lowerboundduetojointactions[act.getDayIndex()] <= 2) 
  		{
  			numberoftoursperday_lowerboundduetojointactions[act.getDayIndex()] = 1;
  		}
  		//TODO ggf. deaktivieren, da dadurch die Anzahl an Touren zu hoch wird!
  		// Bei mehr als 2 Aktivitäten mindestens zwei Touren
  		else
  		{
  			numberoftoursperday_lowerboundduetojointactions[act.getDayIndex()] = 2;
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
    double decision = Double.parseDouble(step.getAlternativeChosen());
    person.addAttributetoMap(variablenname, decision);
    
    if(debugloggers!= null && debugloggers.existsLogger(id))
    {
    	debugloggers.getLogger(id).put(person, String.valueOf(decision));
    }
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
  	    if (currentDay.getAmountOfTours()>0 || numberoftoursperday_lowerboundduetojointactions[currentDay.getIndex()]>0)
  	    {
  	    	step.disableAlternative("H"); 
	    	}
  	    
  	    if (Configuration.coordinated_modelling)
  	    {
	  	    // Entferne die Alternative W, falls bereits Anzahl der Tage mit Arbeitsaktivitäten erreicht sind!
	  	    if (
	  	    		person.getAttributefromMap("anztage_w") <= pattern.countDaysWithSpecificActivity('W') &&
	  	    		currentDay.getTotalAmountOfActivitites('W') == 0 &&
	  	    		person.getEmployment()==1
	  	    		)
	  	    {
	  	    	step.disableAlternative("W"); 
	  	    }
	  	    
	  	    // Entferne die Alternative E, falls bereits Anzahl der Tage mit Bildungsaktivitäten erreicht sind!
	  	    if (
	  	    		person.getAttributefromMap("anztage_e") <= pattern.countDaysWithSpecificActivity('E') &&
	  	    		currentDay.getTotalAmountOfActivitites('E') == 0
	  	    		)
	  	    {
	  	    	step.disableAlternative("E"); 
	  	    }
	  	    
	  	    // Nutzenbonus für Alternative W, falls Person erwerbstätig und Wochentag
	  	    if (person.getEmployment()==1 && currentDay.getWeekday()<6 && step.alternativeisEnabled("W"))
	  	    {
	  	    	step.adaptUtilityFactor("W", 1.2);
	  	    }
  	    }
   
  	    // Auswahl durchführen
  	    step.doStep();
  	    char activityType = step.getAlternativeChosen().charAt(0);
  	    
  	    // DebugLogger schreiben falls aktiviert
  	    if(debugloggers!= null && debugloggers.existsLogger(id))
  	    {
  	    	debugloggers.getLogger(id).put(currentDay, String.valueOf(activityType));
  	    }
    		
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
	    
	    // Prüfe, ob Mindesttourzahl aus lowerBound bereits erreicht wurde
	    if (currentDay.getAmountOfTours() < numberoftoursperday_lowerboundduetojointactions[currentDay.getIndex()])
	    {
	    	int verbleibendetouren = numberoftoursperday_lowerboundduetojointactions[currentDay.getIndex()] - currentDay.getAmountOfTours();
	    	// Bei 3A werden verbleibende Touren halbiert, da auch ggf. noch Touren in 3B modelliert werden können
	    	if (id.equals("3A")) mindesttourzahl = Math.round(verbleibendetouren/2);
	    	// 3B bekommt als Mindesttourenzahl alle bis dahin noch verbliebenen Touren
	    	if (id.equals("3B")) mindesttourzahl = verbleibendetouren;
	    }  
	    
	    // Alternativen limitieren basierend auf Mindestourzahl
	    step.limitLowerBoundOnly(mindesttourzahl);
	    
	    // Limitiere die Alternativen nach oben basierend auf dem Ergebnis von Schritt 1k
	    if (Configuration.coordinated_modelling)
	    {
	    	int maxtourzahl=-1;
	    	if (person.getAttributefromMap("anztourentag_mean")==1.0d) maxtourzahl=1;
	    	if (person.getAttributefromMap("anztourentag_mean")==2.0d) maxtourzahl=2;
	    	if (maxtourzahl!=-1) step.limitUpperBoundOnly((maxtourzahl>=mindesttourzahl ? maxtourzahl : mindesttourzahl));
	    }
	    
	    
	    // Entscheidung durchführen
	    step.doStep();
	    
	    // DebugLogger schreiben falls aktiviert
	    if(debugloggers!= null && debugloggers.existsLogger(id))
	    {
	    	debugloggers.getLogger(id).put(currentDay, String.valueOf(step.getDecision()));
	    }
            
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
    
      HTour.sortTourList(currentDay.getTours());
    
	    if (id.equals("3B")) assert (currentDay.getAmountOfTours() >= numberoftoursperday_lowerboundduetojointactions[currentDay.getIndex()]) : "wrong number of tours - violating lower bound due to joint actions";
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
         *  - andere Hauptaktivitäten, welche über gemeinsame Aktivitäten ins Pattern gekommen sind
         */
      	if(!currentDay.existsActivityTypeforActivity(currentTour.getIndex(),0))
        {
        	// AttributeLookup erzeugen
      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
        	
    	    // Step-Objekt erzeugen
    	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
    	    
    	    // Entferne die Alternative W, falls bereits Anzahl der Tage mit Arbeitsaktivitäten erreicht sind!
    	    if (
    	    		person.getAttributefromMap("anztage_w") <= pattern.countDaysWithSpecificActivity('W') &&
    	    		currentDay.getTotalAmountOfActivitites('W') == 0
    	    		)
    	    {
    	    	step.disableAlternative("W"); 
    	    }
    	    
    	    // Entferne die Alternative E, falls bereits Anzahl der Tage mit Bildungsaktivitäten erreicht sind!
    	    if (
    	    		person.getAttributefromMap("anztage_e") <= pattern.countDaysWithSpecificActivity('E') &&
    	    		currentDay.getTotalAmountOfActivitites('E') == 0
    	    		)
    	    {
    	    	step.disableAlternative("E"); 
    	    }
  	    
    	    //Auswahl durchführen
    	    step.doStep();

          // Speichere gewählte Entscheidung für weitere Verwendung
          char chosenActivityType = step.getAlternativeChosen().charAt(0);
          
          // DebugLogger schreiben falls aktiviert
    	    if(debugloggers!= null && debugloggers.existsLogger(id))
    	    {
    	    	debugloggers.getLogger(id).put(currentTour, String.valueOf(chosenActivityType));
    	    }
          
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
        
      for (int i = currentDay.getLowestTourIndex(); i <= currentDay.getHighestTourIndex(); i++)
      {
      	HTour currentTour = currentDay.getTour(i);
      	
      	// AttributeLookup erzeugen
    		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
      	
    	  // Step-Objekt erzeugen
  	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
    		
  	    // Mindesaktzahl festlegen, falls es schon Aktivitäten aus gemeinsamen Aktivitäten gibt
  	    int mindestaktzahl =0;
  	    
  	    // Prüfe, ob Mindestaktzahl aus lowerBound bereits erreicht wurde
  	    if (currentDay.getTotalAmountOfActivitites() < numberofactsperday_lowerboundduetojointactions[currentDay.getIndex()])
  	    {
  	    	// Bestimme noch nicht verplante Aktivitäten basierend auf Mindestzahl aus gemeinsamen Akt und bereits verplanten
  	    	int verbleibendeakt = numberofactsperday_lowerboundduetojointactions[currentDay.getIndex()] - currentDay.getTotalAmountOfActivitites();
  		
  	    	/*
  	    	 * Bestimme, wieviele Aktivitätenschätzungen in Schritt 5 noch durchgeführt werden
					 *
  	    	 * Formel: verbleibendenAnzahlanTouren * 2 (wegen 5A und 5B) - 1 (falls es schon Schritt 5B ist und damit 5A schon durchgeführt wurde
  	    	 * verbleibendeAnzahlanTouren =  currentDay.getHighestTourIndex() - aktuellerTourIndex(i) + 1
  	    	 */
  	    	int verbleibendeaktschaetzungen =  2*(currentDay.getHighestTourIndex() - i + 1) - (id.equals("5B") ? 1 : 0);
  	    	// Bestimme Mindestzahl aufgrund verbleibender Akt im Verhältnis zu verbleibenden Schätzungen 
  	    	mindestaktzahl = Math.round(verbleibendeakt/verbleibendeaktschaetzungen);
  	    	// bei der letzten Tour des Tages und der NACH-Aktivitätenzahl muss Mindestanzahl zwingend erreicht werden
  	    	if (id.equals("5B") && currentTour.getIndex() == currentDay.getHighestTourIndex()) mindestaktzahl = verbleibendeakt;
  	    }
  	    
  	    // Alternativen limitieren basierend auf Mindesaktzahl
  	    step.limitLowerBoundOnly(mindestaktzahl);
  	    
  	    // Entscheidung durchführen
  	    step.doStep();    		

  	    // DebugLogger schreiben falls aktiviert
  	    if(debugloggers!= null && debugloggers.existsLogger(id))
  	    {
  	    	debugloggers.getLogger(id).put(currentTour, String.valueOf(step.getDecision()));
  	    }
  	    
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
        
        HActivity.sortActivityListbyIndices(currentTour.getActivities());        
      }
      if (id.equals("5B")) assert (currentDay.getTotalAmountOfActivitites() >= numberofactsperday_lowerboundduetojointactions[currentDay.getIndex()]) : "wrong number of activities - violating lower bound due to joint actions";
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
      	    
      	    // Entferne die Alternative W, falls bereits Anzahl der Tage mit Arbeitsaktivitäten erreicht sind!
      	    if (
      	    		person.getAttributefromMap("anztage_w") <= pattern.countDaysWithSpecificActivity('W') &&
      	    		currentDay.getTotalAmountOfActivitites('W') == 0
      	    		)
      	    {
      	    	step.disableAlternative("W"); 
      	    }
      	    
      	    // Entferne die Alternative E, falls bereits Anzahl der Tage mit Bildungsaktivitäten erreicht sind!
      	    if (
      	    		person.getAttributefromMap("anztage_e") <= pattern.countDaysWithSpecificActivity('E') &&
      	    		currentDay.getTotalAmountOfActivitites('E') == 0
      	    		)
      	    {
      	    	step.disableAlternative("E"); 
      	    }
    	    
      	    //Auswahl durchführen
      	    step.doStep();

            // Aktivitätstyp festlegen
      	    char chosenActivityType = step.getAlternativeChosen().charAt(0);
      	    currentActivity.setType(chosenActivityType);
      	    
      	    // DebugLogger schreiben falls aktiviert
      	    if(debugloggers!= null && debugloggers.existsLogger(id))
      	    {
      	    	debugloggers.getLogger(id).put(currentActivity, String.valueOf(chosenActivityType));
      	    }
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
	 * Regelbasiert:
	 * - Wenn Tourindex der ursprünglichen Aktivität vorhanden ist, dann füge Sie in diese Tour ein
	 * - Wenn Aktindex der urspünglichen Aktivität in der Tour vorhanden ist, füge Sie an dem Platz ein	
	 * 
	 * - Wenn Tour oder Aktindex noch nicht vorhanden sind, nehme den nächstgelegenen Index, der noch nicht durch gemeinsame Akt belegt ist
	 * 		Bsp. einzufügende Akt ist 1/1/3, höchster Index ist aber 1/1/2, dann ersetze Akt 1/1/2 mit der gemeinsamen Akt
	 * 
	 * - Zeitüberlappungen beachten! Wenn Liste chronologisch abgearbeitet wird, bestimmt die Position der zuvor eingefügten Aktivität die untere Grenze
	 * 
	 * - Gemeinsame Akt mit Typ 1 oder 3 von zuhause aus muss immer erste Akt in Tour sein, auch bei eingefügter Tour	
	 * 
	 * @throws InvalidPatternException
	 * 
	 */
	private void placeJointActivitiesIntoPattern() throws InvalidPatternException
	{
		
	 	List<HActivity> listgemakt = person.getAllJointActivitiesforConsideration();
		HActivity.sortActivityListbyWeekStartTimes(listgemakt);
	
		/*
		 * Aktivitätenliste in Wochensortierung durchgehen und bestehenden Aktivität durch gemeinsame aus der Liste ersetzen
		 * 
		 */
		
		for (int indexinliste=0 ; indexinliste < listgemakt.size(); indexinliste++)
		{
			HActivity gemakt = listgemakt.get(indexinliste);
			
			// Indextag der Aktivität bestimmen
			int gemakt_tagindex = gemakt.getDayIndex();
			// Tourindex der Aktivität bestimmen
			int gemakt_tourindex = gemakt.getTour().getIndex();
			// Aktindex der Aktivität bestimmen
			int gemakt_aktindex = gemakt.getIndex();
			// JointStatus der Aktivität bestimmen
			int gemakt_jointStatus = gemakt.getJointStatus();
			
			assert gemakt_jointStatus>=1 && gemakt_jointStatus<=3 : "keine gemeinsame Aktivität in der Liste der gemeinsamen Aktivitäten!"; 
			
			
	  	/*
	  	 * Bestimme mögliche Aktivitäten, die ersetzt werden können
	  	 */
			List <HActivity> possibleact = new ArrayList<HActivity>();
			
			/*
			 *  Schritt 1: Alle verfügbaren Aktivitäten des Tages
			 */
			{
	    	for (HActivity act : pattern.getDay(gemakt_tagindex).getAllActivitiesoftheDay())
	    	{
	    		possibleact.add(act);
	    	}
	    	HActivity.sortActivityListbyIndices(possibleact);
			}
			
	  	/*
	  	 *  Schritt 2: 	Bestimme, ob es bereits getauschte Aktivitäten an diesem Tag gibt. Füge nur Aktivitäten, die nach der letzten
	  	 *  						getauschten liegen in eine neue Liste hinzu und arbeite mit dieser weiter
	  	 */
	  	{
	    	HActivity letzteaktgetauscht=null;
	    	for (HActivity act : possibleact)
	    	{
	    		if ((act.getAttributefromMap("actreplacedbyjointact")!= null ? act.getAttributefromMap("actreplacedbyjointact") : 0) == 1.0) letzteaktgetauscht=act;
	    	}
	    	if (letzteaktgetauscht!=null)
	    	{
	    		List<HActivity> possibleactlaterinweek = new ArrayList<HActivity>();
	    		for (HActivity act : possibleact)
	    		{
	    			if (act.compareTo(letzteaktgetauscht) < 0) possibleactlaterinweek.add(act);
	    		}
	    		possibleact = possibleactlaterinweek;
	    		
	    		/*
	    		 * Falls die letzte getauschte Akt nicht unmittelbar zeitlich vor der aktuellen liegt, entferne die erste mögliche Akt zum Tauschen aus
	    		 * der Liste, damit keine Touren mit Lücken entstehen!
	    		 * 
	    		 * Entferne die erste Akt zum Tauschen ebenfalls, falls diese zeitlich mit der letzten getauschten Akt überlagert
	    		 */
	    		if ((letzteaktgetauscht.getJointStatus()!=3 && 
	    				HActivity.getTimebetweenTwoActivities(letzteaktgetauscht, gemakt)!=0 && 
	    				!letzteaktgetauscht.isActivityLastinTour())
	    				||
	    				(letzteaktgetauscht.getJointStatus()!=3 && 
	    				HActivity.getTimebetweenTwoActivities(letzteaktgetauscht, gemakt)<0))	
	    			possibleact.remove(0);
	    	}
	  	}
	  	
	  	/*
	  	 * Schritt 3:	Bestimme, ob es weitere gemeinsame Aktivitäten an dem Tag gibt, die noch getauscht werden müssen
	  	 * 						Entferne entsprechend die letzten X Einträge aus der Liste möglicher Aktivitäten, damit diese noch Platz finden!
	  	 */
	  	{
		  	int anzweiteregemaktamtag=0;
	    	for (int i=indexinliste+1; i<listgemakt.size(); i++)
	    	{
	    		HActivity act = listgemakt.get(i);
	    		if (act.getDayIndex()== gemakt_tagindex) anzweiteregemaktamtag += 1;
	    	}
	    	if (anzweiteregemaktamtag>0)
	    	{
	    		for (int i=1; i<=anzweiteregemaktamtag; i++)
	    		{
	    			int letzterindex = possibleact.size()-1;
	    			possibleact.remove(letzterindex);
	    		}
	    	}
	  	}


	  	/*
	  	 * Schritt 4: Prüfen, ob List aufgrund von Schritt 2&3 möglicherweise leer ist.
	  	 * 						Falls ja, kann Aktivität nicht eigefügt werden.
	  	 */
	  	if (possibleact.size()==0) 
	  	{
	  		System.err.println("Akt konnte nicht ersetzt werden! Schritt 4");
	  		gemakt.removeJointParticipant(person);
	  		break;
	  	}
	  	
	  	
	  	/*
	  	 * Schritt 5: Gemeinsame Akt von Typ 1 oder 3, d.h. mit gemeinsamem Hinweg muss, falls es sich um die erste Aktivität
	  	 *  					auf der Tour handelt auch bei der eingefügten Aktivität die erste der Tour sein.
	  	 *  
	  	 *  					Such in solchen Fällen alle andere ersten Aktivität von Touren in der Liste möglicher Aktivitäten und 
	  	 *  					arbeite mit der neuen Liste weiter
	  	 */
	  	{
	    	if ((gemakt_jointStatus==1 || gemakt_jointStatus==3) && gemakt.isActivityFirstinTour())
	    	{
	    		List<HActivity> possibleactersteaktintour = new ArrayList<HActivity>();
	    		for (HActivity act : possibleact)
	    		{
	    			if (act.isActivityFirstinTour()) possibleactersteaktintour.add(act);
	    		}
	    		possibleact = possibleactersteaktintour;
	    	}
	  	}
	  	
	  	
	  	/*
	  	 * Schritt 6: Prüfen, ob List aufgrund von Schritt 5 möglicherweise leer ist.
	  	 * 						Falls ja, kann Aktivität nicht eigefügt werden.
	  	 */
	  	if (possibleact.size()==0) 
	  	{
	  		System.err.println("Akt konnte nicht ersetzt werden! Schritt 6");
	  		gemakt.removeJointParticipant(person);
	  		break;
	  	}
	  	
	  	
	  	/*
	  	 * Schritt 7: Prüfen, ob der Tourindex der gemeinsamen Akt in den möglichen Akt vorhanden ist (Prio zum Ersetzen!)
	  	 * 						Falls ja, dann prüfen, ob es den Aktindex auf der Tour auch gibt (Prio zum Ersetzen!)
	  	 */
	  	{
	    	// Füge alle Akt mit gleichem Tourindex in eine eigene Liste ein
	    	List<HActivity> possibleactgleichertourindex = new ArrayList<HActivity>();
	    	for (HActivity act : possibleact)
	    	{
	    		if (act.getTour().getIndex() == gemakt_tourindex)
	    		{
	    			possibleactgleichertourindex.add(act);
	    		}
	    	}
	    	// Falls Aktivität mit gleichen Tourindex existieren, arbeite mit dieser Liste weiter
	    	if (possibleactgleichertourindex.size()!=0)
	    	{
	    		possibleact = possibleactgleichertourindex;
	    	
	    		// Füge alle Akt mit gleichem Aktindex in eine eigene Liste ein
	    		List<HActivity> possibleactgleicheraktindex = new ArrayList<HActivity>();
	      	for (HActivity act : possibleact)
	      	{
	      		if (act.getIndex() == gemakt_aktindex)
	      		{
	      			possibleactgleicheraktindex.add(act);
	      		}
	      	}
	      	// Falls Aktivität mit gleichen Aktindex existiert, arbeite mit dieser Liste weiter
	      	if (possibleactgleicheraktindex.size()!=0)
	      	{
	      		possibleact = possibleactgleicheraktindex;
	      	}
	    	}
	  	}
	  	
	  	/*
	  	 * Schritt 8: Falls eine Aktivität die letzte einer Tour ist und unmittelbar anschließend eine weitere gemeinsame Aktivität folgt,
	  	 * 						dann wird diese Aktivität entfernt, da keine Zeit für Heimaktivität übrig bleibt.
	  	 * 
	  	 * 						Anders ausgedrückt: Falls direkt anschließende gemeinsame Aktivität, dann entferne alle letzten Aktivitäten einer Tour
	  	 */
	  	{
	  		if (indexinliste < listgemakt.size()-1 && HActivity.getTimebetweenTwoActivities(gemakt,  listgemakt.get(indexinliste+1))==0)
	  		{
	  			// Füge alle Akt, die nicht letzte Akt sind in eine eigene Liste ein
	    		List<HActivity> possibleactnichtletzte = new ArrayList<HActivity>();
	      	for (HActivity act : possibleact)
	      	{
	      		if (!act.isActivityLastinTour())
	      		{
	      			possibleactnichtletzte.add(act);
	      		}
	      	}
	      	possibleact = possibleactnichtletzte;    			
	  		}
	  	}
	
	  	/*
	  	 * Schritt 9: 
	  	 *
	  	 * Aufgrund von Schritt 8 kann es vorkommen, dass keine Aktivitäten mehr übrig bleiben zum Ersetzen.
	  	 * Falls das der Fall ist kann die Aktivität nicht ersetzt werden! 
	  	 */
	  	if (possibleact.size()==0) 
	  	{
	  		System.err.println("Akt konnte nicht ersetzt werden! Schritt 9");
	  		gemakt.removeJointParticipant(person);
	  		break;
	  	}
	
	  	/*
	  	 * Schritt 10: Wähle zufällig eine der verbleibenden möglichen Aktivitäten
	  	 */
	  	int zufallszahl = randomgenerator.getRandomValueBetween(0, possibleact.size()-1, 1);
	  	HActivity actforreplacement = possibleact.get(zufallszahl);
	  	
	  	/*
	  	 * Schritt 11: Aktivität durch gemeinsame Aktivität ersetzen
	  	 */
	  	{
	    	// Aktivitäteneigenschaften ermitteln
	    	int gemakt_duration = gemakt.getDuration();
	    	int gemakt_starttime = gemakt.getStartTime();
	    	char gemakt_acttype = gemakt.getType(); 		
	    	int gemakt_creatorPersonIndex = gemakt.getCreatorPersonIndex();		
	    	
	    	int gemakt_durationtripbefore = gemakt.getEstimatedTripTimeBeforeActivity();
	    	
	    	// Aktivität markieren
	    	actforreplacement.addAttributetoMap("actreplacedbyjointact", 1.0);
	    	
	    	// Je nach Art der Gemeinsamkeit unterschiedliche Aktivitäteneigenschaften ersetzen
	    	switch(gemakt_jointStatus)
				{
					// Weg davor und Aktivität werden gemeinsam durchgeführt
					case 1:
					{			
						// Akteigenschaften ersetzen
						actforreplacement.setDuration(gemakt_duration);
						actforreplacement.setStartTime(gemakt_starttime);
						actforreplacement.setType(gemakt_acttype);
						actforreplacement.setJointStatus(gemakt_jointStatus);
						actforreplacement.setCreatorPersonIndex(gemakt_creatorPersonIndex); 
						
						// Wegzeiten aufgrund möglichen anderen Aktivitätentyps neu berechnen
						actforreplacement.calculateAndSetTripTimes();
						
						// Hinweg erzeugen und ersetzen
						actforreplacement.setTripbeforeactivity(new HTrip(actforreplacement, gemakt_durationtripbefore));
			
						break;
					}
					// Nur Aktivität wird gemeinsam durchgeführt
					case 2:
					{
						// Akteigenschaften ersetzen
						actforreplacement.setDuration(gemakt_duration);
						actforreplacement.setStartTime(gemakt_starttime);
						actforreplacement.setType(gemakt_acttype);
						actforreplacement.setJointStatus(gemakt_jointStatus);
						actforreplacement.setCreatorPersonIndex(gemakt_creatorPersonIndex); 
						
						// Wegzeiten aufgrund möglichen anderen Aktivitätentyps neu berechnen
						actforreplacement.calculateAndSetTripTimes();
						
						break;
					}		
					// Nur Weg davor wird gemeinsam durchgeführt
					case 3:
					{
						// Akteigenschaften ersetzen
						actforreplacement.setJointStatus(gemakt_jointStatus);
						actforreplacement.setCreatorPersonIndex(gemakt_creatorPersonIndex); 
						
						// Weg erzeugen
						actforreplacement.setTripbeforeactivity(new HTrip(actforreplacement, gemakt_durationtripbefore));
						actforreplacement.setStartTime(gemakt_starttime);
						
						break;
					}
				}			
	  	}
	  	
	  	// Schritt 12: Prüfen, ob die Aktiviät aufgrund möglicher geänderter Wegzeiten nicht vielleicht doch mit einer anderen kollidiert
    	for (HActivity act : pattern.getDay(gemakt_tagindex).getAllActivitiesoftheDay())
    	{
    		if (
    				(act.startTimeisScheduled() 
    					&& HActivity.checkActivityOverlapping(act, actforreplacement))
    				||
    				(act.isScheduled() && act.isActivityLastinTour() && actforreplacement.isActivityFirstinTour() && act.getTourIndex()!=actforreplacement.getTourIndex()
    					&& HActivity.getTimebetweenTwoActivities(act, actforreplacement)==0)
    				||
    				(act.isScheduled() && act.isActivityFirstinTour() && actforreplacement.isActivityLastinTour() && act.getTourIndex()!=actforreplacement.getTourIndex() 
    					&& HActivity.getTimebetweenTwoActivities(act, actforreplacement)==0)
    				) 
    		{
    			String errormsg = "Activity overlapping when adding joint activity";
    			throw new InvalidPatternException("Household", pattern, errormsg);
    		}
    	}
		}
	
		
		//TODO  Sicherstellen, dass die Reihenfolge sortiert nach Index mit der nach Startzeit übereinstimmt!
		
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
	    
	    //Debug-Logger schreiben falls aktiviert
	    if(debugloggers!= null && debugloggers.existsLogger(id))
	    {
	    	debugloggers.getLogger(id).put(person, String.valueOf(step.getDecision()));
	    }
	    
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
        
  	    //Debug-Logger schreiben falls aktiviert
  	    if(debugloggers!= null && debugloggers.existsLogger(id))
  	    {
  	    	debugloggers.getLogger(id).put(person, String.valueOf(chosenTime));
  	    }
  	    
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
    	    
    	    //Debug-Logger schreiben falls aktiviert
    	    if(debugloggers!= null && debugloggers.existsLogger(id))
    	    {
    	    	debugloggers.getLogger(id).put(currentActivity, String.valueOf(step.getAlternativeChosen()));
    	    }

    	    // Eigenschaft abspeichern
    	    if (Configuration.coordinated_modelling)
    	    {
    	    	currentActivity.addAttributetoMap("standarddauer",(step.getAlternativeChosen().equals("yes") ? 1.0d : 0.0d));
    	    }
    	    else
    	    {
      	    // Bei unkoordinierter Modellierung ohne Stabilitätsaspekte wird der Wert immer mit 0 überschrieben!
     	     currentActivity.addAttributetoMap("standarddauer", 0.0d);    	    	
    	    }
        }
      }
    }
	}


	/**
	 * 
	 * @param id_dc
	 * @param id_mc
	 * @throws InvalidPatternException
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
	    	    if (currentActivity.getAttributesMap().get("standarddauer") == 1.0d)
	    	    {
	    	    	// Ermittle die Standard-Zeitkategorie für den Tag und den Zweck
	    	      int timeCategory = currentActivity.calculateMeanTimeCategory();
	    	      	
	    	      // untere Grenze kann minimal 0 werden
	    	      int from = Math.max(timeCategory - 1,0);
	    	      // obere Grenze kann maximal in letzter Zeitklasse liegen
	    	      int to = Math.min(timeCategory + 1,Configuration.NUMBER_OF_ACT_DURATION_CLASSES-1);
	    	        
	    	      step_dc.limitUpperandLowerBound(from, to);
	    	      // add utility bonus of 10% to average time class (middle of the 3 selected)
	    	      step_dc.adaptUtilityFactor(timeCategory, 1.1);
	    	    } 	    
	    	    
	    	    // Grenzen aufgrund ggf. bereits festgelgten Dauern beschränken
	    	    int[] durationBounds = calculateDurationBoundsDueToOtherActivities(currentActivity);   
	    	    int loc_lowerbound = getDurationTimeClassforExactDuration(durationBounds[0]);
	    	    int loc_upperbound = getDurationTimeClassforExactDuration(durationBounds[1]);
	    	    
	    	    // Sicherstellen, dass die unter Grenze nicht über der oberen Grenze liegt
	    	    assert loc_lowerbound<=loc_upperbound;

	    	    // Beide Grenzen sind gleich, das heißt Dauer ist gesetzt
	    	    if (loc_lowerbound==loc_upperbound)
	    	    {
	    	    	step_dc.limitUpperandLowerBound(loc_lowerbound, loc_upperbound);
	    	    }
	    	    // Beide Grenzen sind NICHT gleich
	    	    else
	    	    {   	    
		    	    // Limitiere die obere Grenze, falls diese kleiner ist als die bisherige oder nicht gesetzt ist
		    	    if (loc_upperbound <= step_dc.getUpperBound() || step_dc.getUpperBound()==-1) step_dc.limitUpperBoundOnly(loc_upperbound); 
	    	    
		    	    // Limitiere die untere Grenze, falls diese größer ist als die bisherige untere Grenze
		    	    if (loc_lowerbound >= step_dc.getLowerBound()) step_dc.limitLowerBoundOnly(loc_lowerbound);   
		    	    
		    	    // Limitiere die unter Grenze, falls diese jetzt höher ist als die obere Grenze
		    	    if (step_dc.getLowerBound() >= step_dc.getUpperBound()) step_dc.limitLowerBoundOnly(step_dc.getUpperBound());   
	    	    }

	    	    		
	    	    // Sicherstellen, dass die unter Grenze nicht über der oberen Grenze liegt
	    	    assert step_dc.getLowerBound()<=step_dc.getUpperBound();
   	    		
	    	    // Wahlentscheidung durchführen
	    	    step_dc.doStep();

	    	    //Debug-Logger schreiben falls aktiviert
	    	    if(debugloggers!= null && debugloggers.existsLogger(id_dc))
	    	    {
	    	    	debugloggers.getLogger(id_dc).put(currentActivity, String.valueOf(step_dc.getDecision()));
	    	    }
	
	    	    // Entscheidungsindex abspeichern
	    	    currentActivity.addAttributetoMap("actdurcat_index",(double) step_dc.getDecision()); 	
	    	    
	    	    /*
	    	     * 
	    	     * MC-Schritt (8C, 8E)
	    	     * 
	    	     */
          	// Objekt basierend auf der gewählten Zeitkategorie initialisieren
			      double chosenTimeCategory = currentActivity.getAttributesMap().get("actdurcat_index");
			      DefaultMCModelStep step_mc = new DefaultMCModelStep(id_mc + (int) chosenTimeCategory, this);
			      step_mc.setModifiedDTDtoUse(currentActivity.getType(), (int) chosenTimeCategory);
			      
			      // angepasste Zeitverteilungen aus vorherigen Entscheidungen werden nur im koordinierten Fall verwendet
			      step_mc.setModifyDTDAfterStep(Configuration.coordinated_modelling);
			      step_mc.setDTDTypeToUse(INDICATOR_ACT_DURATIONS);
			      
			      // Limitiere die Grenzen entsprechend der ermittelten Min- und Maxdauern
			      step_mc.setRangeBounds(durationBounds[0], durationBounds[1]);
			      
			      // Wahlentscheidung durchführen
			      step_mc.doStep();
			      
	    	    //Debug-Logger schreiben falls aktiviert
	    	    if(debugloggers!= null && debugloggers.existsLogger(id_mc))
	    	    {
	    	    	debugloggers.getLogger(id_mc).put(currentActivity, String.valueOf(step_mc.getChosenTime()));
	    	    }
			     
			      // Speichere Ergebnisse ab
			      currentActivity.setDuration(step_mc.getChosenTime());
			      
			      // Lege mögliche weitere Startzeiten von Aktivitäten fest
			      HActivity.createPossibleStarttimes(currentTour.getActivities());
	        }
				}		
			}
	  }
	}



	/**
	 * 
	 * @param id_dc
	 * @param id_mc
	 * @throws InvalidPatternException
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
	    	   
	    	    // Grenzen aufgrund ggf. bereits festgelgten Dauern beschränken
	    	    int[] durationBounds = calculateDurationBoundsDueToOtherActivities(currentActivity);   
	    	    int loc_lowerbound = getDurationTimeClassforExactDuration(durationBounds[0]);
	    	    int loc_upperbound = getDurationTimeClassforExactDuration(durationBounds[1]);
	    	    
	    	    // Sicherstellen, dass die unter Grenze nicht über der oberen Grenze liegt
	    	    assert loc_lowerbound<=loc_upperbound;

	    	    // Beide Grenzen sind gleich, das heißt Dauer ist gesetzt
	    	    if (loc_lowerbound==loc_upperbound)
	    	    {
	    	    	step_dc.limitUpperandLowerBound(loc_lowerbound, loc_upperbound);
	    	    }
	    	    // Beide Grenzen sind NICHT gleich
	    	    else
	    	    {   	    
		    	    // Limitiere die obere Grenze, falls diese kleiner ist als die bisherige oder nicht gesetzt ist
		    	    if (loc_upperbound <= step_dc.getUpperBound() || step_dc.getUpperBound()==-1) step_dc.limitUpperBoundOnly(loc_upperbound); 
	    	    
		    	    // Limitiere die untere Grenze, falls diese größer ist als die bisherige untere Grenze
		    	    if (loc_lowerbound >= step_dc.getLowerBound()) step_dc.limitLowerBoundOnly(loc_lowerbound);   
		    	    
		    	    // Limitiere die unter Grenze, falls diese jetzt höher ist als die obere Grenze
		    	    if (step_dc.getLowerBound() >= step_dc.getUpperBound()) step_dc.limitLowerBoundOnly(step_dc.getUpperBound());   
	    	    }
	    	    		
	    	    // Sicherstellen, dass die unter Grenze nicht über der oberen Grenze liegt
	    	    assert step_dc.getLowerBound()<=step_dc.getUpperBound();

	    	    // Wahlentscheidung durchführen
	    	    step_dc.doStep();
	    	    
	    	    //Debug-Logger schreiben falls aktiviert
	    	    if(debugloggers!= null && debugloggers.existsLogger(id_dc))
	    	    {
	    	    	debugloggers.getLogger(id_dc).put(currentActivity, String.valueOf(step_dc.getDecision()));
	    	    }
	
	    	    // Entscheidungsindex abspeichern
	    	    currentActivity.addAttributetoMap("actdurcat_index",(double) step_dc.getDecision()); 	
	    	    
	    	    /*
	    	     * 
	    	     * MC-Schritt
	    	     * 
	    	     */
          	// Objekt basierend auf der gewählten Zeitkategorie initialisieren
          	double chosenTimeCategory = currentActivity.getAttributesMap().get("actdurcat_index");
  		      DefaultMCModelStep step_mc = new DefaultMCModelStep(id_mc + (int) chosenTimeCategory, this);
  		      step_mc.setModifiedDTDtoUse(currentActivity.getType(), (int) chosenTimeCategory);
  		      
  		      // angepasste Zeitverteilungen aus vorherigen Entscheidungen werden nur im koordinierten Fall verwendet
  		      step_mc.setModifyDTDAfterStep(Configuration.coordinated_modelling);
  		      step_mc.setDTDTypeToUse(INDICATOR_ACT_DURATIONS);

			      // Limitiere die Grenzen entsprechend der ermittelten Min- und Maxdauern
			      step_mc.setRangeBounds(durationBounds[0], durationBounds[1]);
			      
			      // Wahlentscheidung durchführen
  		      step_mc.doStep();
  		      
	    	    //Debug-Logger schreiben falls aktiviert
	    	    if(debugloggers!= null && debugloggers.existsLogger(id_mc))
	    	    {
	    	    	debugloggers.getLogger(id_mc).put(currentActivity, String.valueOf(step_mc.getChosenTime()));
	    	    }
  		     
  		      // Speichere Ergebnisse ab
  		      currentActivity.setDuration(step_mc.getChosenTime());
  		      
			      // Lege mögliche weitere Startzeiten von Aktivitäten fest
			      HActivity.createPossibleStarttimes(currentTour.getActivities()); 
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
    // Step 9A: standard start time category for fist tours during the week
    	
    if (person.isPersonWorkorSchoolCommuterAndMainToursAreScheduled())
    {
    	 // AttributeLookup erzeugen
  		AttributeLookup lookup = new AttributeLookup(person);   	
    	
	    // Step-Objekt erzeugen
	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
	    step.doStep();
	    
	    //Debug-Logger schreiben falls aktiviert
	    if(debugloggers!= null && debugloggers.existsLogger(id))
	    {
	    	debugloggers.getLogger(id).put(person, String.valueOf(step.getDecision()));
	    }

	    // Eigenschaft abspeichern
	    person.addAttributetoMap("first_tour_default_start_cat",(double) step.getDecision());
	   }
	}
	
	

	/**
	 * 
	 * @param id
	 */
	private void executeStep10A(String id)
	{
	  // Step 10a: check if first tour is work/edu lies within standard start time (applies only to work/edu persons)
	  if (person.isPersonWorkorSchoolCommuterAndMainToursAreScheduled())
	  {
	    for (HDay currentDay : pattern.getDays())
	    {
	      if (currentDay.isHomeDay())
	      {
	      	continue;
	      }
	      
	      // Bestimme erste Tour des Tages und deren Tourtyp
	      HTour currentTour = currentDay.getFirstTourOfDay();
	    	char tourtype = currentTour.getActivity(0).getType();
	      if (tourtype == 'W' || tourtype == 'E')
	      {
	      	// AttributeLookup erzeugen
	    		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
	      	
	  	    // Step-Objekt erzeugen
	  	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
	  	    step.doStep();
	  	    
	  	    //Debug-Logger schreiben falls aktiviert
	  	    if(debugloggers!= null && debugloggers.existsLogger(id))
	  	    {
	  	    	debugloggers.getLogger(id).put(currentTour, String.valueOf(step.getAlternativeChosen()));
	  	    }
	
	  	    // Eigenschaft abspeichern
	  	    currentTour.addAttributetoMap("default_start_cat_yes",(step.getAlternativeChosen().equals("yes") ? 1.0d : 0.0d));
	      }
	    }
	  }
	}



	/**
	 * 
	 * Legt die Startzeiten für Touren fest bei denen es bereits festgelegte Startzeiten für Aktivitäten gibt, 
	 * bspw. durch bereits festgelegte gemeinsame Aktivitäten von anderen Personen
	 * @throws InvalidPatternException 
	 * 
	 */
	private void createTourStartTimesDueToScheduledActivities() throws InvalidPatternException
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
		  		
		  		// Prüfe, ob es eine Aktivität in der Tour gibt, deren Startzeit bereits festgelegt wurde (bspw. durch gemeinsame Aktivitäten)
		  		int startTimeDueToScheduledActivities=99999;
		  		
	  			int tripdurations=0;
	  			int activitydurations=0;
	  			
		  		HActivity.sortActivityListbyIndices(currentTour.getActivities());
		  		for (HActivity tmpact : currentTour.getActivities())
		  		{
		  			/*
		  			 *  Wenn die Startzeit der Aktivität festgelegt ist, rechne von dem Punkt aus 
		  			 *  rückwärts und ziehe alle Dauern bisheriger Wege und Aktivitäten in der Tour ab
		  			 */		  			
		  			if (tmpact.startTimeisScheduled())
		  			{
		  				startTimeDueToScheduledActivities= tmpact.getTripStartTimeBeforeActivity() - tripdurations - activitydurations;
		  				break;
		  			}
		  			/*
		  			 * Andernfalls addiere die Tour und Aktivitätszeit auf
		  			 */
		  			else
		  			{
		  				tripdurations += tmpact.getEstimatedTripTimeBeforeActivity();
		  				activitydurations += tmpact.getDuration();
		  			}
		  		}
		  		
		  		/*
		  		 * Durch bereits festgelegte gemeinsame Aktivitäten kann es vorkommen, dass negative Tourstartzeiten entstehen.
		  		 * Bsp: Die Aktivität stammt von einer anderen Person und ist sehr nahe an 0 Uhr. Falls die aktuelle Personen einen 
		  		 * längeren Default-Pendelweg hat, kann dadurch der Startzeitpunkt der Tour unter 0 Uhr fallen!
		  		 */
		  		if (startTimeDueToScheduledActivities<0)
		  		{
		  			throw new InvalidPatternException("Person", pattern, "TourStartTimes <0 " + currentTour);
		  		}
		  		
		  		// Lege Startzeit fest falls durch bereits festgelegte Aktivitäten bestimmt 
		  		if (startTimeDueToScheduledActivities!=99999)
		  		{
		  			// Startzeit der Tour festlegen
		  			currentTour.setStartTime(startTimeDueToScheduledActivities);   
		  			// Setze die Startzeiten der Aktivitäten in dieser Tour
		  			currentTour.createStartTimesforActivities();
		  		}
		    }
	    }
	  }
	}



	/**
	 * 
	 * @param id
	 * @param tournrdestages
	 * @throws InvalidPersonPatternException
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
		    step_dc.limitUpperandLowerBound(lowerbound, upperbound);
		    
		    if (Configuration.coordinated_modelling)
		    {
			    if (currentTour.existsAttributeinMap("default_start_cat_yes") && currentTour.getAttributefromMap("default_start_cat_yes")==1.0d)
			    {
			    	int defaultcat = (int) person.getAttributefromMap("first_tour_default_start_cat");
			    	if (defaultcat>= lowerbound && defaultcat <= upperbound) step_dc.limitUpperandLowerBound(defaultcat, defaultcat);
			    }
		    }
		    
		    // Führe Entscheidungswahl durch
		    step_dc.doStep();
		    
  	    //Debug-Logger schreiben falls aktiviert
  	    if(debugloggers!= null && debugloggers.existsLogger(id_dc))
  	    {
  	    	debugloggers.getLogger(id_dc).put(currentTour, String.valueOf(step_dc.getDecision()));
  	    }
	
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
	      int chosenStartTime = step_mc.getChosenTime();
	      
  	    //Debug-Logger schreiben falls aktiviert
  	    if(debugloggers!= null && debugloggers.existsLogger(id_mc))
  	    {
  	    	debugloggers.getLogger(id_mc).put(currentTour, String.valueOf(chosenStartTime));
  	    }
	      
	      // Speichere Ergebnisse ab
	      currentTour.setStartTime(chosenStartTime);   	  	
	      
	      // Setze die Startzeiten der Aktivitäten in dieser Tour
	      currentTour.createStartTimesforActivities();
	      
//TODO previousTour muss immer scheduled sein bei chronologier Modellierungsreihenfolge	      
	      // Stelle sicher, dass sich die Touren nicht überlappen!
	      HTour previousTour = currentTour.getPreviousTourinPattern();
	      if (previousTour!=null && previousTour.isScheduled()) assert currentTour.getStartTimeWeekContext() > previousTour.getEndTimeWeekContext() : "Tours are overlapping!";
		  }	       
	  }
	}



	/**
	 * 
	 * @throws InvalidPersonPatternException
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
	    	    dcstep.limitUpperandLowerBound(lowerbound, upperbound);
	    	    
	    	    // Führe Entscheidungswahl durch
	    	    dcstep.doStep();
	    	    
	    	    //Debug-Logger schreiben falls aktiviert
	    	    if(debugloggers!= null && debugloggers.existsLogger("10S"))
	    	    {
	    	    	debugloggers.getLogger("10S").put(currentTour, String.valueOf(dcstep.getDecision()));
	    	    }
	
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
	          
	    	    //Debug-Logger schreiben falls aktiviert
	    	    if(debugloggers!= null && debugloggers.existsLogger("10T"))
	    	    {
	    	    	debugloggers.getLogger("10T").put(currentTour, String.valueOf(mcstep.getChosenTime()));
	    	    }
	          
	          // Speichere Ergebnisse ab
	          int starttimetour = currentDay.getTour(currentTour.getIndex()-1).getEndTime() + mcstep.getChosenTime();
	          currentTour.setStartTime(starttimetour);
	          
	          // Setze die Startzeiten der Aktivitäten in dieser Tour
	          currentTour.createStartTimesforActivities();
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
	    	    
      	    // DebugLogger schreiben falls aktiviert
      	    if(debugloggers!= null && debugloggers.existsLogger(id))
      	    {
      	    	debugloggers.getLogger(id).put(currentActivity, String.valueOf(step.getAlternativeChosen()));
      	    }
	
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
	  
    // DebugLogger schreiben falls aktiviert
    if(debugloggers!= null && debugloggers.existsLogger(id))
    {
    	debugloggers.getLogger(id).put(activity, String.valueOf(step.getAlternativeChosen()));
    }
	
	  // Speichere gewählte Entscheidung für weitere Verwendung
	  int chosenActivityType = Integer.parseInt(step.getAlternativeChosen());
	  
	  // Aktivitätstyp festlegen
	  activity.setMobiToppActType((byte) chosenActivityType);          
	}

   

	
	/**
	 * 
	 * Bestimmt die Obergrenze und Untergrenze für die Aktivitätendauern auf Basis bereits geplanter Aktivitäten.
	 * 
	 * @param act
	 * @return [0] = Untergrenze [1] = Obergrenze
	 * @throws InvalidPatternException
	 */
	private int[] calculateDurationBoundsDueToOtherActivities(HActivity act) throws InvalidPatternException
	{
		HDay dayofact = act.getDay();
		
		/*
		 * Grundidee der Bestimmung der unteren Grenze
		 * 
		 * 1. Ausgangspunkt (in absteigender Priorität)
		 * - Es gibt bereits eine vorhergehende Aktivität mit festgelegter Startzeit am Tag
		 * - Die letzte Aktivität des Vortags ragt in den aktuellen Tag hinein
		 * - Anfang des Tages (1 Minute Puffer für Heimzeiten)
		 * 
		 * 2. Ermittel alle Aktivitätendauern zwischen Tagesanfang / letzter Aktivität und der aktuellen Akt
		 * 3. Ermittel alle Wegdauern zwischen Tagesanfang / letzter Aktivität und der aktuellen Akt
		 * 4. Ermittel Puffer für Heimzeiten für alle Touren zwischen Tagesanfang / Tour der letzten Aktivität und der aktuellen Akt
		 * 
		 * 
		 * Grundidee der Bestimmung der oberen Grenze
		 * 
		 * 1. Ausgangspunkt (in absteigender Priorität)
		 * - Es gibt bereits eine nachfolgende Aktivität mit festgelegter Startzeit am Tag
		 * - Ende des Tages
		 * 
		 * Schritte 2-4 analog
		 */
		
		
		/*
		 * 1.
		 * 
		 * Ermittel die Ausgangspunkte
		 * 
		 */
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
		
		/*
		 * Bestimme Ausgangspunkt der unteren Grenze
		 */
		
		int ausgangspunktunteregrenze=1;
		if (last_act_scheduled!=null)
		{
			ausgangspunktunteregrenze = last_act_scheduled.getStartTime() + (last_act_scheduled.durationisScheduled() ?  last_act_scheduled.getDuration() : Configuration.FIXED_ACTIVITY_TIME_ESTIMATOR); 
		}
		else
		{
			// Prüfe, ob letzte Akt des Vortages in den aktuellen Tag ragt!
			HDay vortag = dayofact.getPreviousDay();
			if (vortag!=null && !vortag.isHomeDay())
			{
				HActivity letzteaktvortag = vortag.getLastTourOfDay().getLastActivityInTour();
				if (letzteaktvortag.startTimeisScheduled())
				{
					int endeletzteaktvortag = letzteaktvortag.getStartTime() +
							(letzteaktvortag.durationisScheduled() ? letzteaktvortag.getDuration() : 0) + 
							(letzteaktvortag.tripAfterActivityisScheduled() ? letzteaktvortag.getEstimatedTripTimeAfterActivity() : 0);
					if (endeletzteaktvortag>1440) 
					{
						// +1 für Heimaktivität nach Ende der letzten Tour des Vortages
						ausgangspunktunteregrenze = endeletzteaktvortag-1440+1;
					}
				}
			}
		}
	
		/*
		 * Bestimme Ausgangspunkt der oberen Grenze
		 */	

		int ausgangspunktoberegrenze=0;
		// Falls nächste Aktivität bereits bestimmt ist, verwende diese als Richtgröße
		if (next_act_scheduled!=null)
		{
			ausgangspunktoberegrenze = next_act_scheduled.getStartTime(); 
		}
		// Andernfalls ist 3 Uhr nachts die Obergrenze, es sei denn in dem Zeitraum bis 3 Uhr des nächsten Tages ist bereits eine Akt geplant. 
		// Dann ist der Startzeitpunkt dieser Aktivität entsprechend der Ausgangspunkt
		else
		{
			ausgangspunktoberegrenze = 1620;
			
			// Prüfe, ob erste Akt des Folgetages im Zeitraum bis 3 Uhr liegt!
			HDay folgetag = dayofact.getNextDay();
			if (folgetag!=null && !folgetag.isHomeDay())
			{
				HActivity ersteaktfolgetag = folgetag.getFirstTourOfDay().getFirstActivityInTour();
				if (ersteaktfolgetag.startTimeisScheduled())
				{
					int startersteaktfolgetag = ersteaktfolgetag.getTripStartTimeBeforeActivity();
					if (startersteaktfolgetag<180) 
					{
						// -1 für Heimaktivität vor der ersten Akt des Folgetags
						ausgangspunktoberegrenze = 1440 + startersteaktfolgetag -1;
					}
				}
			}
	
		}
	
		
		/*
		 * 2.
		 * 
		 * Ermittel die Aktivitätendauern
		 * 
		 */
		
		int activitydurationsincelastscheduled = countActivityDurationsbetweenActivitiesofOneDay(last_act_scheduled, act);
		int activitydurationuntilnextscheduled = countActivityDurationsbetweenActivitiesofOneDay(act, next_act_scheduled);
		
		/*
		 * 3.
		 * 
		 * Ermittel die Wegdauern
		 * 
		 */
		
		int tripdurationssincelastscheduled = countTripDurationsbetweenActivitiesofOneDay(last_act_scheduled, act);
		int tripdurationsuntilnextscheduled = countTripDurationsbetweenActivitiesofOneDay(act, next_act_scheduled);
	
		/*
		 * 4.
		 * 
		 * Ermittel die Heimzeitpuffer
		 * Berücksichtige jeweils 1 Minute pro Heimakt als Mindestpuffer
		 * 
		 */

		/*
		 * Vorher
		 */
		int timeforhomeactsincelastscheduled=0;		
	  if (last_act_scheduled==null)
		{
			// Zähle wieviele Touren vor der aktuelle Tour liegen
	  	timeforhomeactsincelastscheduled += (act.getTour().getIndex() - act.getDay().getLowestTourIndex());
		}
		else
		{
			// Zähle wieviele Touren zwischen der der letzten festgelegten und der aktuellen liegen
			timeforhomeactsincelastscheduled += (act.getTour().getIndex() - last_act_scheduled.getTour().getIndex());
		}
	  
	  /*
	   * Nachher
	   */
		int timeforhomeactuntilnextscheduled=0;
	  if (next_act_scheduled==null)
		{
			// Zähle wieviele Touren nach der aktuellen Tour noch kommen
	  	timeforhomeactuntilnextscheduled += (act.getDay().getHighestTourIndex() - act.getTour().getIndex());
		}
		else
		{
			// Zähle wieviele Touren zwischen der der nächsten festgelegten und der aktuellen liegen
			timeforhomeactuntilnextscheduled += (next_act_scheduled.getTour().getIndex() - act.getTour().getIndex());
		}
		
		/*
		 * 5.
		 * 
		 * Bestimme Schranken und maximale Dauern
		 * 
		 */	
		
		// Bestimme obere und untere Schranken
		int lowerbound = ausgangspunktunteregrenze + activitydurationsincelastscheduled + tripdurationssincelastscheduled + timeforhomeactsincelastscheduled;
		int upperbound = ausgangspunktoberegrenze - activitydurationuntilnextscheduled - tripdurationsuntilnextscheduled - timeforhomeactuntilnextscheduled;
		
		/*
		 * Falls Aktivität selbst schon eine festgelegte Startzeit hat, wird dadurch die untere Grenze bestimmt -> ersetze lowerbound
		 */
		if (act.startTimeisScheduled()) lowerbound = act.getStartTime();
		
		int maxduration = upperbound - lowerbound;
		int minduration = 1;
		
    // Limitiere maximaleDauer auf 1 Tag falls mehr als 1 Tag!
    maxduration = Math.min(maxduration,1440);
       
    // Fehlerbehandlung, falls UpperBound kleinergleich LowerBound
    if (upperbound<=lowerbound)
    {
    	// Household Exception, da Konflikt aufgrund von gemeinsamen Akt anderer Personen im Haushalt entstanden ist
    	if (next_act_scheduled!= null && next_act_scheduled.getCreatorPersonIndex()!=person.getPersIndex() && 
    			last_act_scheduled!=null && last_act_scheduled.getCreatorPersonIndex()!=person.getPersIndex())
    	{
    		String errorMsg = "Duration Bounds incompatible Act" + act.getDayIndex() + "/" + act.getTour().getIndex() + "/" + act.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
    		throw new InvalidPatternException("Household",pattern, errorMsg);
    	}
    	// Person Exception, da Konflikt bei der Modellierung der Person selbst entstanden ist
    	else
    	{
    		String errorMsg = "Duration Bounds incompatible Act " + act.getDayIndex() + "/" + act.getTour().getIndex() + "/" + act.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
    		throw new InvalidPatternException("Person",pattern, errorMsg);
    	}
    }
    
    /*
     * Prüfen, ob Aktivität innerhalb einer Tour liegt und vorhergehende und nachfolgende Aktivität bereits bzgl. der
     * Startzeit determiniert sind. Falls ja, ist untere Grenze = obere Grenze
     */
    if(!act.isActivityFirstinTour() && !act.isActivityLastinTour())
    {
    	HActivity letzteakt = act.getPreviousActivityinTour();
    	HActivity naechsteakt = act.getNextActivityinTour();
    	
    	if (letzteakt.startTimeisScheduled() && letzteakt.durationisScheduled() && naechsteakt.startTimeisScheduled())
    	{
    		minduration = maxduration;
    	}
    }
    /*
     * Prüfen, ob Aktivität und die nachfolgende bereits eine Startzeit haben.
     * Dann gilt ebenfalls untere Grenze = obere Grenze
     */
    if (act.startTimeisScheduled() && !act.isActivityLastinTour())
    {
    	if (act.getNextActivityinTour().startTimeisScheduled()) minduration=maxduration;
    }
    
    
    /*
     * Rückgabe der Grenzen für die Dauer
     */
		
    int[] durationBounds = new int[2];
    durationBounds[0] = minduration;
    durationBounds[1] = maxduration;
    
		return durationBounds;
	}
	
	
	/**
	 * 
	 * Bestimmt anhands eines exakten Wertes die entsprechende Zeitklasse
	 * 
	 * @param maxduration
	 * @return
	 */
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
			
		/*
		 * Grundidee der Bestimmung der unteren Grenze
		 * 
		 * 1. Ausgangspunkt (in absteigender Priorität)
		 * - Die Tour ist nicht die erste Tour des Tages -> Es gibt bereits die Endezeit der vorhergehenden Tour + 1
		 * - Die letzte Aktivität des Vortags ragt in den aktuellen Tag hinein
		 * - Anfang des Tages
		 * 
		 * 
		 * Grundidee der Bestimmung der oberen Grenze
		 * 
		 * 1. Ausgangspunkt (in absteigender Priorität)
		 * - Prüfe, ob es bereits eine weitere geplante Anfangszeit einer Tour im Tagesverlauf gibt
		 * - Prüfe, ob es am nächsten Tag bis 3 Uhr morgens schon eine geplante Aktivität gibt
		 * - 3 Uhr Nachts des Folgetages als spätestens Ende der Tour = 1620
		 * 
		 * 2. Alle noch geplanten Touren inkl. aller Aktivitäts- und Wegzeiten abziehen zwischen Tagesende bzw. nächster geplanter Tour
		 * 3. Puffer für Heimaktivitäten zwischen den Touren
		 * 
		 */
		  
	  HDay tourday = tour.getDay();
	 
	  int lowercat = -1;
	  int uppercat = -1; 
	  
		/*
		 * 
		 * untere Grenze
		 * 
		 */

	  int basisunteregrenze = 1;
	 
	  
	  // Falls es sich nicht um die erste Tour des Tages handelt, wird lowerbound durch das Ende der vorhergehenden Tour bestimmt
	  if (tour.getIndex() != tourday.getLowestTourIndex())
	  {
	  	basisunteregrenze = tourday.getTour(tour.getIndex()-1).getEndTime() + 1;
	  }
	  // Ansonsten prüfe, ob letzte Aktivität des Vortags noch in den aktuellen Tag ragt
	  else
	  {
			// Prüfe, ob letzte Akt des Vortages in den aktuellen Tag ragt!
			HDay vortag = tourday.getPreviousDay();
			if (vortag!=null && !vortag.isHomeDay())
			{
				HActivity letzteaktvortag = vortag.getLastTourOfDay().getLastActivityInTour();
				if (letzteaktvortag.startTimeisScheduled())
				{
					int endeletzteaktvortag = letzteaktvortag.getStartTime() +
							(letzteaktvortag.durationisScheduled() ? letzteaktvortag.getDuration() : 0) + 
							(letzteaktvortag.tripAfterActivityisScheduled() ? letzteaktvortag.getEstimatedTripTimeAfterActivity() : 0);
					if (endeletzteaktvortag>1440) 
					{
						// +1 um noch eine Heimaktivität nach der letzten Tour des Vortags zu ermöglichen
						basisunteregrenze = endeletzteaktvortag-1440+1;
					}
				}
			}
	  }
	  

		/*
		 * 
		 * obere Grenze
		 * 
		 */

	  /*
	   * 1. Ausgangspunkt
	   */
	  int basisoberegrenze = 1620;
	  HTour nexttourscheduled=null;
	  
	  //Prüfe, ob es im Tagesverlauf noch weitere geplante Touren gibt
	  for (int i = tour.getIndex()+1; i <= tourday.getHighestTourIndex(); i++)
	  {
	  	HTour tmptour = tourday.getTour(i);
	  	// Sobald eine bereits geplante Tour gefunden wurde wird von diesem Punkt ausgegangen die obere Grenze berechnet
	  	if (tmptour.isScheduled())
	  	{
	  		nexttourscheduled=tmptour;
	  		basisoberegrenze = tmptour.getStartTime();
	  		break;
	  	}
	  }
	  // Prüfe, ob am Folgetag bis 3 Uhr nachts bereits die erste Aktivität geplant ist, falls keine weitere geplante Tour an diesem Tag
	  if (nexttourscheduled==null)
	  {
	  	HDay folgetag = tourday.getNextDay();
	  	if (folgetag!=null && !folgetag.isHomeDay())
			{
				HActivity ersteaktfolgetag = folgetag.getFirstTourOfDay().getFirstActivityInTour();
				if (ersteaktfolgetag.startTimeisScheduled())
				{
					int startersteaktfolgetag = ersteaktfolgetag.getStartTime() -
							(ersteaktfolgetag.tripBeforeActivityisScheduled() ? ersteaktfolgetag.getEstimatedTripTimeBeforeActivity() : 0);
					if (startersteaktfolgetag<180) 
					{
						basisoberegrenze = 1439 + startersteaktfolgetag;
					}
				}
			}	  						
	  }
	  
	  
	  /*
	   * 2. Aktivitäts- und Wegzeiten bis Tagesende / nächster geplanter Tour
	   * 3. Heimzeitpuffer
	   */
	  int tmptourdurations = 0;
	  int heimzeitpuffer = 0;
	  int tourindexfuersuche;
	  // Bestimme, bis zu welcher Tour die Dauern gezählt werden
	  if(nexttourscheduled!=null)
	  {
	  	// Falls nächste Tour bekannt ist, werden alle Touren bis dahin gezählt
	  	tourindexfuersuche = nexttourscheduled.getIndex()-1;
	  }
	  else
	  {
	  	// Falls nächste Tour nicht bekannt ist, werden alle restlichen Touren des Tages gezählt
	  	tourindexfuersuche = tourday.getHighestTourIndex();
	  }
	  for (int i = tour.getIndex(); i <= tourindexfuersuche; i++)
	  {
	  	HTour tmptour = tourday.getTour(i);
	  	tmptourdurations += tmptour.getTourDuration();
	  	
	  	heimzeitpuffer += 1;
	  }
	  
	  
	  /*
	   * 
	   * Grenzen bestimmen und falls notwendig Kategorien bilden
	   * 
	   */
	  
	  int lowerbound = basisunteregrenze;
	  int upperbound = basisoberegrenze - tmptourdurations - heimzeitpuffer;
	  
	  // UpperBound falls notwendig auf 1439 kürzen, da keine späteren Anfangszeiten  möglich
	  if (upperbound>1439) upperbound=1439;
	  
	        
	  // Fehlerbehandlung, falls UpperBound kleiner ist als LowerBound
	  if (upperbound<lowerbound)
	  {
	  	String errorMsg = "TourStartTimes Tour " + tourday.getIndex() + "/" + tour.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	  	throw new InvalidPatternException("Person",pattern, errorMsg);
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
	    	throw new InvalidPatternException("Person",pattern, errorMsg);
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
	 * Bestimme die Grenzen für die Dauer der Heimzeit
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
		
		// lowerbound startet mit 1 - upperbound mit -1 (wird berechnet)
	  int lowerbound = 1;
	  int upperbound = -1;
	  
	  int lowercat = -1;
	  int uppercat = -1;   
	  
	  int starttime_nexttourscheduled = 1620;
	  	  
	  // Bestimme obere Grenze basierend auf bereits festgelegten Startzeitpunkten der im weiteren Tagesverlauf folgenden Touren
	  int tmptourdurations = 0;
	  for (int i = tour.getIndex(); i <= tourday.getHighestTourIndex(); i++)
	  {
	  	HTour tmptour = tourday.getTour(i);
	  	
	  	// Sobald eine bereits geplante Tour gefunden wurde wird von diesem Punkt ausgegangen die obere Grenze berechnet
	  	if (tmptour.isScheduled())
	  	{
	  		starttime_nexttourscheduled = tmptour.getStartTime();
	  		break;
	  	}
	  	// Sollte die Tour noch nicht verplant sein wird die Dauer der Tour in die Grenzenberechnung mit einbezogen
	  	else
	  	{
	  		// +1 um jeweils nach der Tour noch eine Heimaktivität von min. einer Minute zu ermöglichen
	  		tmptourdurations += tmptour.getTourDuration() + 1;
	  	}
	  }
	  // Falls keine weitere Tour geplant ist, prüfe, ob bis 3 Uhr am Folgetag eine Tour startet
	  if (starttime_nexttourscheduled==1620)
	  {
	  	HDay folgetag = tourday.getNextDay();
	  	if (folgetag!=null && !folgetag.isHomeDay())
			{
				HActivity ersteaktfolgetag = folgetag.getFirstTourOfDay().getFirstActivityInTour();
				if (ersteaktfolgetag.startTimeisScheduled())
				{
					int startersteaktfolgetag = ersteaktfolgetag.getStartTime() -
							(ersteaktfolgetag.tripBeforeActivityisScheduled() ? ersteaktfolgetag.getEstimatedTripTimeBeforeActivity() : 0);
					if (startersteaktfolgetag<180) 
					{
						starttime_nexttourscheduled = 1439 + startersteaktfolgetag;
					}
				}
			}	 
	  }
	  
	  // Maximaldauer berechnet sich aus verbleibendem Zeitpuffer zwischen Ende der vorhergehenden Tour und dem Endzeitpunkt des Tages - verbleibende Tour/Wegzeiten 
	  upperbound = starttime_nexttourscheduled - tmptourdurations - tourday.getTour(tour.getIndex()-1).getEndTime();
	  if (upperbound>1439) upperbound=1439;
	  
	  assert upperbound!=-1 : "Konnte UpperBound nicht bestimmen!";
	        
	  // Fehlerbehandlung, falls UpperBound kleiner ist als LowerBound
	  if (upperbound<lowerbound)
	  {
	  	String errorMsg = "HomeTime Tour " + tour.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	  	throw new InvalidPatternException("Person", pattern, errorMsg);
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
	    	throw new InvalidPatternException("Person",pattern, errorMsg);
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
   * Methode erzeugt Home-Aktivitäten zwischen den Touren
   * 
   * @param allmodeledActivities
   * @throws InvalidPersonPatternException
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
    			HTour acttour = act.getTour();
    			HTour nexttour =  allmodeledActivities.get(i+1).getTour();
    			
    			int ende_tour = acttour.getEndTimeWeekContext();
    			int start_next_tour = nexttour.getStartTimeWeekContext();
    			
    			// Bestimme Puffer
    			int duration2 = start_next_tour - ende_tour;
   			
    			assert duration2>0 : "Fehler - keine Home-Aktivität nach Ende der Tour möglich! - " + start_next_tour + " // " + ende_tour;
    			// Bestimme zugehörigen Tag zu der Heimaktivität
    			int day = (int) ende_tour/1440;
    			int startzeit = ende_tour%1440;
    			// Sonderbehandlung für Heim-Aktivitäten, die nach dem 7. Tag um 0 Uhr liegen. Diese werden dem letzten Tag zugeordnet.
    			if (day==7)
    			{
    				day=6;
    				startzeit = startzeit+1440; 
    			}
    			// Füge Heimaktivität in Liste hinzu
    			//TODO Falsche Zuordnung von Heimaktivitäten, die zwischen 0 und 3 Uhr des Folgetages beginnen! bzw. nach den 7 Tagen stattfinden!
    			if (duration2>0)
    			{
    				pattern.addHomeActivity(new HActivity(pattern.getDay(day), homeact, duration2, startzeit));
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
	private void selectWithWhomforJointActions() 
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
					int anzahlweiterepersausverteilung=99;
					double randomvalue = randomgenerator.getRandomValue();
					int hhgro = person.getHousehold().getNumberofPersonsinHousehold();
					
					/*
					 * Wahrscheinlichkeiten für die Anzahl mehrerer Personen stammt aus MOP-Auswertungen
					 */
					if (hhgro==2)
					{
						anzahlweiterepersausverteilung=1;
					}
					if (hhgro==3)
					{
						if (randomvalue <  0.75) anzahlweiterepersausverteilung=1;
						if (randomvalue >= 0.75) anzahlweiterepersausverteilung=2;
					}
					if (hhgro==4)
					{
						if (0 	 <= randomvalue && randomvalue < 0.73) 	anzahlweiterepersausverteilung=1;
						if (0.73 <= randomvalue && randomvalue < 0.89) 	anzahlweiterepersausverteilung=2;
						if (0.89 <= randomvalue && randomvalue <= 1) 		anzahlweiterepersausverteilung=3;
					}
					if (hhgro>=5)
					{
						if (0 	 <= randomvalue && randomvalue < 0.79) 	anzahlweiterepersausverteilung=1;
						if (0.79 <= randomvalue && randomvalue < 0.92) 	anzahlweiterepersausverteilung=2;
						if (0.92 <= randomvalue && randomvalue < 0.95) 	anzahlweiterepersausverteilung=3;
						if (0.95 <= randomvalue && randomvalue <= 1) 		anzahlweiterepersausverteilung=4;
					}
					
					//TODO Verbesserungsmöglichkeit: Die Auswahl gemeinsamer Personen kontextsensitiver gestalten, bspw. immer Vater mit Kind, ...
								
					// Maximale Anzahl wird zusätzlich begrenzt von der Anzahl weitere möglicher Personen, die noch nicht modelliert wurden
					int anzahlweiterepers = Math.min(anzahlweiterepersausverteilung, otherunmodeledpersinhh.size());
					for (int i=1 ; i<= anzahlweiterepers; i++)
					{
						// Wähle eine zufällige Nummer der verbleibenden Personen
						List<Integer> keys = new ArrayList<Integer>(otherunmodeledpersinhh.keySet());
						Integer randomkey = keys.get(randomgenerator.getRandomPersonKey(keys.size()));
						
						// Aktivität zur Berücksichtigung bei anderer Person aufnehmen
						ActitoppPerson otherperson = otherunmodeledpersinhh.get(randomkey);
						otherperson.addJointActivityforConsideration(tmpactivity);
						
						// Andere Person als Teilnehmer bei der Aktivität eintragen
						tmpactivity.addJointParticipant(otherperson);
						
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
