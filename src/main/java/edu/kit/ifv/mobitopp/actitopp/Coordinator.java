package edu.kit.ifv.mobitopp.actitopp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Tim Hilgert
 * 
 * Coordinator-Klasse, die die Erstellung der Wochen-Aktivit�tenpl�ne koordiniert.
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
       
    /*
     * - distributions for wrd (weighted random draw) model steps are personalized.
     * - they are dependent from step id, category and activity type
     * 
     * They are needed to store modified distributions after modeling decisions (e.g. when people decide to perform
     * a 8-hour working acitivity, the distribution element (8 hours) will get a bonus that it is more likely to choose 
     * 8 hours again the next time for this person. This ensures better stability of duration and starttime modeling.		
     */
    private HashMap<String, WRDDiscreteDistribution> personalWRDDistributions;
    
    
    // Important for modeling joint actions
    
   	private int[] numberofactsperday_lowerboundduetojointactions = {0,0,0,0,0,0,0};
  	private int[] numberoftoursperday_lowerboundduetojointactions = {0,0,0,0,0,0,0};
    
    
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
     
      this.personalWRDDistributions = new HashMap<String, WRDDiscreteDistribution>();
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
  	
  	// Durchf�hrung der Modellschritte
  
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

    // Gemeinsame Aktivit�ten
    if (Configuration.model_joint_actions) 
    {
    	placeJointActivitiesIntoPattern();
    }

    executeStep7DC("7A", ActivityType.WORK);
    executeStep7WRD("7B", ActivityType.WORK);
    
    executeStep7DC("7C", ActivityType.EDUCATION);
    executeStep7WRD("7D", ActivityType.EDUCATION);
    
    executeStep7DC("7E", ActivityType.LEISURE);
    executeStep7WRD("7F", ActivityType.LEISURE);
    
    executeStep7DC("7G", ActivityType.SHOPPING);
    executeStep7WRD("7H", ActivityType.SHOPPING);
    
    executeStep7DC("7I", ActivityType.TRANSPORT);
    executeStep7WRD("7J", ActivityType.TRANSPORT);
  
    executeStep8A("8A");
    executeStep8_MainAct("8B", "8C");
    executeStep8_MainAct("8D", "8E");
    executeStep8_NonMainAct("8J", "8K");

    executeStep9A("9A");
    
    executeStep10A("10A");
       
    createTourStartTimesDueToScheduledActivities();
    
    executeStep10("10M","10N", 1);
    executeStep10("10O","10P", 2);

    executeStep10ST();
    
    if (Configuration.model_joint_actions) 
    {
    	executeStep11("11");
  		// Bestimme, welche gemeinsamen Wege/Aktivit�ten mit welchen anderen Personen durchgef�hrt werden sollen
  		selectWithWhomforJointActions();		
    }
    
					 
    // Finalisierung der Wochenaktivit�tenpl�ne 
    
    // 1) Erstelle eine Liste mit allen Aktivit�ten der Woche
    List<HActivity> allModeledActivities = pattern.getAllOutofHomeActivities();    	
    HActivity.sortActivityListbyWeekStartTimes(allModeledActivities);
	
    // 2) Erzeuge Zuhause-Aktivit�ten zwischen den Touren
    createHomeActivities(allModeledActivities);
    
    // 3) Wandel die Aktivit�tenzwecke des Modells zur�ck in mobiTopp-Aktivit�tenzwecke
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
   * Bestimmt untere Grenzen f�r die Anzahl an Touren und Aktivit�ten an jedem Tag
   * Basierend auf der Liste gemeinsamer Aktivit�ten wird vorgegeben, wieviele Touren bzw. Aktivit�ten 
   * an dem jeweiligen Tag mindestens vorhanden sein m�ssen.
   */
  private void determineMinimumTourActivityBounds()
  {
  	
  	/*
  	 * Idee:
  	 * 
  	 * - Bestimme untere Grenzen f�r die Anzahl an Touren und Aktivit�ten an jedem Tag basierend auf der Liste gemeinsamer Aktivit�ten
  	 * 
  	 * - Modelliere Anzahl an Touren und Aktivit�ten (siehe Schritte 1-6)
  	 * 		- Es muss an jedem Tag mit gemeinsamen Aktivit�ten mindestens eine Tour geben!
  	 * 		- Bei mehr als 2 gemeinsamen Aktivit�ten mindestens 2 Touren!
  	 * 		- Es gibt f�r Haupt- und Vorheraktivit�tenzahl keine Mindestanzahlen. Aktualisiere die Zahl der bereits modellierten Aktivit�ten
  	 * 		  und lege die Grenze nur bei den Nachheraktivit�ten fest. Bei mehreren Touren auf jede Tour aufteilen!
  	 * 
  	 * - Nach Schritt 6: Aktivit�ten platzieren
  	 * 
  	 */
  	
  	/*
  	 * Bestimme Mindestzahl an Touren und Aktivit�ten basierend auf den bereits vorhandenen gemeinsamen Aktivit�ten
  	 */
  	  	
  	for (HActivity act : person.getAllJointActivitiesforConsideration())
  	{
  		// Z�hle Anzahl der Aktivit�ten hoch
  		numberofactsperday_lowerboundduetojointactions[act.getDayIndex()] += 1;
  		
  		// Bestimme Mindestzahl an Touren
  		// Bei max. 2 Aktivit�ten nur eine Tour mindestens
  		if (numberofactsperday_lowerboundduetojointactions[act.getDayIndex()] <= 2) 
  		{
  			numberoftoursperday_lowerboundduetojointactions[act.getDayIndex()] = 1;
  		}
  		//TODO ggf. deaktivieren, da dadurch die Anzahl an Touren zu hoch wird!
  		// Bei mehr als 2 Aktivit�ten mindestens zwei Touren
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
    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
    step.doStep();
    
    // Ergebnis zu Personen-Map hinzuf�gen
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
    	// Schritt wird ausgef�hrt, falls die Hauptaktivit�t noch nicht exisitiert oder noch keinen Aktivit�tstyp hat
    	if(!currentDay.existsActivityTypeforActivity(0,0))
    	{
      	// AttributeLookup erzeugen
    		AttributeLookup lookup = new AttributeLookup(person, currentDay);   	
      	
  	    // Step-Objekt erzeugen
  	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
  	    
  	    // Falls es schon Touren gibt (aus gemeinsamen Akt), H als Aktivit�tstyp ausschlie�en
  	    if (currentDay.getAmountOfTours()>0 || numberoftoursperday_lowerboundduetojointactions[currentDay.getIndex()]>0)
  	    {
  	    	step.disableAlternative("H"); 
	    	}
  	    
  	    if (Configuration.coordinated_modelling)
  	    {
	  	    // Entferne die Alternative W, falls bereits Anzahl der Tage mit Arbeitsaktivit�ten erreicht sind!
	  	    if (
	  	    		person.getAttributefromMap("anztage_w") <= pattern.countDaysWithSpecificActivity(ActivityType.WORK) &&
	  	    		currentDay.getTotalNumberOfActivitites(ActivityType.WORK) == 0 &&
	  	    		person.personisAnywayEmployed()
	  	    		)
	  	    {
	  	    	step.disableAlternative("W"); 
	  	    }
	  	    
	  	    // Entferne die Alternative E, falls bereits Anzahl der Tage mit Bildungsaktivit�ten erreicht sind!
	  	    if (
	  	    		person.getAttributefromMap("anztage_e") <= pattern.countDaysWithSpecificActivity(ActivityType.EDUCATION) &&
	  	    		currentDay.getTotalNumberOfActivitites(ActivityType.EDUCATION) == 0 &&
	  	    		person.personisinEducation()
	  	    		)
	  	    {
	  	    	step.disableAlternative("E"); 
	  	    }
	  	    
	  	    // Nutzenbonus f�r Alternative W, falls Person erwerbst�tig und Wochentag
	  	    if (person.personisAnywayEmployed() && currentDay.getWeekday()<6 && step.alternativeisEnabled("W"))
	  	    {
	  	    	step.adaptUtilityFactor("W", 1.3);
	  	    }
	  	    
	  	    // Nutzenbonus f�r Alternative E, falls Person Sch�ler/Student ist und Wochentag
	  	    if (person.personisinEducation() && currentDay.getWeekday()<6 && step.alternativeisEnabled("E"))
	  	    {
	  	    	step.adaptUtilityFactor("E", 1.3);
	  	    }
  	    }
   
  	    // Auswahl durchf�hren
  	    step.doStep();
  	    ActivityType activityType = ActivityType.getTypeFromChar(step.getAlternativeChosen().charAt(0));
  	    
  	    // DebugLogger schreiben falls aktiviert
  	    if(debugloggers!= null && debugloggers.existsLogger(id))
  	    {
  	    	debugloggers.getLogger(id).put(currentDay, String.valueOf(activityType));
  	    }
    		
  	    if (activityType!=ActivityType.HOME)
        {	
          // F�ge die Tour in das Pattern ein, falls sie noch nicht existiert
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
  	    	
  	    	// F�ge die Aktivit�t in das Pattern ein, falls sie noch nicht existiert
  	    	HActivity activity = null;
  	    	if (!currentDay.existsActivity(0,0))
          {
  	    		activity = new HActivity(mainTour, 0, activityType);
            mainTour.addActivity(activity);
          }
  	    	else
  	    	{
  	    		activity = currentDay.getTour(0).getActivity(0);
  	    		activity.setActivityType(activityType);
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
    	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgef�hrt
      if (currentDay.isHomeDay())
      {
      	continue;
      }
      
      // AttributeLookup erzeugen
  		AttributeLookup lookup = new AttributeLookup(person, currentDay);   	
    	
	    // Step-Objekt erzeugen
	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
	    
	    // Mindesttourzahl festlegen, falls es schon Touren aus gemeinsamen Aktivit�ten gibt
	    int mindesttourzahl=0;
	    
	    // Pr�fe, ob Mindesttourzahl aus lowerBound bereits erreicht wurde
	    if (currentDay.getAmountOfTours() < numberoftoursperday_lowerboundduetojointactions[currentDay.getIndex()])
	    {
	    	int verbleibendetouren = numberoftoursperday_lowerboundduetojointactions[currentDay.getIndex()] - currentDay.getAmountOfTours();
	    	// Bei 3A werden verbleibende Touren halbiert, da auch ggf. noch Touren in 3B modelliert werden k�nnen
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
	    
	    
	    // Entscheidung durchf�hren
	    step.doStep();
	    
	    // DebugLogger schreiben falls aktiviert
	    if(debugloggers!= null && debugloggers.existsLogger(id))
	    {
	    	debugloggers.getLogger(id).put(currentDay, String.valueOf(step.getDecision()));
	    }
            
      // Erstelle die weiteren Touren an diesem Tag basierend auf der Entscheidung und f�ge Sie in das Pattern ein, falls sie noch nicht existieren
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
    	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgef�hrt
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
                     
      for (HTour currentTour : currentDay.getTours())
      {
        /*
         * Ignoriere Touren, deren Hauptaktivit�t schon festgelegt ist
         * 	- Hauptouren des Tages (siehe Schritt 2)
         *  - andere Hauptaktivit�ten, welche �ber gemeinsame Aktivit�ten ins Pattern gekommen sind
         */
      	if(!currentDay.existsActivityTypeforActivity(currentTour.getIndex(),0))
        {
        	// AttributeLookup erzeugen
      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
        	
    	    // Step-Objekt erzeugen
    	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
    	    
    	    // Entferne die Alternative W, falls bereits Anzahl der Tage mit Arbeitsaktivit�ten erreicht sind!
    	    if (
    	    		person.getAttributefromMap("anztage_w") <= pattern.countDaysWithSpecificActivity(ActivityType.WORK) &&
    	    		currentDay.getTotalNumberOfActivitites(ActivityType.WORK) == 0
    	    		)
    	    {
    	    	step.disableAlternative("W"); 
    	    }
    	    
    	    // Entferne die Alternative E, falls bereits Anzahl der Tage mit Bildungsaktivit�ten erreicht sind!
    	    if (
    	    		person.getAttributefromMap("anztage_e") <= pattern.countDaysWithSpecificActivity(ActivityType.EDUCATION) &&
    	    		currentDay.getTotalNumberOfActivitites(ActivityType.EDUCATION) == 0
    	    		)
    	    {
    	    	step.disableAlternative("E"); 
    	    }
  	    
    	    //Auswahl durchf�hren
    	    step.doStep();
    	    ActivityType activityType = ActivityType.getTypeFromChar(step.getAlternativeChosen().charAt(0));
          
          // DebugLogger schreiben falls aktiviert
    	    if(debugloggers!= null && debugloggers.existsLogger(id))
    	    {
    	    	debugloggers.getLogger(id).put(currentTour, String.valueOf(activityType));
    	    }
          
  	    	HActivity activity = null;
  	    	
  	    	// Falls die Aktivit�t existiert wird nur deren Typ bestimmt
  	    	if (currentDay.existsActivity(currentTour.getIndex(),0))
          {
  	    		activity = currentTour.getActivity(0);
  	    		activity.setActivityType(activityType);
          }
  	    	// Erstelle die Aktivit�t mit entsprechendem Typ, falls Sie noch nicht exisitert
  	    	else
  	    	{ 	    		
  	    		activity = new HActivity(currentTour, 0, activityType);
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
    	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgef�hrt
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
  	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
    		
  	    // Mindesaktzahl festlegen, falls es schon Aktivit�ten aus gemeinsamen Aktivit�ten gibt
  	    int mindestaktzahl =0;
  	    
  	    // Pr�fe, ob Mindestaktzahl aus lowerBound bereits erreicht wurde
  	    if (currentDay.getTotalAmountOfActivitites() < numberofactsperday_lowerboundduetojointactions[currentDay.getIndex()])
  	    {
  	    	// Bestimme noch nicht verplante Aktivit�ten basierend auf Mindestzahl aus gemeinsamen Akt und bereits verplanten
  	    	int verbleibendeakt = numberofactsperday_lowerboundduetojointactions[currentDay.getIndex()] - currentDay.getTotalAmountOfActivitites();
  		
  	    	/*
  	    	 * Bestimme, wieviele Aktivit�tensch�tzungen in Schritt 5 noch durchgef�hrt werden
					 *
  	    	 * Formel: verbleibendenAnzahlanTouren * 2 (wegen 5A und 5B) - 1 (falls es schon Schritt 5B ist und damit 5A schon durchgef�hrt wurde
  	    	 * verbleibendeAnzahlanTouren =  currentDay.getHighestTourIndex() - aktuellerTourIndex(i) + 1
  	    	 */
  	    	int verbleibendeaktschaetzungen =  2*(currentDay.getHighestTourIndex() - i + 1) - (id.equals("5B") ? 1 : 0);
  	    	// Bestimme Mindestzahl aufgrund verbleibender Akt im Verh�ltnis zu verbleibenden Sch�tzungen 
  	    	mindestaktzahl = Math.round(verbleibendeakt/verbleibendeaktschaetzungen);
  	    	// bei der letzten Tour des Tages und der NACH-Aktivit�tenzahl muss Mindestanzahl zwingend erreicht werden
  	    	if (id.equals("5B") && currentTour.getIndex() == currentDay.getHighestTourIndex()) mindestaktzahl = verbleibendeakt;
  	    }
  	    
  	    // Alternativen limitieren basierend auf Mindesaktzahl
  	    step.limitLowerBoundOnly(mindestaktzahl);
  	    
  	    // Entscheidung durchf�hren
  	    step.doStep();    		

  	    // DebugLogger schreiben falls aktiviert
  	    if(debugloggers!= null && debugloggers.existsLogger(id))
  	    {
  	    	debugloggers.getLogger(id).put(currentTour, String.valueOf(step.getDecision()));
  	    }
  	    
  	    // Erstelle die weiteren Aktivit�ten in dieser Tour basierend auf der Entscheidung und f�ge Sie in das Pattern ein
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
    	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgef�hrt
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
      	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
      	    
      	    // Entferne die Alternative W, falls bereits Anzahl der Tage mit Arbeitsaktivit�ten erreicht sind!
      	    if (
      	    		person.getAttributefromMap("anztage_w") <= pattern.countDaysWithSpecificActivity(ActivityType.WORK) &&
      	    		currentDay.getTotalNumberOfActivitites(ActivityType.WORK) == 0
      	    		)
      	    {
      	    	step.disableAlternative("W"); 
      	    }
      	    
      	    // Entferne die Alternative E, falls bereits Anzahl der Tage mit Bildungsaktivit�ten erreicht sind!
      	    if (
      	    		person.getAttributefromMap("anztage_e") <= pattern.countDaysWithSpecificActivity(ActivityType.EDUCATION) &&
      	    		currentDay.getTotalNumberOfActivitites(ActivityType.EDUCATION) == 0
      	    		)
      	    {
      	    	step.disableAlternative("E"); 
      	    }
    	    
      	    //Auswahl durchf�hren
      	    step.doStep();

            // Aktivit�tstyp festlegen
      	    ActivityType activityType = ActivityType.getTypeFromChar(step.getAlternativeChosen().charAt(0));
      	    currentActivity.setActivityType(activityType);
      	    
      	    // DebugLogger schreiben falls aktiviert
      	    if(debugloggers!= null && debugloggers.existsLogger(id))
      	    {
      	    	debugloggers.getLogger(id).put(currentActivity, String.valueOf(activityType));
      	    }
          }
        }
      }
    }
	}

	/**
	 * 
	 * Festlegung von Default-Wegzeiten f�r alle Aktivit�ten
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
	    		act.createTripsforActivity();
	      }
	    }
	  }	
	}



	/**
	 * Regelbasiert:
	 * - Wenn Tourindex der urspr�nglichen Aktivit�t vorhanden ist, dann f�ge Sie in diese Tour ein
	 * - Wenn Aktindex der ursp�nglichen Aktivit�t in der Tour vorhanden ist, f�ge Sie an dem Platz ein	
	 * 
	 * - Wenn Tour oder Aktindex noch nicht vorhanden sind, nehme den n�chstgelegenen Index, der noch nicht durch gemeinsame Akt belegt ist
	 * 		Bsp. einzuf�gende Akt ist 1/1/3, h�chster Index ist aber 1/1/2, dann ersetze Akt 1/1/2 mit der gemeinsamen Akt
	 * 
	 * - Zeit�berlappungen beachten! Wenn Liste chronologisch abgearbeitet wird, bestimmt die Position der zuvor eingef�gten Aktivit�t die untere Grenze
	 * 
	 * - Gemeinsame Akt mit Typ 1 oder 3 von zuhause aus muss immer erste Akt in Tour sein, auch bei eingef�gter Tour	
	 * 
	 * @throws InvalidPatternException
	 * 
	 */
	private void placeJointActivitiesIntoPattern() throws InvalidPatternException
	{
		
	 	List<HActivity> listgemakt = person.getAllJointActivitiesforConsideration();
		HActivity.sortActivityListbyWeekStartTimes(listgemakt);
	
		/*
		 * Aktivit�tenliste in Wochensortierung durchgehen und bestehenden Aktivit�t durch gemeinsame aus der Liste ersetzen
		 * 
		 */
		
		for (int indexinliste=0 ; indexinliste < listgemakt.size(); indexinliste++)
		{
			HActivity gemakt = listgemakt.get(indexinliste);
			
			// Indextag der Aktivit�t bestimmen
			int gemakt_tagindex = gemakt.getDayIndex();
			// Tourindex der Aktivit�t bestimmen
			int gemakt_tourindex = gemakt.getTour().getIndex();
			// Aktindex der Aktivit�t bestimmen
			int gemakt_aktindex = gemakt.getIndex();
			// JointStatus der Aktivit�t bestimmen
			JointStatus gemakt_jointStatus = gemakt.getJointStatus();
			
			assert JointStatus.JOINTELEMENTS.contains(gemakt_jointStatus) : "keine gemeinsame Aktivit�t in der Liste der gemeinsamen Aktivit�ten!"; 
			
			
	  	/*
	  	 * Bestimme m�gliche Aktivit�ten, die ersetzt werden k�nnen
	  	 */
			List <HActivity> possibleact = new ArrayList<HActivity>();
			
			/*
			 *  Schritt 1: Alle verf�gbaren Aktivit�ten des Tages
			 */
			{
	    	for (HActivity act : pattern.getDay(gemakt_tagindex).getAllActivitiesoftheDay())
	    	{
	    		possibleact.add(act);
	    	}
	    	HActivity.sortActivityListbyIndices(possibleact);
			}
			
	  	/*
	  	 *  Schritt 2: 	Bestimme, ob es bereits getauschte Aktivit�ten an diesem Tag gibt. F�ge nur Aktivit�ten, die nach der letzten
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
	    		 * Falls die letzte getauschte Akt nicht unmittelbar zeitlich vor der aktuellen liegt, entferne die erste m�gliche Akt zum Tauschen aus
	    		 * der Liste, damit keine Touren mit L�cken entstehen!
	    		 * 
	    		 * Entferne die erste Akt zum Tauschen ebenfalls, falls diese zeitlich mit der letzten getauschten Akt �berlagert
	    		 */
	    		if ((letzteaktgetauscht.getJointStatus()!=JointStatus.JOINTTRIP && 
	    				HActivity.getTimebetweenTwoActivities(letzteaktgetauscht, gemakt)!=0 && 
	    				!letzteaktgetauscht.isActivityLastinTour())
	    				||
	    				(letzteaktgetauscht.getJointStatus()!=JointStatus.JOINTTRIP && 
	    				HActivity.getTimebetweenTwoActivities(letzteaktgetauscht, gemakt)<0))	
	    			possibleact.remove(0);
	    	}
	  	}
	  	
	  	/*
	  	 * Schritt 3:	Bestimme, ob es weitere gemeinsame Aktivit�ten an dem Tag gibt, die noch getauscht werden m�ssen
	  	 * 						Entferne entsprechend die letzten X Eintr�ge aus der Liste m�glicher Aktivit�ten, damit diese noch Platz finden!
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
	  	 * Schritt 4: Pr�fen, ob List aufgrund von Schritt 2&3 m�glicherweise leer ist.
	  	 * 						Falls ja, kann Aktivit�t nicht eigef�gt werden.
	  	 */
	  	if (possibleact.size()==0) 
	  	{
	  		if (Configuration.debugenabled) System.err.println("Akt konnte nicht ersetzt werden! Schritt 4");
	  		gemakt.removeJointParticipant(person);
	  		break;
	  	}
	  	
	  	
	  	/*
	  	 * Schritt 5: Gemeinsame Akt von Typ 1 oder 3, d.h. mit gemeinsamem Hinweg muss, falls es sich um die erste Aktivit�t
	  	 *  					auf der Tour handelt auch bei der eingef�gten Aktivit�t die erste der Tour sein.
	  	 *  
	  	 *  					Such in solchen F�llen alle andere ersten Aktivit�t von Touren in der Liste m�glicher Aktivit�ten und 
	  	 *  					arbeite mit der neuen Liste weiter
	  	 */
	  	{
	    	if ((gemakt_jointStatus==JointStatus.JOINTTRIPANDACTIVITY || gemakt_jointStatus==JointStatus.JOINTTRIP) && gemakt.isActivityFirstinTour())
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
	  	 * Schritt 6: Pr�fen, ob List aufgrund von Schritt 5 m�glicherweise leer ist.
	  	 * 						Falls ja, kann Aktivit�t nicht eigef�gt werden.
	  	 */
	  	if (possibleact.size()==0) 
	  	{
	  		if (Configuration.debugenabled) System.err.println("Akt konnte nicht ersetzt werden! Schritt 6");
	  		gemakt.removeJointParticipant(person);
	  		break;
	  	}
	  	
	  	
	  	/*
	  	 * Schritt 7: Pr�fen, ob der Tourindex der gemeinsamen Akt in den m�glichen Akt vorhanden ist (Prio zum Ersetzen!)
	  	 * 						Falls ja, dann pr�fen, ob es den Aktindex auf der Tour auch gibt (Prio zum Ersetzen!)
	  	 */
	  	{
	    	// F�ge alle Akt mit gleichem Tourindex in eine eigene Liste ein
	    	List<HActivity> possibleactgleichertourindex = new ArrayList<HActivity>();
	    	for (HActivity act : possibleact)
	    	{
	    		if (act.getTour().getIndex() == gemakt_tourindex)
	    		{
	    			possibleactgleichertourindex.add(act);
	    		}
	    	}
	    	// Falls Aktivit�t mit gleichen Tourindex existieren, arbeite mit dieser Liste weiter
	    	if (possibleactgleichertourindex.size()!=0)
	    	{
	    		possibleact = possibleactgleichertourindex;
	    	
	    		// F�ge alle Akt mit gleichem Aktindex in eine eigene Liste ein
	    		List<HActivity> possibleactgleicheraktindex = new ArrayList<HActivity>();
	      	for (HActivity act : possibleact)
	      	{
	      		if (act.getIndex() == gemakt_aktindex)
	      		{
	      			possibleactgleicheraktindex.add(act);
	      		}
	      	}
	      	// Falls Aktivit�t mit gleichen Aktindex existiert, arbeite mit dieser Liste weiter
	      	if (possibleactgleicheraktindex.size()!=0)
	      	{
	      		possibleact = possibleactgleicheraktindex;
	      	}
	    	}
	  	}
	  	
	  	/*
	  	 * Schritt 8: Falls eine Aktivit�t die letzte einer Tour ist und unmittelbar anschlie�end eine weitere gemeinsame Aktivit�t folgt,
	  	 * 						dann wird diese Aktivit�t entfernt, da keine Zeit f�r Heimaktivit�t �brig bleibt.
	  	 * 
	  	 * 						Anders ausgedr�ckt: Falls direkt anschlie�ende gemeinsame Aktivit�t, dann entferne alle letzten Aktivit�ten einer Tour
	  	 */
	  	{
	  		if (indexinliste < listgemakt.size()-1 && HActivity.getTimebetweenTwoActivities(gemakt,  listgemakt.get(indexinliste+1))==0)
	  		{
	  			// F�ge alle Akt, die nicht letzte Akt sind in eine eigene Liste ein
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
	  	 * Aufgrund von Schritt 8 kann es vorkommen, dass keine Aktivit�ten mehr �brig bleiben zum Ersetzen.
	  	 * Falls das der Fall ist kann die Aktivit�t nicht ersetzt werden! 
	  	 */
	  	if (possibleact.size()==0) 
	  	{
	  		if (Configuration.debugenabled) System.err.println("Akt konnte nicht ersetzt werden! Schritt 9");
	  		gemakt.removeJointParticipant(person);
	  		break;
	  	}
	
	  	/*
	  	 * Schritt 10: W�hle zuf�llig eine der verbleibenden m�glichen Aktivit�ten
	  	 */
	  	int zufallszahl = randomgenerator.getRandomValueBetween(0, possibleact.size()-1, 1);
	  	HActivity actforreplacement = possibleact.get(zufallszahl);
	  	
	  	/*
	  	 * Schritt 11: Aktivit�t durch gemeinsame Aktivit�t ersetzen
	  	 */
	  	{
	    	// Aktivit�teneigenschaften ermitteln
	    	int gemakt_duration = gemakt.getDuration();
	    	int gemakt_starttime = gemakt.getStartTime();
	    	ActivityType gemakt_acttype = gemakt.getActivityType(); 		
	    	int gemakt_creatorPersonIndex = gemakt.getCreatorPersonIndex();		
	    	
	    	int gemakt_durationtripbefore = gemakt.getEstimatedTripTimeBeforeActivity();
	    	
	    	// Aktivit�t markieren
	    	actforreplacement.addAttributetoMap("actreplacedbyjointact", 1.0);
	    	
	    	// Je nach Art der Gemeinsamkeit unterschiedliche Aktivit�teneigenschaften ersetzen
	    	switch(gemakt_jointStatus)
				{
					// Weg davor und Aktivit�t werden gemeinsam durchgef�hrt
					case JOINTTRIPANDACTIVITY:
					{			
						// Akteigenschaften ersetzen
						actforreplacement.setDuration(gemakt_duration);
						actforreplacement.setStartTime(gemakt_starttime);
						actforreplacement.setActivityType(gemakt_acttype);
						actforreplacement.setJointStatus(gemakt_jointStatus);
						actforreplacement.setCreatorPersonIndex(gemakt_creatorPersonIndex); 
						
						// Wegzeiten aufgrund m�glichen anderen Aktivit�tentyps neu berechnen
						actforreplacement.createTripsforActivity();
						
						// Hinweg erzeugen und ersetzen
						actforreplacement.setTripbeforeactivity(new HTrip(actforreplacement, TripStatus.TRIP_BEFORE_ACT, gemakt_durationtripbefore));
			
						break;
					}
					// Nur Aktivit�t wird gemeinsam durchgef�hrt
					case JOINTACTIVITY:
					{
						// Akteigenschaften ersetzen
						actforreplacement.setDuration(gemakt_duration);
						actforreplacement.setStartTime(gemakt_starttime);
						actforreplacement.setActivityType(gemakt_acttype);
						actforreplacement.setJointStatus(gemakt_jointStatus);
						actforreplacement.setCreatorPersonIndex(gemakt_creatorPersonIndex); 
						
						// Wegzeiten aufgrund m�glichen anderen Aktivit�tentyps neu berechnen
						actforreplacement.createTripsforActivity();
						
						break;
					}		
					// Nur Weg davor wird gemeinsam durchgef�hrt
					case JOINTTRIP:
					{
						// Akteigenschaften ersetzen
						actforreplacement.setJointStatus(gemakt_jointStatus);
						actforreplacement.setCreatorPersonIndex(gemakt_creatorPersonIndex); 
						
						// Weg erzeugen
						actforreplacement.setTripbeforeactivity(new HTrip(actforreplacement, TripStatus.TRIP_BEFORE_ACT, gemakt_durationtripbefore));
						actforreplacement.setStartTime(gemakt_starttime);
						
						break;
					}
				default:
						throw new RuntimeException();
				}			
	  	}
	  	
	  	// Schritt 12: Pr�fen, ob die Aktivi�t aufgrund m�glicher ge�nderter Wegzeiten nicht vielleicht doch mit einer anderen kollidiert
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
	
		
		//TODO  Sicherstellen, dass die Reihenfolge sortiert nach Index mit der nach Startzeit �bereinstimmt!
		
	}



	/**
	 * 
	 * @param id
	 * @param variablenname
	 */
	private void executeStep7DC(String id, ActivityType activitytype)
	{
		// Wird nur ausgef�hrt, wenn es zu dem Aktivit�tentyp auch Aktivit�ten gibt
	  if (pattern.countActivitiesPerWeek(activitytype)>0)
	  {
			// AttributeLookup erzeugen
			AttributeLookup lookup = new AttributeLookup(person);
			
	    // Step-Objekt erzeugen
	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
	    step.doStep();
	    
	    //Debug-Logger schreiben falls aktiviert
	    if(debugloggers!= null && debugloggers.existsLogger(id))
	    {
	    	debugloggers.getLogger(id).put(person, String.valueOf(step.getDecision()));
	    }
	    
	    // Ergebnis als Index und Alternative zu Personen-Map hinzuf�gen f�r sp�tere Verwendung
	    person.addAttributetoMap(activitytype+"budget_category_index", (double) step.getDecision());
	    person.addAttributetoMap(activitytype+"budget_category_alternative", Double.parseDouble(step.getAlternativeChosen()));
	  }
	  
	  // special case: if there is exactly no activity allocated for work, than we must set cat to 0
	  // needed to achieve value for Attribute zeitbudget_work_ueber_kat2
    if (activitytype==ActivityType.WORK && pattern.countActivitiesPerWeek(activitytype)==0)
    {
    	person.addAttributetoMap(activitytype+"budget_category_alternative", 0.0d);
    } 
	}

	/**
	 * 
	 * @param id
	 * @param activitytype
	 */
	private void executeStep7WRD(String id, ActivityType activitytype)
    {
	  	// Wird nur ausgef�hrt, wenn es zu dem Aktivit�tentyp auch Aktivit�ten gibt
	  	if (pattern.countActivitiesPerWeek(activitytype)>0)
      {
        // Entscheidung aus Schritt 7A-E ermitteln
        double chosenIndex = person.getAttributefromMap(activitytype+"budget_category_index");

        WRDDefaultModelStep step = new WRDDefaultModelStep(id, String.valueOf((int) chosenIndex), activitytype, this);
        step.doStep();
        
        int chosenTime = step.getchosenDistributionElement();
        
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
    	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgef�hrt
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
  	
      for (HTour currentTour : currentDay.getTours())
      {
      	// Anwendung des Modellschritts nur auf Hauptaktivit�ten
        HActivity currentActivity = currentTour.getActivity(0);
        
        // Schritt wird nur durchgef�hrt, falls Dauer der Aktivit�t noch nicht feststeht
        if(!currentActivity.durationisScheduled())
        {
        	// AttributeLookup erzeugen
      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
        	
    	    // Step-Objekt erzeugen
    	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
    	    step.doStep();
    	    
    	    //Debug-Logger schreiben falls aktiviert
    	    if(debugloggers!= null && debugloggers.existsLogger(id))
    	    {
    	    	debugloggers.getLogger(id).put(currentActivity, String.valueOf(step.getAlternativeChosen()));
    	    }

    	    // Save Attribute
    	    if (Configuration.coordinated_modelling && (currentActivity.getActivityType()==ActivityType.WORK || currentActivity.getActivityType()==ActivityType.EDUCATION))
    	    {
    	    	currentActivity.addAttributetoMap("standarddauer",(step.getAlternativeChosen().equals("yes") ? 1.0d : 0.0d));
    	    }
    	    else
    	    {
      	    // Bei unkoordinierter Modellierung ohne Stabilit�tsaspekte wird der Wert immer mit 0 �berschrieben!
     	     currentActivity.addAttributetoMap("standarddauer", 0.0d);    	    	
    	    }
        }
      }
    }
	}


	/**
	 * 
	 * @param id_dc
	 * @param id_wrd
	 * @throws InvalidPatternException
	 */
	private void executeStep8_MainAct(String id_dc, String id_wrd) throws InvalidPatternException
	{
		
	  for (HDay currentDay : pattern.getDays())
	  {
	  	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgef�hrt
	  	if (currentDay.isHomeDay())
	    {
	    	continue;
	    }
		
	  	// Anwendung des Modellschritts nur auf Hauptaktivit�ten
			for (HTour currentTour : currentDay.getTours())
			{
				boolean running=false;
				if (id_dc.equals("8B") && currentTour.isFirstTouroftheDay()) running=true;  // 8B gilt nur f�r erste Tour des Tages
				if (id_dc.equals("8D") && !currentTour.isFirstTouroftheDay()) running=true;	// 8D gilt nur ab der zweiten Tour des Tages)
					
				if (running)
				{
	        HActivity currentActivity = currentTour.getActivity(0);
	        
	  	    /*
	  	     * 
	  	     * DC-Schritt (8B, 8D)
	  	     * 
	  	     */
	        
	        // Schritt nur durchf�hren, falls Dauer noch nicht festgelegt wurde
	        if (!currentActivity.durationisScheduled())
	        {
	          // AttributeLookup erzeugen
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
	        	
	    	    // Step-Objekt erzeugen
	    	    DCDefaultModelStep step_dc = new DCDefaultModelStep(id_dc, this, lookup);
	    	    
	    	    // Alternativen ggf. auf Standardzeitkategorie einschr�nken
	    	    if (currentActivity.getAttributesMap().get("standarddauer") == 1.0d)
	    	    {
	    	    	// Ermittle die Standard-Zeitkategorie f�r den Tag und den Zweck
	    	      int timeCategory = currentActivity.calculateMeanTimeCategory();
	    	      
		    	    //Debug-Logger schreiben falls aktiviert
		    	    if(debugloggers!= null && debugloggers.existsLogger("meantime"))
		    	    {
		    	    	debugloggers.getLogger("meantime").put(currentActivity, String.valueOf(timeCategory));
		    	    }
	    	      	
	    	      // untere Grenze kann minimal 0 werden
	    	      int from = Math.max(timeCategory - 1,0);
	    	      // obere Grenze kann maximal in letzter Zeitklasse liegen
	    	      int to = Math.min(timeCategory + 1,Configuration.NUMBER_OF_ACT_DURATION_CLASSES-1);
	    	        
	    	      step_dc.limitUpperandLowerBound(from, to);
	    	      // add utility bonus of 10% to average time class (middle of the 3 selected)
	    	      step_dc.adaptUtilityFactor(timeCategory, 1.1);
	    	    } 	    
	    	    
	    	    // Grenzen aufgrund ggf. bereits festgelgten Dauern beschr�nken
	    	    int[] durationBounds = calculateDurationBoundsDueToOtherActivities(currentActivity);   
	    	    int loc_lowerbound = getDurationTimeClassforExactDuration(durationBounds[0]);
	    	    int loc_upperbound = getDurationTimeClassforExactDuration(durationBounds[1]);
	    	    
	    	    // Sicherstellen, dass die unter Grenze nicht �ber der oberen Grenze liegt
	    	    assert loc_lowerbound<=loc_upperbound;

	    	    // Beide Grenzen sind gleich, das hei�t Dauer ist gesetzt
	    	    if (loc_lowerbound==loc_upperbound)
	    	    {
	    	    	step_dc.limitUpperandLowerBound(loc_lowerbound, loc_upperbound);
	    	    }
	    	    // Beide Grenzen sind NICHT gleich
	    	    else
	    	    {   	    
		    	    // Limitiere die obere Grenze, falls diese kleiner ist als die bisherige oder nicht gesetzt ist
		    	    if (loc_upperbound <= step_dc.getUpperBound() || step_dc.getUpperBound()==-1) step_dc.limitUpperBoundOnly(loc_upperbound); 
	    	    
		    	    // Limitiere die untere Grenze, falls diese gr��er ist als die bisherige untere Grenze
		    	    if (loc_lowerbound >= step_dc.getLowerBound()) step_dc.limitLowerBoundOnly(loc_lowerbound);   
		    	    
		    	    // Limitiere die unter Grenze, falls diese jetzt h�her ist als die obere Grenze
		    	    if (step_dc.getLowerBound() >= step_dc.getUpperBound()) step_dc.limitLowerBoundOnly(step_dc.getUpperBound());   
	    	    }

	    	    		
	    	    // Sicherstellen, dass die unter Grenze nicht �ber der oberen Grenze liegt
	    	    assert step_dc.getLowerBound()<=step_dc.getUpperBound();
   	    		
	    	    // Wahlentscheidung durchf�hren
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
	    	     * WRD-step (8C, 8E)
	    	     * 
	    	     */
          	// Objekt basierend auf der gew�hlten Zeitkategorie initialisieren
			      double chosenTimeCategory = currentActivity.getAttributesMap().get("actdurcat_index");
			      WRDDefaultModelStep step_wrd = new WRDDefaultModelStep(id_wrd, String.valueOf((int) chosenTimeCategory), currentActivity.getActivityType(), this);
			      			      
			      // Limitiere die Grenzen entsprechend der ermittelten Min- und Maxdauern
			      step_wrd.setRangeBounds(durationBounds[0], durationBounds[1]);
			      
			      // Bestimme, ob Verteilung danach modifiziert wird für Stabilität
			      if (currentActivity.getAttributesMap().get("standarddauer") == 1.0d) step_wrd.setModifydistribution(true);
			      
			      // Wahlentscheidung durchf�hren
			      step_wrd.doStep();
			      int chosenTime = (int) step_wrd.getchosenDistributionElement();
			      		      
	    	    //Debug-Logger schreiben falls aktiviert
	    	    if(debugloggers!= null && debugloggers.existsLogger(id_wrd))
	    	    {
	    	    	debugloggers.getLogger(id_wrd).put(currentActivity, String.valueOf(chosenTime));
	    	    }
			     
			      // Speichere Ergebnisse ab
			      currentActivity.setDuration(chosenTime);
			      
			      // Lege m�gliche weitere Startzeiten von Aktivit�ten fest
			      HActivity.createPossibleStarttimes(currentTour.getActivities());
	        }
				}		
			}
	  }
	}



	/**
	 * 
	 * @param id_dc
	 * @param id_wrd
	 * @throws InvalidPatternException
	 */
	private void executeStep8_NonMainAct(String id_dc, String id_wrd) throws InvalidPatternException
	{

	  for (HDay currentDay : pattern.getDays())
	  {
	  	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgef�hrt
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
	        
	      	// Schritt nur durchf�hren, falls keine Hauptaktivit�t und Dauer noch nicht festgelegt wurde
	        if (currentActivity.getIndex() != 0 && !currentActivity.durationisScheduled())
	        {   	     
	          // AttributeLookup erzeugen
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
	        	
	    	    // Step-Objekt erzeugen
	    	    DCDefaultModelStep step_dc = new DCDefaultModelStep(id_dc, this, lookup);
	    	   
	    	    // Grenzen aufgrund ggf. bereits festgelgten Dauern beschr�nken
	    	    int[] durationBounds = calculateDurationBoundsDueToOtherActivities(currentActivity);   
	    	    int loc_lowerbound = getDurationTimeClassforExactDuration(durationBounds[0]);
	    	    int loc_upperbound = getDurationTimeClassforExactDuration(durationBounds[1]);
	    	    
	    	    // Sicherstellen, dass die unter Grenze nicht �ber der oberen Grenze liegt
	    	    assert loc_lowerbound<=loc_upperbound;

	    	    // Beide Grenzen sind gleich, das hei�t Dauer ist gesetzt
	    	    if (loc_lowerbound==loc_upperbound)
	    	    {
	    	    	step_dc.limitUpperandLowerBound(loc_lowerbound, loc_upperbound);
	    	    }
	    	    // Beide Grenzen sind NICHT gleich
	    	    else
	    	    {   	    
		    	    // Limitiere die obere Grenze, falls diese kleiner ist als die bisherige oder nicht gesetzt ist
		    	    if (loc_upperbound <= step_dc.getUpperBound() || step_dc.getUpperBound()==-1) step_dc.limitUpperBoundOnly(loc_upperbound); 
	    	    
		    	    // Limitiere die untere Grenze, falls diese gr��er ist als die bisherige untere Grenze
		    	    if (loc_lowerbound >= step_dc.getLowerBound()) step_dc.limitLowerBoundOnly(loc_lowerbound);   
		    	    
		    	    // Limitiere die unter Grenze, falls diese jetzt h�her ist als die obere Grenze
		    	    if (step_dc.getLowerBound() >= step_dc.getUpperBound()) step_dc.limitLowerBoundOnly(step_dc.getUpperBound());   
	    	    }
	    	    		
	    	    // Sicherstellen, dass die unter Grenze nicht �ber der oberen Grenze liegt
	    	    assert step_dc.getLowerBound()<=step_dc.getUpperBound();

	    	    // Wahlentscheidung durchf�hren
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
	    	     * WRD-Schritt
	    	     * 
	    	     */
          	// Objekt basierend auf der gew�hlten Zeitkategorie initialisieren
          	double chosenTimeCategory = currentActivity.getAttributesMap().get("actdurcat_index");
  		      WRDDefaultModelStep step_wrd = new WRDDefaultModelStep(id_wrd, String.valueOf((int) chosenTimeCategory), currentActivity.getActivityType(), this);
  		     
			      // Limitiere die Grenzen entsprechend der ermittelten Min- und Maxdauern
  		      step_wrd.setRangeBounds(durationBounds[0], durationBounds[1]);
			      
			      // Wahlentscheidung durchf�hren
  		      step_wrd.doStep();
			      int chosenTime = (int) step_wrd.getchosenDistributionElement();
			      
	    	    //Debug-Logger schreiben falls aktiviert
	    	    if(debugloggers!= null && debugloggers.existsLogger(id_wrd))
	    	    {
	    	    	debugloggers.getLogger(id_wrd).put(currentActivity, String.valueOf(chosenTime));
	    	    }
			     
			      // Speichere Ergebnisse ab
			      currentActivity.setDuration(chosenTime);
  		      
			      // Lege m�gliche weitere Startzeiten von Aktivit�ten fest
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
	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
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
	    	ActivityType tourtype = currentTour.getActivity(0).getActivityType();
	      if (tourtype == ActivityType.WORK || tourtype == ActivityType.EDUCATION)
	      {
	      	// AttributeLookup erzeugen
	    		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
	      	
	  	    // Step-Objekt erzeugen
	  	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
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
	 * Legt die Startzeiten f�r Touren fest bei denen es bereits festgelegte Startzeiten f�r Aktivit�ten gibt, 
	 * bspw. durch bereits festgelegte gemeinsame Aktivit�ten von anderen Personen
	 * @throws InvalidPatternException 
	 * 
	 */
	private void createTourStartTimesDueToScheduledActivities() throws InvalidPatternException
	{
		for (HDay currentDay : pattern.getDays())
	  {
			// Falls zu wenig Touren oder ein Heimtag vorliegt, wird der Tag �bersprungen
	    if (currentDay.isHomeDay())
	    {
	    	continue;
	    }
	  	
	    for (HTour currentTour : currentDay.getTours())
	    {
		  	// F�hre Schritt nur f�r Touren aus, die noch keine festgelegte Startzeit haben
		  	if (!currentTour.isScheduled())
		    {
		  		
		  		// Pr�fe, ob es eine Aktivit�t in der Tour gibt, deren Startzeit bereits festgelegt wurde (bspw. durch gemeinsame Aktivit�ten)
		  		int startTimeDueToScheduledActivities=99999;
		  		
	  			int tripdurations=0;
	  			int activitydurations=0;
	  			
		  		HActivity.sortActivityListbyIndices(currentTour.getActivities());
		  		for (HActivity tmpact : currentTour.getActivities())
		  		{
		  			/*
		  			 *  Wenn die Startzeit der Aktivit�t festgelegt ist, rechne von dem Punkt aus 
		  			 *  r�ckw�rts und ziehe alle Dauern bisheriger Wege und Aktivit�ten in der Tour ab
		  			 */		  			
		  			if (tmpact.startTimeisScheduled())
		  			{
		  				startTimeDueToScheduledActivities= tmpact.getTripStartTimeBeforeActivity() - tripdurations - activitydurations;
		  				break;
		  			}
		  			/*
		  			 * Andernfalls addiere die Tour und Aktivit�tszeit auf
		  			 */
		  			else
		  			{
		  				tripdurations += tmpact.getEstimatedTripTimeBeforeActivity();
		  				activitydurations += tmpact.getDuration();
		  			}
		  		}
		  		
		  		/*
		  		 * Durch bereits festgelegte gemeinsame Aktivit�ten kann es vorkommen, dass negative Tourstartzeiten entstehen.
		  		 * Bsp: Die Aktivit�t stammt von einer anderen Person und ist sehr nahe an 0 Uhr. Falls die aktuelle Personen einen 
		  		 * l�ngeren Default-Pendelweg hat, kann dadurch der Startzeitpunkt der Tour unter 0 Uhr fallen!
		  		 */
		  		if (startTimeDueToScheduledActivities<0)
		  		{
		  			throw new InvalidPatternException("Person", pattern, "TourStartTimes <0 " + currentTour);
		  		}
		  		
		  		// Lege Startzeit fest falls durch bereits festgelegte Aktivit�ten bestimmt 
		  		if (startTimeDueToScheduledActivities!=99999)
		  		{
		  			// Startzeit der Tour festlegen
		  			currentTour.setStartTime(startTimeDueToScheduledActivities);   
		  			// Setze die Startzeiten der Aktivit�ten in dieser Tour
		  			currentTour.createStartTimesforActivities();
		  		}
		    }
	    }
	  }
	}



	/**
	 * 
	 * @param id_dc
	 * @param id_wrd
	 * @param tournrdestages
	 * @throws InvalidPatternException
	 */
	private void executeStep10(String id_dc, String id_wrd, int tournrdestages) throws InvalidPatternException
	{
			
	  // STEP 10: determine time class for the start of the x tour of the day
		for (HDay currentDay : pattern.getDays())
	  {
			// Falls zu wenig Touren oder ein Heimtag vorliegt, wird der Tag �bersprungen
	    if (currentDay.isHomeDay()|| currentDay.getAmountOfTours()<tournrdestages)
	    {
	    	continue;
	    }
	  	
	    // Bestimme x-te Tour des Tages
	    HTour currentTour = currentDay.getTour(currentDay.getLowestTourIndex()+(tournrdestages-1));
	  	
	  	// F�hre Schritt nur f�r Touren aus, die noch keine festgelegte Startzeit haben
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
		    DCDefaultModelStep step_dc = new DCDefaultModelStep(id_dc, this, lookup);
		     		
	      // Bestimme Ober- und Untergrenze und schr�nke Alternativenmenge ein
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
		    
		    // F�hre Entscheidungswahl durch
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
		     * WRD-Schritt
		     * 
		     */
		    
		    // Ermittle Entscheidung aus Schritt DC-Modellschritt  		
	      double chosenStartCategory = (double) currentTour.getAttributesMap().get("tourStartCat_index");
	      
	      // Vorbereitungen und Objekte erzeugen
	      WRDDefaultModelStep step_wrd = new WRDDefaultModelStep(id_wrd, String.valueOf((int)chosenStartCategory), currentTour.getActivity(0).getActivityType(), this);
	      
	      int[] bounds_mc = calculateStartingBoundsForTours(currentTour, false);
	      step_wrd.setRangeBounds(bounds_mc[0], bounds_mc[1]);
	      
	      // Entscheidung durchf�hren
	      step_wrd.doStep();
	      int chosenStartTime = step_wrd.getchosenDistributionElement();
	      
  	    //Debug-Logger schreiben falls aktiviert
  	    if(debugloggers!= null && debugloggers.existsLogger(id_wrd))
  	    {
  	    	debugloggers.getLogger(id_wrd).put(currentTour, String.valueOf(chosenStartTime));
  	    }
	      
	      // Speichere Ergebnisse ab
	      currentTour.setStartTime(chosenStartTime);   	  	
	      
	      // Setze die Startzeiten der Aktivit�ten in dieser Tour
	      currentTour.createStartTimesforActivities();
	      
//TODO previousTour muss immer scheduled sein bei chronologier Modellierungsreihenfolge	      
	      // Stelle sicher, dass sich die Touren nicht �berlappen!
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
	
	  for (HDay currentDay : pattern.getDays())
	  {
	  	if (currentDay.isHomeDay())
	    {
	    	continue;
	    }
	    for (int j=currentDay.getLowestTourIndex(); j<=currentDay.getHighestTourIndex(); j++)
	    {
	    	HTour currentTour = currentDay.getTour(j);
	    	// Bestimme Heimzeit vor Tour f�r alle Touren ohne Startzeit
	      if (!currentTour.isScheduled())
	      {
	      	// 10S
	      	      	
	        	// AttributeLookup erzeugen
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
	        	
	    	    // Step-Objekt erzeugen
	    	    DCDefaultModelStep dcstep = new DCDefaultModelStep("10S", this, lookup);
	    	     		
	          // Bestimme Ober- und Untergrenze und schr�nke Alternativenmenge ein
	          int dcbounds[] = calculateBoundsForHomeTime(currentTour, true);
	    	    int lowerbound = dcbounds[0];
	    	    int upperbound = dcbounds[1];
	    	    dcstep.limitUpperandLowerBound(lowerbound, upperbound);
	    	    
	    	    // F�hre Entscheidungswahl durch
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
	    	    WRDDefaultModelStep step_wrd = new WRDDefaultModelStep("10T", String.valueOf((int)chosenHomeTimeCategory), currentTour.getActivity(0).getActivityType(), this);
	    	    int[] wrdbounds = calculateBoundsForHomeTime(currentTour, false);
	          step_wrd.setRangeBounds(wrdbounds[0], wrdbounds[1]);
	          
	          // Entscheidung durchf�hren
	          step_wrd.doStep();
	          int chosenTime = step_wrd.getchosenDistributionElement();
	          
	          
	    	    //Debug-Logger schreiben falls aktiviert
	    	    if(debugloggers!= null && debugloggers.existsLogger("10T"))
	    	    {
	    	    	debugloggers.getLogger("10T").put(currentTour, String.valueOf(chosenTime));
	    	    }
	          
	          // Speichere Ergebnisse ab
	          int starttimetour = currentDay.getTour(currentTour.getIndex()-1).getEndTime() + chosenTime;
	          currentTour.setStartTime(starttimetour);
	          
	          // Setze die Startzeiten der Aktivit�ten in dieser Tour
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
	  	// Ist der Tag durch Home bestimmt, wird der Schritt nicht ausgef�hrt
	  	if (currentDay.isHomeDay())
	    {
	    	continue;
	    }
	    
	    for (HTour currentTour : currentDay.getTours())
	    {
	      for (HActivity currentActivity : currentTour.getActivities())
	      {
	      	/* 
	      	 * Falls die Aktivit�t nicht von der Person selbst erzeugt wurde sondern von einer anderen Person stammt
	      	 * und als gemeinsame Aktivit�t �bernommen wurde, wird der Schritt �bersprungen
	      	 */
	      	if (currentActivity.getCreatorPersonIndex() != person.getPersIndex())
	      	{
	      		continue;
	      	}
	      	
	      	
	      	/*
	    		 * Schritte nur durchf�hren, falls Person nicht als letzte Person eines Haushalts modelliert wird
	    		 * Bei letzter Person im Haushalt k�nnen keine weiteren neuen gemeinsamen Aktivit�ten mehr erzeugt werden!
	    		 */
	    		if ((int) person.getAttributefromMap("numbermodeledinhh") != person.getHousehold().getNumberofPersonsinHousehold())
	    		{
	        	// AttributeLookup erzeugen
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
	        	
	    	    // Step-Objekt erzeugen
	    	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
	    	    step.doStep();
	    	    
      	    // DebugLogger schreiben falls aktiviert
      	    if(debugloggers!= null && debugloggers.existsLogger(id))
      	    {
      	    	debugloggers.getLogger(id).put(currentActivity, String.valueOf(step.getAlternativeChosen()));
      	    }
	
	          // Status festlegen
	    	    currentActivity.setJointStatus(JointStatus.getTypeFromInt(Integer.parseInt(step.getAlternativeChosen())));
	    		}
	  	    else
	  	    {
	  	    	// Falls letzte Person, sind keine weiteren gemeinsamen Aktionen m�glich
	    	    currentActivity.setJointStatus(JointStatus.NOJOINTELEMENT);
	  	    }
	      }
	    }
	  }
	}



	/**
	 * 
	 * Bestimmung des genaueren Aktivit�tenzwecks f�r den Zweck Shopping 'S', 'L' und 'W'
	 * 
	 * @param activity
	 * @param id
	 */
	private void executeStep98(HActivity activity, String id)
	{
	  // STEP 98A-C Verfeinerung Aktivit�tenzweck SHOPPING, LEISURE und WORK
	  
		HDay currentDay = activity.getDay();
		HTour currentTour = activity.getTour();
	
		// AttributeLookup erzeugen
		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, activity);   	
		
	  // Step-Objekt erzeugen
	  DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
	  step.doStep();
	  
    // DebugLogger schreiben falls aktiviert
    if(debugloggers!= null && debugloggers.existsLogger(id))
    {
    	debugloggers.getLogger(id).put(activity, String.valueOf(step.getAlternativeChosen()));
    }
	
	  // Speichere gew�hlte Entscheidung f�r weitere Verwendung
	  int chosenActivityType = Integer.parseInt(step.getAlternativeChosen());
	  
	  // Aktivit�tstyp festlegen
	  activity.setMobiToppActType((byte) chosenActivityType);          
	}

   

	
	/**
	 * 
	 * Bestimmt die Obergrenze und Untergrenze f�r die Aktivit�tendauern auf Basis bereits geplanter Aktivit�ten.
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
		 * 1. Ausgangspunkt (in absteigender Priorit�t)
		 * - Es gibt bereits eine vorhergehende Aktivit�t mit festgelegter Startzeit am Tag
		 * - Die letzte Aktivit�t des Vortags ragt in den aktuellen Tag hinein
		 * - Anfang des Tages (1 Minute Puffer f�r Heimzeiten)
		 * 
		 * 2. Ermittel alle Aktivit�tendauern zwischen Tagesanfang / letzter Aktivit�t und der aktuellen Akt
		 * 3. Ermittel alle Wegdauern zwischen Tagesanfang / letzter Aktivit�t und der aktuellen Akt
		 * 4. Ermittel Puffer f�r Heimzeiten f�r alle Touren zwischen Tagesanfang / Tour der letzten Aktivit�t und der aktuellen Akt
		 * 
		 * 
		 * Grundidee der Bestimmung der oberen Grenze
		 * 
		 * 1. Ausgangspunkt (in absteigender Priorit�t)
		 * - Es gibt bereits eine nachfolgende Aktivit�t mit festgelegter Startzeit am Tag
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
			
			// Suche nach letzter im Tagesverlauf bereits festgelegter Startzeit einer Aktivit�t
			if(act.compareTo(tmpact)==-1)		// Findet alle fr�heren Aktivit�t als die Aktivit�t selbst	
			{
				//System.out.println(tmpact.getTour().getIndex() + "/" + tmpact.getIndex());
				if(tmpact.startTimeisScheduled() && (last_act_scheduled==null || tmpact.getStartTime()>last_act_scheduled.getStartTime())) last_act_scheduled = tmpact;
			}	
			
			// Suche nach n�chster im Tagesverlauf bereits festgelegter Startzeit einer Aktivit�t
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
			ausgangspunktunteregrenze = last_act_scheduled.getStartTime() + (last_act_scheduled.durationisScheduled() ?  last_act_scheduled.getDuration() : last_act_scheduled.getDefaultActivityTime()); 
		}
		else
		{
			// Pr�fe, ob letzte Akt des Vortages in den aktuellen Tag ragt!
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
						// +1 f�r Heimaktivit�t nach Ende der letzten Tour des Vortages
						ausgangspunktunteregrenze = endeletzteaktvortag-1440+1;
					}
				}
			}
		}
	
		/*
		 * Bestimme Ausgangspunkt der oberen Grenze
		 */	

		int ausgangspunktoberegrenze=0;
		// Falls n�chste Aktivit�t bereits bestimmt ist, verwende diese als Richtgr��e
		if (next_act_scheduled!=null)
		{
			ausgangspunktoberegrenze = next_act_scheduled.getStartTime(); 
		}
		// Andernfalls ist 3 Uhr nachts die Obergrenze, es sei denn in dem Zeitraum bis 3 Uhr des n�chsten Tages ist bereits eine Akt geplant. 
		// Dann ist der Startzeitpunkt dieser Aktivit�t entsprechend der Ausgangspunkt
		else
		{
			ausgangspunktoberegrenze = 1620;
			
			// Pr�fe, ob erste Akt des Folgetages im Zeitraum bis 3 Uhr liegt!
			HDay folgetag = dayofact.getNextDay();
			if (folgetag!=null && !folgetag.isHomeDay())
			{
				HActivity ersteaktfolgetag = folgetag.getFirstTourOfDay().getFirstActivityInTour();
				if (ersteaktfolgetag.startTimeisScheduled())
				{
					int startersteaktfolgetag = ersteaktfolgetag.getTripStartTimeBeforeActivity();
					if (startersteaktfolgetag<180) 
					{
						// -1 f�r Heimaktivit�t vor der ersten Akt des Folgetags
						ausgangspunktoberegrenze = 1440 + startersteaktfolgetag -1;
					}
				}
			}
	
		}
	
		
		/*
		 * 2.
		 * 
		 * Ermittel die Aktivit�tendauern
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
		 * Ber�cksichtige jeweils 1 Minute pro Heimakt als Mindestpuffer
		 * 
		 */

		/*
		 * Vorher
		 */
		int timeforhomeactsincelastscheduled=0;		
	  if (last_act_scheduled==null)
		{
			// Z�hle wieviele Touren vor der aktuelle Tour liegen
	  	timeforhomeactsincelastscheduled += (act.getTour().getIndex() - act.getDay().getLowestTourIndex());
		}
		else
		{
			// Z�hle wieviele Touren zwischen der der letzten festgelegten und der aktuellen liegen
			timeforhomeactsincelastscheduled += (act.getTour().getIndex() - last_act_scheduled.getTour().getIndex());
		}
	  
	  /*
	   * Nachher
	   */
		int timeforhomeactuntilnextscheduled=0;
	  if (next_act_scheduled==null)
		{
			// Z�hle wieviele Touren nach der aktuellen Tour noch kommen
	  	timeforhomeactuntilnextscheduled += (act.getDay().getHighestTourIndex() - act.getTour().getIndex());
		}
		else
		{
			// Z�hle wieviele Touren zwischen der der n�chsten festgelegten und der aktuellen liegen
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
		 * Falls Aktivit�t selbst schon eine festgelegte Startzeit hat, wird dadurch die untere Grenze bestimmt -> ersetze lowerbound
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
     * Pr�fen, ob Aktivit�t innerhalb einer Tour liegt und vorhergehende und nachfolgende Aktivit�t bereits bzgl. der
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
     * Pr�fen, ob Aktivit�t und die nachfolgende bereits eine Startzeit haben.
     * Dann gilt ebenfalls untere Grenze = obere Grenze
     */
    if (act.startTimeisScheduled() && !act.isActivityLastinTour())
    {
    	if (act.getNextActivityinTour().startTimeisScheduled()) minduration=maxduration;
    }
    
    
    /*
     * R�ckgabe der Grenzen f�r die Dauer
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
	 * Bestimmt die Aktivit�tendauern zwischen zwei Aktivit�ten eines Tages
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
			// Suche alle Aktivit�ten die zwischen from und to liegen und addiere die Aktivit�tszeit auf das Ergebnis
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
					result += tmpact.getDefaultActivityTime();
				}
			}
		}
		return result;
	}
	
	
	
	/**
	 * 
	 * Bestimmt die Wegdauern zwischen zwei Aktivit�ten eines Tages
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
			// Suche alle Aktivit�ten die zwischen from und to (inkl. to) liegen und addiere die Wegzeiten auf das Ergebnis
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
	 * Bestimmt die Ober- und Untergrenze der Startzeiten f�r Touren basierend auf m�glichen schon festgelegten Startzeiten und Dauern
	 * Boolean-Wert categories bestimmt, ob die Zeitkategorien oder die konkreten Grenzwerte zur�ckgegeben werden
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
		 * 1. Ausgangspunkt (in absteigender Priorit�t)
		 * - Die Tour ist nicht die erste Tour des Tages -> Es gibt bereits die Endezeit der vorhergehenden Tour + 1
		 * - Die letzte Aktivit�t des Vortags ragt in den aktuellen Tag hinein
		 * - Anfang des Tages
		 * 
		 * 
		 * Grundidee der Bestimmung der oberen Grenze
		 * 
		 * 1. Ausgangspunkt (in absteigender Priorit�t)
		 * - Pr�fe, ob es bereits eine weitere geplante Anfangszeit einer Tour im Tagesverlauf gibt
		 * - Pr�fe, ob es am n�chsten Tag bis 3 Uhr morgens schon eine geplante Aktivit�t gibt
		 * - 3 Uhr Nachts des Folgetages als sp�testens Ende der Tour = 1620
		 * 
		 * 2. Alle noch geplanten Touren inkl. aller Aktivit�ts- und Wegzeiten abziehen zwischen Tagesende bzw. n�chster geplanter Tour
		 * 3. Puffer f�r Heimaktivit�ten zwischen den Touren
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
	  // Ansonsten pr�fe, ob letzte Aktivit�t des Vortags noch in den aktuellen Tag ragt
	  else
	  {
			// Pr�fe, ob letzte Akt des Vortages in den aktuellen Tag ragt!
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
						// +1 um noch eine Heimaktivit�t nach der letzten Tour des Vortags zu erm�glichen
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
	  int basisoberegrenze = 1440;
	  HTour nexttourscheduled=null;
	  
	  //Pr�fe, ob es im Tagesverlauf noch weitere geplante Touren gibt
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
	  // Pr�fe, ob am Folgetag bis 3 Uhr nachts bereits die erste Aktivit�t geplant ist, falls keine weitere geplante Tour an diesem Tag
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
					if (startersteaktfolgetag<(basisoberegrenze-1440)) 
					{
						basisoberegrenze = 1439 + startersteaktfolgetag;
					}
				}
			}	  						
	  }
	  
	  
	  /*
	   * 2. Aktivit�ts- und Wegzeiten bis Tagesende / n�chster geplanter Tour
	   * 3. Heimzeitpuffer
	   */
	  int tmptourdurations = 0;
	  int heimzeitpuffer = 0;
	  int tourindexfuersuche;
	  // Bestimme, bis zu welcher Tour die Dauern gez�hlt werden
	  if(nexttourscheduled!=null)
	  {
	  	// Falls n�chste Tour bekannt ist, werden alle Touren bis dahin gez�hlt
	  	tourindexfuersuche = nexttourscheduled.getIndex()-1;
	  }
	  else
	  {
	  	// Falls n�chste Tour nicht bekannt ist, werden alle restlichen Touren des Tages gez�hlt
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
	  
	  // UpperBound falls notwendig auf 1439 k�rzen, da keine sp�teren Anfangszeiten m�glich
	  if (upperbound>1439) upperbound=1439;
	  
	        
	  // Fehlerbehandlung, falls UpperBound kleiner ist als LowerBound
	  if (upperbound<lowerbound)
	  {
	  	String errorMsg = "TourStartTimes Tour " + tourday.getIndex() + "/" + tour.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	  	throw new InvalidPatternException("Person",pattern, errorMsg);
	  }
	
	  
	  // Zeitklassen f�r erste Tour des Tages
	  if(categories && tour.getIndex()== tourday.getLowestTourIndex())
	  {
	    // Setze die Zeiten in Kategorien um
	      for (int i=0; i<Configuration.NUMBER_OF_FIRST_START_TIME_CLASSES; i++)
	      {
	      	if (lowerbound>=Configuration.FIRST_TOUR_START_TIMECLASSES_LB[i] && lowerbound<=Configuration.FIRST_TOUR_START_TIMECLASSES_UB[i])
	      	{
	      		lowercat =i;
	      	}
	      	if (upperbound>=Configuration.FIRST_TOUR_START_TIMECLASSES_LB[i] && upperbound<=Configuration.FIRST_TOUR_START_TIMECLASSES_UB[i])
	      	{
	      		uppercat =i;
	      	}
	      }
	    }
	
	  // Zeitklassen f�r zweite und dritte Tour des Tages
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
	 * Bestimme die Grenzen f�r die Dauer der Heimzeit
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
	  		// +1 um jeweils nach der Tour noch eine Heimaktivit�t von min. einer Minute zu erm�glichen
	  		tmptourdurations += tmptour.getTourDuration() + 1;
	  	}
	  }
	  // Falls keine weitere Tour geplant ist, pr�fe, ob bis 3 Uhr am Folgetag eine Tour startet
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
   * method to create home activities between two tours
   * 
   * @param allmodeledActivities
   * @throws InvalidPersonPatternException
   */
  private void createHomeActivities(List<HActivity> allmodeledActivities) throws InvalidPatternException 
  {
  	ActivityType homeact = ActivityType.HOME;
  	
  	if(allmodeledActivities.size()!=0)
  	{	
    	// create home activity before starting the first tour
    	int duration1 = allmodeledActivities.get(0).getTripStartTimeBeforeActivityWeekContext();

    	//assert duration1>0 : "person error - no home activity possible at beginning of the week!";
    	if (duration1<=0) throw new InvalidPatternException("person",this.pattern,"person error - no home activity possible at beginning of the week!");
    	
    	pattern.addHomeActivity(new HActivity(pattern.getDay(0), homeact, duration1, 0));
    	    	
    	// loop through all activities and create home activities after last activity in a tour
    	for (int i=0; i<allmodeledActivities.size()-1; i++)
    	{
    		HActivity act = allmodeledActivities.get(i);
    		if (act.isActivityLastinTour())
    		{
    			HTour acttour = act.getTour();
    			HTour nexttour =  allmodeledActivities.get(i+1).getTour();
    			
    			int ende_tour = acttour.getEndTimeWeekContext();
    			int start_next_tour = nexttour.getStartTimeWeekContext();
    			
    			// calculate buffer
    			int duration2 = start_next_tour - ende_tour;
   			
    			//assert (duration2>0) : "person error - no home activity possible after end of the tour! - " + start_next_tour + " // " + ende_tour;
    			if (duration2<=0) throw new InvalidPatternException("person",this.pattern,"person error - no home activity possible after end of the tour! - " + start_next_tour + " // " + ende_tour);
    			// get corresponding day for home activity
    			int day = (int) ende_tour/1440;
    			int starttime = ende_tour%1440;

    			// if an activity start after end of day 7, the activity will still be part of day 7 since there is no day 8 modeled
    			if (day==7)
    			{
    				day=6;
    				starttime = starttime+1440; 
    			}
    			// add home activity
    			pattern.addHomeActivity(new HActivity(pattern.getDay(day), homeact, duration2, starttime));
    		}
    	}
    	
    	// check the remaining time after the last activity of the week
    	HActivity lastact = allmodeledActivities.get(allmodeledActivities.size()-1);
    	int ende_lastTour = lastact.getTour().getEndTimeWeekContext();
    	if (ende_lastTour<10080)
    	{
    		// calculate buffer
    		int duration3 = 10080 - ende_lastTour;
    		// get corresponding day for home activity
    		int day = (int) ende_lastTour/1440;
    		// add home activity
    		pattern.addHomeActivity(new HActivity(pattern.getDay(day), homeact, duration3, ende_lastTour%1440));
    	}
  	}
  	// otherwise the activity list is empty - generate home activity for the whole week
    else
    {
    	pattern.addHomeActivity(new HActivity(pattern.getDay(0), homeact, 10080, 0));
    }
  }
  
  /**
   * 
   * Methode bestimmt, mit welchen anderen Personen, bisher im Haushalt noch nicht modellierten Personen
   * gemeinsame Aktivit�ten und Wege durchgef�hrt werden und f�gt diese den Listen der Personen hinzu
   * 
   */
	private void selectWithWhomforJointActions() 
	{

		for (HActivity tmpactivity : pattern.getAllOutofHomeActivities()) 
		{
			/*
			 * Aktivit�t in die Liste gemeinsamer Aktivit�ten anderer Personen hinzuf�gen, falls
			 * die Aktivit�t gemeinsam ist UND nicht von einer anderen Person urspr�nglich erzeugt wurde (das hei�t keine gemeinsame Aktivit�t 
			 * des Ursprungs einer anderen Person ist)
			 * 
			 */
			if (tmpactivity.getJointStatus()!=JointStatus.NOJOINTELEMENT && tmpactivity.getCreatorPersonIndex()==person.getPersIndex()) 
			{
				
				// Erstelle Map mit allen anderen Personennummern im Haushalt, die noch nicht modelliert wurden und w�hle zuf�llig eine
				Map<Integer,ActitoppPerson> otherunmodeledpersinhh = new HashMap<Integer, ActitoppPerson>();
				// F�ge zun�chst alle Personen des Haushalts hinzu
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
					// Bestimme, mit wievielen Personen die Aktivit�t durchgef�hrt wird
					int anzahlweiterepersausverteilung=99;
					double randomvalue = randomgenerator.getRandomValue();
					int hhgro = person.getHousehold().getNumberofPersonsinHousehold();
					
					/*
					 * Wahrscheinlichkeiten f�r die Anzahl mehrerer Personen stammt aus MOP-Auswertungen
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
					
					//TODO Verbesserungsm�glichkeit: Die Auswahl gemeinsamer Personen kontextsensitiver gestalten, bspw. immer Vater mit Kind, ...
								
					// Maximale Anzahl wird zus�tzlich begrenzt von der Anzahl weitere m�glicher Personen, die noch nicht modelliert wurden
					int anzahlweiterepers = Math.min(anzahlweiterepersausverteilung, otherunmodeledpersinhh.size());
					for (int i=1 ; i<= anzahlweiterepers; i++)
					{
						// W�hle eine zuf�llige Nummer der verbleibenden Personen
						List<Integer> keys = new ArrayList<Integer>(otherunmodeledpersinhh.keySet());
						Integer randomkey = keys.get(randomgenerator.getRandomPersonKey(keys.size()));
						
						// Aktivit�t zur Ber�cksichtigung bei anderer Person aufnehmen
						ActitoppPerson otherperson = otherunmodeledpersinhh.get(randomkey);
						otherperson.addJointActivityforConsideration(tmpactivity);
						
						// Andere Person als Teilnehmer bei der Aktivit�t eintragen
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
			switch (act.getActivityType())
			{
				case WORK:
					//DECISION notwendig - 1 oder 2
					executeStep98(act, "98C");
					break;
				case EDUCATION:
					act.setMobiToppActType((byte) 3);
					break;
				case SHOPPING:
					//DECISION notwendig - 11 oder 41 oder 42	
					executeStep98(act, "98A");
					break;
				case LEISURE:
					//DECISION notwendig - 12 oder 51 oder 52 oder 53 oder 77
					executeStep98(act, "98B");
					break;
				case TRANSPORT:
					act.setMobiToppActType((byte) 6);
					break;
				case HOME:
					act.setMobiToppActType((byte) 7);
					break;
				default:
					System.err.println("unknown activity type");
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

   
	public WRDDiscreteDistribution getpersonalWRDdistribution(String id, String categoryName, ActivityType activityType)
  {
  	return personalWRDDistributions.get(id+categoryName+activityType);
  }
	
	public void addpersonalWRDdistribution(String id, String categoryName, ActivityType activityType, WRDDiscreteDistribution wrddist)
  {
  	personalWRDDistributions.put(id+categoryName+activityType,wrddist);
  }
}
