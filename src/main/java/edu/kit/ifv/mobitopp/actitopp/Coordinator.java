package edu.kit.ifv.mobitopp.actitopp;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


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
  	
 	
  	if (person.getPersIndex()==5600)
  	{
  		System.out.println("");
  	}
  	
  	// Durchführung der Modellschritte
  
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
    executeStep8_MainAct_DC("8B");
    executeStep8_MainAct_MC("8C");
    executeStep8_MainAct_DC("8D");
    executeStep8_MainAct_MC("8E");
    executeStep8_NonMainAct_DC("8J");
    executeStep8_NonMainAct_MC("8K");
    
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
    
    executeStep10DC("10M", 1);
    executeStep10MC("10N", 1);
    executeStep10DC("10O", 2);
    executeStep10MC("10P", 2);    
    executeStep10DC("10Q", 3);
    executeStep10MC("10R", 3);
    executeStep10ST();
					 
    // Finalisierung der Wochenaktivitätenpläne 

  	// 1) Erstelle Startzeiten für jede Aktivität 
    createStartTimesforActivities();
    
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
    	// AttributeLookup erzeugen
  		AttributeLookup lookup = new AttributeLookup(person, currentDay);   	
    	
	    // Step-Objekt erzeugen
	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
	    step.doStep();
	          
	    char activityType = step.getAlternativeChosen().charAt(0);
      if (activityType!='H')
      {	
        // Füge die Tour in das Pattern ein
        HTour mainTour = new HTour(currentDay, 0);
        currentDay.addTour(mainTour);
                  
        // Füge die Aktivität in das Pattern ein
        HActivity activity = new HActivity(mainTour, 0, activityType);
        mainTour.addActivity(activity);
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
	    step.doStep();
            
      // Erstelle die weiteren Touren an diesem Tag basierend auf der Entscheidung und füge Sie in das Pattern ein
      for (int j = 1; j <= step.getDecision(); j++)
      {
      	HTour tour = null;
      	// 3A - Touren vor der Haupttour
        if (id.equals("3A")) tour = new HTour(currentDay, (-1) * j);
      	// 3B - Touren nach der Haupttour
        if (id.equals("3B")) tour = new HTour(currentDay, (+1) * j);        
        
        currentDay.addTour(tour);
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
        // ignore main tour, as it is already created and has a main activity
        if (currentTour.getIndex() != 0)
        {
        	// AttributeLookup erzeugen
      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
        	
    	    // Step-Objekt erzeugen
    	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
    	    step.doStep();

          // Speichere gewählte Entscheidung für weitere Verwendung
          char chosenActivityType = step.getAlternativeChosen().charAt(0);

          //Erstelle für den gewählten Tourtyp die Hauptaktivität der Tour
          HActivity activity = new HActivity(currentTour, 0, chosenActivityType);
          currentTour.addActivity(activity);
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
  	    step.doStep();

  	    // Erstelle die weiteren Aktivitäten in dieser Tour basierend auf der Entscheidung und füge Sie in das Pattern ein
        for (int j = 1; j <= step.getDecision(); j++)
        {
        	HActivity act = null;
        	// 3A - Touren vor der Haupttour
          if (id.equals("5A")) act = new HActivity(currentTour, (-1) * j);
        	// 3B - Touren nach der Haupttour
          if (id.equals("5B")) act = new HActivity(currentTour, (+1) * j);
          
          currentTour.addActivity(act);
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
          if (currentActivity.getIndex()!= 0)
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
        
        // AttributeLookup erzeugen
    		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
      	
  	    // Step-Objekt erzeugen
  	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
  	    step.doStep();

  	    // Eigenschaft abspeichern
  	    currentActivity.addAttributetoMap("standarddauer",(step.getAlternativeChosen().equals("yes") ? 1.0d : 0.0d));
      }
    }
	}


	/**
	 * 
	 * @param id
	 */
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
  			if (id.equals("8B") && currentTour.getIndex()==0) running=true;
  			if (id.equals("8D") && currentTour.getIndex()!=0) running=true;
  				
  			if (running)
  			{
          HActivity currentActivity = currentTour.getActivity(0);
          
          // AttributeLookup erzeugen
      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
        	
    	    // Step-Objekt erzeugen
    	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
    	    modifyAlternativesDueTo8A(currentActivity, step);  	    
    	    step.doStep();

    	    // Entscheidungsindex abspeichern
    	    currentActivity.addAttributetoMap("actdurcat_index",(double) step.getDecision()); 	
  			}		
  		}
    }			
  }

	/**
	 * 
	 * @param id
	 */
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
		    
		      double chosenTimeCategory = currentActivity.getAttributesMap().get("actdurcat_index");
		      DefaultMCModelStep step = new DefaultMCModelStep(id + (int) chosenTimeCategory, this);
		      step.setModifiedDTDtoUse(currentActivity.getType(), (int) chosenTimeCategory);
		      step.setModifyDTDAfterStep(true);
		      step.setDTDTypeToUse(INDICATOR_ACT_DURATIONS);
		      step.doStep();
		     
		      // Speichere Ergebnisse ab
		      currentActivity.setDuration(step.getChosenTime());
  			}
  		}
    }
	}
	
	
	/**
	 * 
	 * @param id
	 */
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
          if (currentActivity.getIndex() != 0)
          {
          	 // AttributeLookup erzeugen
        		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
          	
      	    // Step-Objekt erzeugen
      	    DefaultDCModelStep step = new DefaultDCModelStep(id, this, lookup);
      	    modifyAlternativesForStep8J(currentDay, currentTour, step);
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
          if (currentActivity.getIndex() != 0)
          {
          	double chosenTimeCategory = currentActivity.getAttributesMap().get("actdurcat_index");
  		      DefaultMCModelStep step = new DefaultMCModelStep(id + (int) chosenTimeCategory, this);
  		      step.setModifiedDTDtoUse(currentActivity.getType(), (int) chosenTimeCategory);
  		      step.setModifyDTDAfterStep(true);
  		      step.setDTDTypeToUse(INDICATOR_ACT_DURATIONS);
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
   * @param id
   * @param tournrdestages
   * @throws InvalidPatternException
   */
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
	          int dcbounds[] = calculateBoundsForHomeTime(currentDay, currentTour, true);
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
	          mcstep.setModifyDTDAfterStep(true);
	          //step.setOutPropertyName("tourStartTime");
	          mcstep.setDTDTypeToUse(INDICATOR_ACT_DURATIONS);
	          int[] mcbounds = calculateBoundsForHomeTime(currentDay, currentTour, false);
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
      	        
      int from = timeCategory -1;
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
	 * max time class to be chosen= min(1440-totalDailyTripTime-durationOfMainActs, mainActDur-1))
	 * 
	 * @param day
	 * @param acttour
	 * @param step8j
	 */
	private void modifyAlternativesForStep8J(HDay day, HTour acttour, DefaultDCModelStep step8j)
	{
//TODO Optimierungspotential - die Aktivität darf nicht länger sein als die noch übrige Zeit - 
//TODO nicht nur abhängig von den Hauptaktivitäten sondern auch den restlichen bisher festgelegten Aktivitäten
	    
		// Obergrenze 1 - bisher festgelegte "verbrauchte" Zeiten am Tag
			int totalMainActivityTime = 0;
			// Alle Hauptaktivitäten + zugehörige Wege + letzter Weg am Ende der Tour
		    for (HTour tour : day.getTours())
		    {
		    	totalMainActivityTime += tour.getActivity(0).getDuration() + tour.getActivity(0).getEstimatedTripTime() + tour.getLastActivityInTour().getEstimatedTripTimeAfterActivity();
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
	 * @param day
	 * @param tour
	 * @param categories
	 * @return
	 * @throws InvalidPatternException
	 */
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
    	if (previousTour.isScheduled())
    	{
    		preTourTime = previousTour.getStartTime() + previousTour.getTourDuration();
    	}
// TODO if Bedingung rausnehmen - Tour sollte scheduled sein in diesem Schritt
    	else
    	{
    		preTourTime = 0 + previousTour.getTourDuration();
    	}
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
//TODO Checken, ob korrekte Methode aufgerufen wird - ActivityTime oder Tourtime
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
              	if (act.isActivityFirstinTour())
              	{
              		act.setStartTime(tour.getStartTime() + act.getEstimatedTripTime());
              	}
              	// Ansonsten durch das Ende der vorherigen Aktivität
              	else
              	{
              		act.setStartTime(act.getPreviousActivityinTour().getEndTime() + act.getEstimatedTripTime());
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
    	int duration1 = allmodeledActivities.get(0).getTripStartTimeWeekContext();
//TODO Ändern
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
    			assert duration2>0 : "Fehler - keine Home-Aktivität nach Ende der Tour möglich!";
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
