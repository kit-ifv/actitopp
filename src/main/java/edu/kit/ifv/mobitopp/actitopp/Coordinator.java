package edu.kit.ifv.mobitopp.actitopp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Tim Hilgert
 * 
 * class to coordinate the modeling of week activity schedules
 * will be called from {@link ActitoppPerson} to generate schedules
 * 
 */
public class Coordinator
{

    ////////////////
    
    //	declaration of variables
    
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
     * a 8-hour working activity, the distribution element (8 hours) will get a bonus that it is more likely to choose 
     * 8 hours again the next time for this person. This ensures better stability of duration and starttime modeling.		
     */
    private HashMap<String, WRDDiscreteDistribution> personalWRDDistributions;
    
    
    // Important for modeling joint actions
    
   	private int[] numberofactsperday_lowerboundduetojointactions = {0,0,0,0,0,0,0};
  	private int[] numberoftoursperday_lowerboundduetojointactions = {0,0,0,0,0,0,0};
    
    
    /**
     * 
     * Constructor
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
     * Constructor including debug-loggers
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
   * main method to coordinate all model steps
   *
   * @return
   * @throws InvalidPatternException
   */
  public void executeModel() throws InvalidPatternException
  {
  
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

    // joint activities
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
  		// select other persons to join activity or trip
  		selectWithWhomforJointActions();		
    }
    
					 
    // finalizing activity schedules
    
    // 1) create and sort a list including all modeled activities of the whole week
    List<HActivity> allModeledActivities = pattern.getAllOutofHomeActivities();    	
    HActivity.sortActivityListbyWeekStartTimes(allModeledActivities);
	
    // 2) create home activities to be performed between tours
    createHomeActivities(allModeledActivities);
    
    // 3) convert actiTopp purpose types to mobiTopp ones
    convertactiToppPurposesTomobiToppPurposeTypes(pattern.getAllActivities());
    
    // DEBUG
    if (Configuration.debugenabled)
    {
    	pattern.printAllActivitiesList();
    }
    
    // first sanity checks: check for overlapping activities. if found, throw exception and redo activityweek
    pattern.weekPatternisFreeofOverlaps();

  }
  
  /**
   * select the minimum number of tours and activities for each day
   * based on the list of known joint activities, the method decides for a minimum number of tours
   * and activities that are needed to perform these known joint activities
   */
  private void determineMinimumTourActivityBounds()
  {
  	
  	/*
  	 * idea:
  	 * 
  	 * - decide for a minimum number of tours and activities for each day based on the list of known joint activities
  	 *
  	 * - model number of tours and activities (modeling steps 1-6) using the following the following constraints
  	 * 		- there has to be at least one tour if joint activities or trips exists for the day
  	 * 		- if there are more than 2 joint actions --> at least two tours
  	 * 
  	 */

  	for (HActivity act : person.getAllJointActivitiesforConsideration())
  	{
  		// Count number of activities
  		numberofactsperday_lowerboundduetojointactions[act.getDayIndex()] += 1;
  		
  		// Determine number of tours
  		if (numberofactsperday_lowerboundduetojointactions[act.getDayIndex()] <= 2) 
  		{
  			numberoftoursperday_lowerboundduetojointactions[act.getDayIndex()] = 1;
  		}
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
		// create attribute lookup
		AttributeLookup lookup = new AttributeLookup(person);
		
    // create step object
    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
    step.doStep();
    
    // save result
    double decision = Double.parseDouble(step.getAlternativeChosen());
    // set anztage_w to 0 if person is not allowed to work (this may be configured for minors)
    if (variablenname == "anztage_w" && !person.isAllowedToWork()) decision=0;  
    
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
    	// execute step if main activity type does not exist
    	if(!currentDay.existsActivityTypeforActivity(0,0))
    	{
      	// create attribute lookup
    		AttributeLookup lookup = new AttributeLookup(person, currentDay);   	
      	
  	    // create step object
  	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
  	    
  	    // if there are existing tours (e.g., from joint activities) , disable H as alternative as being at home is no longer a valid alternative
  	    if (currentDay.getAmountOfTours()>0 || numberoftoursperday_lowerboundduetojointactions[currentDay.getIndex()]>0)
  	    {
  	    	step.disableAlternative("H"); 
	    	}
  	    
  	    if (Configuration.coordinated_modelling)
  	    {
	  	    // if number of working days is achieved, disable W as alternative
 	  	    if (
	  	    		person.getAttributefromMap("anztage_w") <= pattern.countDaysWithSpecificActivity(ActivityType.WORK) &&
	  	    		currentDay.getTotalNumberOfActivitites(ActivityType.WORK) == 0 &&
	  	    		person.personisAnywayEmployed()
	  	    		)
	  	    {
	  	    	step.disableAlternative("W"); 
	  	    }	  	    
 	  	    // if number of education days is achieved, disable E as alternative
	  	    if (
	  	    		person.getAttributefromMap("anztage_e") <= pattern.countDaysWithSpecificActivity(ActivityType.EDUCATION) &&
	  	    		currentDay.getTotalNumberOfActivitites(ActivityType.EDUCATION) == 0 &&
	  	    		person.personisinEducation()
	  	    		)
	  	    {
	  	    	step.disableAlternative("E"); 
	  	    }
	  	    
	  	    // utility bonus for alternative W if person is employed and day is from Monday to Friday
	  	    if (person.personisAnywayEmployed() && currentDay.getWeekday()<6 && step.alternativeisEnabled("W"))
	  	    {
	  	    	step.adaptUtilityFactor("W", 1.3);
	  	    }
	  	    // utility bonus for alternative E if person is in Education and day is from Monday to Friday
	  	    if (person.personisinEducation() && currentDay.getWeekday()<6 && step.alternativeisEnabled("E"))
	  	    {
	  	    	step.adaptUtilityFactor("E", 1.3);
	  	    }
  	    }
   
  	    // make selection
  	    step.doStep();
  	    ActivityType activityType = ActivityType.getTypeFromChar(step.getAlternativeChosen().charAt(0));
  	    
  	    if(debugloggers!= null && debugloggers.existsLogger(id))
  	    {
  	    	debugloggers.getLogger(id).put(currentDay, String.valueOf(activityType));
  	    }
    		
  	    if (activityType!=ActivityType.HOME)
        {	
          // add a new tour into the pattern if not existing
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
  	    	
  	    	// add a new activity into the pattern if not existing or set activity type
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
    	// skip day if person is at home
      if (currentDay.isHomeDay())
      {
      	continue;
      }
      
      // create attribute lookup
  		AttributeLookup lookup = new AttributeLookup(person, currentDay);   	
    	
	    // create step object
	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
	    
	    // initialize minimum number of tours
	    int minnumberoftours=0;
	    
	    // check if minimum number bound is already achieved
	    if (currentDay.getAmountOfTours() < numberoftoursperday_lowerboundduetojointactions[currentDay.getIndex()])
	    {
	    	int remainingnumberoftours = numberoftoursperday_lowerboundduetojointactions[currentDay.getIndex()] - currentDay.getAmountOfTours();
	    	// Half number of tours for step 3A as some of them will be modeled using step 3B
	    	if (id.equals("3A")) minnumberoftours = Math.round(remainingnumberoftours/2);
	    	// set all remaining tours for step 3B
	    	if (id.equals("3B")) minnumberoftours = remainingnumberoftours;
	    }  
	    
	    // limit alternatives (lower bounds)
	    step.limitLowerBoundOnly(minnumberoftours);
	    
	    // limit alternatives (upper bound using result from step 1k)
	    if (Configuration.coordinated_modelling)
	    {
	    	int maxnumberoftours=-1;
	    	if (person.getAttributefromMap("anztourentag_mean")==1.0d) maxnumberoftours=1;
	    	if (person.getAttributefromMap("anztourentag_mean")==2.0d) maxnumberoftours=2;
	    	if (maxnumberoftours!=-1) step.limitUpperBoundOnly((maxnumberoftours>=minnumberoftours ? maxnumberoftours : minnumberoftours));
	    }
	    
	    
	    // make selection
	    step.doStep();
	    
	    if(debugloggers!= null && debugloggers.existsLogger(id))
	    {
	    	debugloggers.getLogger(id).put(currentDay, String.valueOf(step.getDecision()));
	    }
            
      // create tours based on the decision and add them to the pattern
	    for (int j = 1; j <= step.getDecision(); j++)
      {
      	HTour tour = null;
      	// 3A - tours before main tour
        if (id.equals("3A") && !currentDay.existsTour(-1*j)) tour = new HTour(currentDay, (-1) * j);
      	// 3B - tours after main tour
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
    	// skip day if person is at home
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
                     
      for (HTour currentTour : currentDay.getTours())
      {
        /*
         * ignore tours if main activity purpose is already set
         */
      	if(!currentDay.existsActivityTypeforActivity(currentTour.getIndex(),0))
        {
        	// create attribute lookup
      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
        	
    	    // create step object
    	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
    	    
    	    // if number of working days is achieved, disable W as alternative
    	    if (
    	    		person.getAttributefromMap("anztage_w") <= pattern.countDaysWithSpecificActivity(ActivityType.WORK) &&
    	    		currentDay.getTotalNumberOfActivitites(ActivityType.WORK) == 0
    	    		)
    	    {
    	    	step.disableAlternative("W"); 
    	    }
    	    // if number of education days is achieved, disable E as alternative
    	    if (
    	    		person.getAttributefromMap("anztage_e") <= pattern.countDaysWithSpecificActivity(ActivityType.EDUCATION) &&
    	    		currentDay.getTotalNumberOfActivitites(ActivityType.EDUCATION) == 0
    	    		)
    	    {
    	    	step.disableAlternative("E"); 
    	    }
  	    
    	    // make selection
    	    step.doStep();
    	    ActivityType activityType = ActivityType.getTypeFromChar(step.getAlternativeChosen().charAt(0));
          
    	    if(debugloggers!= null && debugloggers.existsLogger(id))
    	    {
    	    	debugloggers.getLogger(id).put(currentTour, String.valueOf(activityType));
    	    }
          
  	    	HActivity activity = null;
  	    	
  	    	// if activity already exits, set activity type only
  	    	if (currentDay.existsActivity(currentTour.getIndex(),0))
          {
  	    		activity = currentTour.getActivity(0);
  	    		activity.setActivityType(activityType);
          }
  	    	// otherwise create activity and set activity type
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
    	// skip day if person is at home
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
        
      for (int i = currentDay.getLowestTourIndex(); i <= currentDay.getHighestTourIndex(); i++)
      {
      	HTour currentTour = currentDay.getTour(i);
      	
      	// create attribute lookup
    		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
      	
    	  // create step object
  	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
    		
  	    // initialize minimum number of activities
  	    int minimumnumberofactivities =0;
  	    
  	    // check if minimum number bound is already achieved
  	    if (currentDay.getTotalAmountOfActivitites() < numberofactsperday_lowerboundduetojointactions[currentDay.getIndex()])
  	    {
  	    	// calculate number of missing activities until bound is achieved
  	    	int remainingnumberofactivities = numberofactsperday_lowerboundduetojointactions[currentDay.getIndex()] - currentDay.getTotalAmountOfActivitites();
  		
  	    	/*
  	    	 * calculate number of remaining activity executions of step 5
  				 *
  	    	 * Equation: remainingnumberoftours * 2 (because 5A and 5B will be executed for each tour) - 1 (if this is step 5B and 5A is done)
  	    	 */
  	    	int reaminingstepexecutions =  2*(currentDay.getHighestTourIndex() - i + 1) - (id.equals("5B") ? 1 : 0); 
  	    	minimumnumberofactivities = Math.round(remainingnumberofactivities/reaminingstepexecutions);
  	    	// if this is the last tour of the day and step 5B, i.e., last executions of step 5 for the day, bound needs to be achieved
  	    	if (id.equals("5B") && currentTour.getIndex() == currentDay.getHighestTourIndex()) minimumnumberofactivities = remainingnumberofactivities;
  	    }
  	    
  	    // limit alternatives
  	    step.limitLowerBoundOnly(minimumnumberofactivities);
  	    
  	    // make selection
  	    step.doStep();    		

  	    if(debugloggers!= null && debugloggers.existsLogger(id))
  	    {
  	    	debugloggers.getLogger(id).put(currentTour, String.valueOf(step.getDecision()));
  	    }
  	    
  	    // create activities based on the decision and add them to the pattern
        for (int j = 1; j <= step.getDecision(); j++)
        {
        	HActivity act = null;
        	// 5A - activity before main activity
          if (id.equals("5A") && !currentDay.existsActivity(currentTour.getIndex(),-1*j)) act = new HActivity(currentTour, (-1) * j);
        	// 5B - activity after main activity
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
    	// skip day if person is at home
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
          	// create attribute lookup
        		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
          	
      	    // create step object
      	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
      	    
      	    // if number of working days is achieved, disable W as alternative
      	    if (
      	    		person.getAttributefromMap("anztage_w") <= pattern.countDaysWithSpecificActivity(ActivityType.WORK) &&
      	    		currentDay.getTotalNumberOfActivitites(ActivityType.WORK) == 0
      	    		)
      	    {
      	    	step.disableAlternative("W"); 
      	    }
      	    
      	    // if number of education days is achieved, disable E as alternative
      	    if (
      	    		person.getAttributefromMap("anztage_e") <= pattern.countDaysWithSpecificActivity(ActivityType.EDUCATION) &&
      	    		currentDay.getTotalNumberOfActivitites(ActivityType.EDUCATION) == 0
      	    		)
      	    {
      	    	step.disableAlternative("E"); 
      	    }
    	    
      	    //make selection
      	    step.doStep();

            // set activity type
      	    ActivityType activityType = ActivityType.getTypeFromChar(step.getAlternativeChosen().charAt(0));
      	    currentActivity.setActivityType(activityType);
      	    
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
	 * determination of default trip times for all activities
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
	 * Placing of joint activities (and trips) created by another household member into an existing pattern.
	 * Replaces an existing activity of the pattern with this joint activity
	 * 
	 * rule-based:
	 *  - if tour index of the originating activity exist, place it into this tour if possible
	 *  - if activity index of the originating activity exists, replace this activity if possible
	 *  
	 *  - if tour or activity in this tour does not exist, use the nearest exiting activity that is no joint activity
	 *  example: originating activity is 1/1/3 (day/tour/act). Highest index is 1/1/2, so use this for replacement
	 *  
	 *  - consider time overlaps. if list of joint activities is processed one by one, the last processed activity defines
	 *  	lower temporal bound for the new one.
	 *  
	 *  - joint activities of type 1 and type 3 starting at home are only possible as first activity in a tour
	 *   
	 * @throws InvalidPatternException
	 * 
	 */
	private void placeJointActivitiesIntoPattern() throws InvalidPatternException
	{
		
	 	List<HActivity> listjointact = person.getAllJointActivitiesforConsideration();
		HActivity.sortActivityListbyWeekStartTimes(listjointact);
	
		/*
		 * loop the list in week order and look for an existing activity to be replaced by activity actually processed
		 */
		
		for (int indexinliste=0 ; indexinliste < listjointact.size(); indexinliste++)
		{
			HActivity jointact = listjointact.get(indexinliste);
			
			int jointact_dayindey = jointact.getDayIndex();
			int jointact_tourindex = jointact.getTour().getIndex();
			int jointact_actindex = jointact.getIndex();
			JointStatus jointact_jointStatus = jointact.getJointStatus();
			
			assert JointStatus.JOINTELEMENTS.contains(jointact_jointStatus) : "keine gemeinsame Aktivit�t in der Liste der gemeinsamen Aktivit�ten!"; 
			
			
	  	/*
	  	 * possible activities for replacement
	  	 */
			List <HActivity> possibleact = new ArrayList<HActivity>();
			
			/*
			 *  Step 1: All available activities of the day
			 */
			{
	    	for (HActivity act : pattern.getDay(jointact_dayindey).getAllActivitiesoftheDay())
	    	{
	    		possibleact.add(act);
	    	}
	    	HActivity.sortActivityListbyIndices(possibleact);
			}
			
	  	/*
	  	 *  Step 2: Check if there are already joint activities on that day. If yes new joint activities need to be AFTER the last joint activity
	  	 */  
	  	{
	    	HActivity lastactreplaced=null;
	    	for (HActivity act : possibleact)
	    	{
	    		if ((act.getAttributefromMap("actreplacedbyjointact")!= null ? act.getAttributefromMap("actreplacedbyjointact") : 0) == 1.0) lastactreplaced=act;
	    	}
	    	if (lastactreplaced!=null)
	    	{
	    		List<HActivity> possibleactlaterinweek = new ArrayList<HActivity>();
	    		for (HActivity act : possibleact)
	    		{
	    			if (act.compareTo(lastactreplaced) < 0) possibleactlaterinweek.add(act);
	    		}
	    		possibleact = possibleactlaterinweek;
	    		
	    		/*
	    		 * - if the last joint act is not directly before the new activity, replace the first possible act for replacement to avoid temporal gaps
	    		 * - remove the first possible activity if this will cause time overlaps
	    		 */
	    		if ((lastactreplaced.getJointStatus()!=JointStatus.JOINTTRIP && 
	    				HActivity.getTimebetweenTwoActivities(lastactreplaced, jointact)!=0 && 
	    				!lastactreplaced.isActivityLastinTour())
	    				||
	    				(lastactreplaced.getJointStatus()!=JointStatus.JOINTTRIP && 
	    				HActivity.getTimebetweenTwoActivities(lastactreplaced, jointact)<0))	
	    			possibleact.remove(0);
	    	}
	  	}
	  	
	  	/*
	  	 * step 3: 	check for further joint activities on that day and remove the last X activities of that day for replacement 
	  	 */ 
	  	{
		  	int furtherjointactonday=0;
	    	for (int i=indexinliste+1; i<listjointact.size(); i++)
	    	{
	    		HActivity act = listjointact.get(i);
	    		if (act.getDayIndex()== jointact_dayindey) furtherjointactonday += 1;
	    	}
	    	if (furtherjointactonday>0)
	    	{
	    		for (int i=1; i<=furtherjointactonday; i++)
	    		{
	    			int letzterindex = possibleact.size()-1;
	    			possibleact.remove(letzterindex);
	    		}
	    	}
	  	}


	  	/*
	  	 * step 4: check if list is empty (because of rules in step 2 and 3)
	  	 */
	  	if (possibleact.size()==0) 
	  	{
	  		if (Configuration.debugenabled) System.err.println("could not replace activity! step 4");
	  		jointact.removeJointParticipant(person);
	  		break;
	  	}
	  	
	  	
	  	/*
	  	 * step 5: 	if joint type is 1 or 3, i.e., there is a joint trip included, the acitivity needs to be the first in the tour if the originating 
	  	 * 					one is the first on in the tour too (of the household member created the activity)
	  	 * 
	  	 * 					for such cases, only use the remaining activities that are the first one in their tour
	  	 */
	  	{
	    	if ((jointact_jointStatus==JointStatus.JOINTTRIPANDACTIVITY || jointact_jointStatus==JointStatus.JOINTTRIP) && jointact.isActivityFirstinTour())
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
	  	 * step 6: check if list is empty (because of rules in step 5)
	  	 */
	  	if (possibleact.size()==0) 
	  	{
	  		if (Configuration.debugenabled) System.err.println("could not replace activity! step 6");
	  		jointact.removeJointParticipant(person);
	  		break;
	  	}
	  	
	  	
	  	/*
	  	 * step 7:	check if tourindex of the originating activity still exists for replacement (first priority for replacement)
	  	 * 					if yes check the same for the activityindex
	  	 */
	  	{
	    	// add all activities with the same tour index into an own list
	    	List<HActivity> possibleactsametourindex = new ArrayList<HActivity>();
	    	for (HActivity act : possibleact)
	    	{
	    		if (act.getTour().getIndex() == jointact_tourindex)
	    		{
	    			possibleactsametourindex.add(act);
	    		}
	    	}
	    	// use this list for further processing if not empty
	    	if (possibleactsametourindex.size()!=0)
	    	{
	    		possibleact = possibleactsametourindex;
	    	
	    	// add all activities with the same act index into an own list
	    		List<HActivity> possibleactsameactindex = new ArrayList<HActivity>();
	      	for (HActivity act : possibleact)
	      	{
	      		if (act.getIndex() == jointact_actindex)
	      		{
	      			possibleactsameactindex.add(act);
	      		}
	      	}
	      	// use this list for further processing if not empty
	      	if (possibleactsameactindex.size()!=0)
	      	{
	      		possibleact = possibleactsameactindex;
	      	}
	    	}
	  	}
	  	
	  	/*
	  	 * step 8: if an activity is the last one of a tour and is followed directly by a joint activity, remove this activity from list as there is
	  	 * 				 no time left for the home activity
	  	 */
	  	{
	  		if (indexinliste < listjointact.size()-1 && HActivity.getTimebetweenTwoActivities(jointact,  listjointact.get(indexinliste+1))==0)
	  		{
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
	  	 * step 9: check if list is empty (because of rules in step 8)
	  	 */
	  	if (possibleact.size()==0) 
	  	{
	  		if (Configuration.debugenabled) System.err.println("could not replace activity! step 9");
	  		jointact.removeJointParticipant(person);
	  		break;
	  	}
	
	  	/*
	  	 * step 10: choose randomly on of the remaining activities
	  	 */
	  	int rnd = randomgenerator.getRandomValueBetween(0, possibleact.size()-1, 1);
	  	HActivity actforreplacement = possibleact.get(rnd);
	  	
	  	/*
	  	 * step 11: replace activity
	  	 */
	  	{
	    	// get activity properties
	    	int gemakt_duration = jointact.getDuration();
	    	int gemakt_starttime = jointact.getStartTime();
	    	ActivityType gemakt_acttype = jointact.getActivityType(); 		
	    	int gemakt_creatorPersonIndex = jointact.getCreatorPersonIndex();		
	    	
	    	int gemakt_durationtripbefore = jointact.getEstimatedTripTimeBeforeActivity();
	    	
	    	actforreplacement.addAttributetoMap("actreplacedbyjointact", 1.0);
	    	
	    	// replace different properties depending on type of joint action
	    	switch(jointact_jointStatus)
				{
					// activity and trip to the activities are done jointly
					case JOINTTRIPANDACTIVITY:
					{			
						// replace properties
						actforreplacement.setDuration(gemakt_duration);
						actforreplacement.setStartTime(gemakt_starttime);
						actforreplacement.setActivityType(gemakt_acttype);
						actforreplacement.setJointStatus(jointact_jointStatus);
						actforreplacement.setCreatorPersonIndex(gemakt_creatorPersonIndex); 
						
						// recalculate trip times
						actforreplacement.createTripsforActivity();
						
						// generate trip to the activity
						actforreplacement.setTripbeforeactivity(new HTrip(actforreplacement, TripStatus.TRIP_BEFORE_ACT, gemakt_durationtripbefore));
			
						break;
					}
					// only activity is done jointly
					case JOINTACTIVITY:
					{
						// replace properties
						actforreplacement.setDuration(gemakt_duration);
						actforreplacement.setStartTime(gemakt_starttime);
						actforreplacement.setActivityType(gemakt_acttype);
						actforreplacement.setJointStatus(jointact_jointStatus);
						actforreplacement.setCreatorPersonIndex(gemakt_creatorPersonIndex); 
						
						// recalculate trip times
						actforreplacement.createTripsforActivity();
						
						break;
					}		
					// only trip to the activity is done jointly
					case JOINTTRIP:
					{
					// replace properties
						actforreplacement.setJointStatus(jointact_jointStatus);
						actforreplacement.setCreatorPersonIndex(gemakt_creatorPersonIndex); 
						
						// generate trip
						actforreplacement.setTripbeforeactivity(new HTrip(actforreplacement, TripStatus.TRIP_BEFORE_ACT, gemakt_durationtripbefore));
						actforreplacement.setStartTime(gemakt_starttime);
						
						break;
					}
				default:
						throw new RuntimeException();
				}			
	  	}
	  	
	  	// step 12: check again for temporal overlaps
    	for (HActivity act : pattern.getDay(jointact_dayindey).getAllActivitiesoftheDay())
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
	
		
		//TODO  ensure order of the activities by index is identical with order by start time
		
	}



	/**
	 * 
	 * @param id
	 * @param variablenname
	 */
	private void executeStep7DC(String id, ActivityType activitytype)
	{
	  if (pattern.countActivitiesPerWeek(activitytype)>0)
	  {
			// create attribute lookup
			AttributeLookup lookup = new AttributeLookup(person);
			
	    // create step object
	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
	    step.doStep();
	    
	    if(debugloggers!= null && debugloggers.existsLogger(id))
	    {
	    	debugloggers.getLogger(id).put(person, String.valueOf(step.getDecision()));
	    }
	    
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
	  	if (pattern.countActivitiesPerWeek(activitytype)>0)
      {
        // get decision from step 7 DC
        double chosenIndex = person.getAttributefromMap(activitytype+"budget_category_index");

        WRDDefaultModelStep step = new WRDDefaultModelStep(id, String.valueOf((int) chosenIndex), activitytype, this);
        step.doStep();
        
        int chosenTime = step.getchosenDistributionElement();
        
  	    if(debugloggers!= null && debugloggers.existsLogger(id))
  	    {
  	    	debugloggers.getLogger(id).put(person, String.valueOf(chosenTime));
  	    }

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
    	// skip day if person is at home
    	if (currentDay.isHomeDay())
      {
      	continue;
      }
  	
      for (HTour currentTour : currentDay.getTours())
      {
        HActivity currentActivity = currentTour.getActivity(0);
        
        if(!currentActivity.durationisScheduled())
        {
        	// create attribute lookup
      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
        	
    	    // create step object
    	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
    	    step.doStep();
    	    
    	    if(debugloggers!= null && debugloggers.existsLogger(id))
    	    {
    	    	debugloggers.getLogger(id).put(currentActivity, String.valueOf(step.getAlternativeChosen()));
    	    }

    	    // save attribute for work and education activities if coordinated modeling is enabled 
    	    if (Configuration.coordinated_modelling && (currentActivity.getActivityType()==ActivityType.WORK || currentActivity.getActivityType()==ActivityType.EDUCATION))
    	    {
    	    	currentActivity.addAttributetoMap("standarddauer",(step.getAlternativeChosen().equals("yes") ? 1.0d : 0.0d));
    	    }
    	    else
    	    {
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
	  	// skip day if person is at home
	  	if (currentDay.isHomeDay())
	    {
	    	continue;
	    }

			for (HTour currentTour : currentDay.getTours())
			{
				boolean running=false;
				if (id_dc.equals("8B") && currentTour.isFirstTouroftheDay()) running=true;  // 8B for first tour of the day
				if (id_dc.equals("8D") && !currentTour.isFirstTouroftheDay()) running=true;	// 8D for all other tours
					
				if (running)
				{
	        HActivity currentActivity = currentTour.getActivity(0);
	        
	  	    /*
	  	     * 
	  	     * DC-step (8B, 8D)
	  	     * 
	  	     */

	        if (!currentActivity.durationisScheduled())
	        {
	          // create attribute lookup
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
	        	
	    	    // create step object
	    	    DCDefaultModelStep step_dc = new DCDefaultModelStep(id_dc, this, lookup);
	    	    
	    	    // limit alternatives if needed
	    	    if (currentActivity.getAttributesMap().get("standarddauer") == 1.0d)
	    	    {
	    	      int timeCategory = currentActivity.calculateMeanTimeCategory();
	    	      
		    	    if(debugloggers!= null && debugloggers.existsLogger("meantime"))
		    	    {
		    	    	debugloggers.getLogger("meantime").put(currentActivity, String.valueOf(timeCategory));
		    	    }
	    	      	
	    	      // lower bound minimum is 0
	    	      int from = Math.max(timeCategory - 1,0);
	    	      // upper bound maximum is last time category
	    	      int to = Math.min(timeCategory + 1,Configuration.NUMBER_OF_ACT_DURATION_CLASSES-1);
	    	        
	    	      step_dc.limitUpperandLowerBound(from, to);
	    	      // add utility bonus of 10% to average time class (middle of the 3 selected)
	    	      step_dc.adaptUtilityFactor(timeCategory, 1.1);
	    	    } 	    
	    	    
	    	    // set durations bound because of other determined activities
	    	    int[] durationBounds = calculateDurationBoundsDueToOtherActivities(currentActivity);   
	    	    int loc_lowerbound = getDurationTimeClassforExactDuration(durationBounds[0]);
	    	    int loc_upperbound = getDurationTimeClassforExactDuration(durationBounds[1]);
	    	    
	    	    assert loc_lowerbound<=loc_upperbound;

	    	    // if bounds are identical, duration is set
	    	    if (loc_lowerbound==loc_upperbound)
	    	    {
	    	    	step_dc.limitUpperandLowerBound(loc_lowerbound, loc_upperbound);
	    	    }
	    	    else
	    	    {   	    
		    	    // limit upper bound if not yet set or below old upper bound
		    	    if (loc_upperbound <= step_dc.getUpperBound() || step_dc.getUpperBound()==-1) step_dc.limitUpperBoundOnly(loc_upperbound); 
	    	    
		    	    // limit lower bound if higher than old lower bound
		    	    if (loc_lowerbound >= step_dc.getLowerBound()) step_dc.limitLowerBoundOnly(loc_lowerbound);   
		    	    
		    	    // limit lower bound if bound is now higher than upper bound
		    	    if (step_dc.getLowerBound() >= step_dc.getUpperBound()) step_dc.limitLowerBoundOnly(step_dc.getUpperBound());   
	    	    }

	    	    assert step_dc.getLowerBound()<=step_dc.getUpperBound();
   	    		
	    	    // make selection
	    	    step_dc.doStep();

	    	    if(debugloggers!= null && debugloggers.existsLogger(id_dc))
	    	    {
	    	    	debugloggers.getLogger(id_dc).put(currentActivity, String.valueOf(step_dc.getDecision()));
	    	    }

	    	    currentActivity.addAttributetoMap("actdurcat_index",(double) step_dc.getDecision()); 	
	    	    
	    	    /*
	    	     * 
	    	     * WRD-step (8C, 8E)
	    	     * 
	    	     */
          	// initialize object based on chosen time category
			      double chosenTimeCategory = currentActivity.getAttributesMap().get("actdurcat_index");
			      WRDDefaultModelStep step_wrd = new WRDDefaultModelStep(id_wrd, String.valueOf((int) chosenTimeCategory), currentActivity.getActivityType(), this);
			      			      
			      step_wrd.setRangeBounds(durationBounds[0], durationBounds[1]);
			      
			      if (currentActivity.getAttributesMap().get("standarddauer") == 1.0d) step_wrd.setModifydistribution(true);
			      
			      // make selection
			      step_wrd.doStep();
			      int chosenTime = (int) step_wrd.getchosenDistributionElement();
			      		      
	    	    if(debugloggers!= null && debugloggers.existsLogger(id_wrd))
	    	    {
	    	    	debugloggers.getLogger(id_wrd).put(currentActivity, String.valueOf(chosenTime));
	    	    }
			     
			      currentActivity.setDuration(chosenTime);
			      
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
	  	// skip day if person is at home
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
	  	     * DC-step
	  	     * 
	  	     */    		
	        
	        if (currentActivity.getIndex() != 0 && !currentActivity.durationisScheduled())
	        {   	     
	          // create attribute lookup
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
	        	
	    	    // create step object
	    	    DCDefaultModelStep step_dc = new DCDefaultModelStep(id_dc, this, lookup);
	    	   
	    	    // limit bounds because of determined durations
	    	    int[] durationBounds = calculateDurationBoundsDueToOtherActivities(currentActivity);   
	    	    int loc_lowerbound = getDurationTimeClassforExactDuration(durationBounds[0]);
	    	    int loc_upperbound = getDurationTimeClassforExactDuration(durationBounds[1]);
	    	    
	    	    assert loc_lowerbound<=loc_upperbound;

	    	    // duration is set if bounds are identical
	    	    if (loc_lowerbound==loc_upperbound)
	    	    {
	    	    	step_dc.limitUpperandLowerBound(loc_lowerbound, loc_upperbound);
	    	    }
	    	    else
	    	    {   	    
	    	    	// limit upper bound if not yet set or below old upper bound
		    	    if (loc_upperbound <= step_dc.getUpperBound() || step_dc.getUpperBound()==-1) step_dc.limitUpperBoundOnly(loc_upperbound); 
	    	    
		    	    // limit lower bound if higher than old lower bound
		    	    if (loc_lowerbound >= step_dc.getLowerBound()) step_dc.limitLowerBoundOnly(loc_lowerbound);   
		    	    
		    	    // limit lower bound if bound is now higher than upper bound
		    	    if (step_dc.getLowerBound() >= step_dc.getUpperBound()) step_dc.limitLowerBoundOnly(step_dc.getUpperBound());   
	    	    }
	    	    		
	    	    assert step_dc.getLowerBound()<=step_dc.getUpperBound();

	    	    // make selection
	    	    step_dc.doStep();
	    	    
	    	    if(debugloggers!= null && debugloggers.existsLogger(id_dc))
	    	    {
	    	    	debugloggers.getLogger(id_dc).put(currentActivity, String.valueOf(step_dc.getDecision()));
	    	    }
	
	    	    currentActivity.addAttributetoMap("actdurcat_index",(double) step_dc.getDecision()); 	
	    	    
	    	    /*
	    	     * 
	    	     * WRD-step
	    	     * 
	    	     */
	    	    // initialize object based on chosen time category
          	double chosenTimeCategory = currentActivity.getAttributesMap().get("actdurcat_index");
  		      WRDDefaultModelStep step_wrd = new WRDDefaultModelStep(id_wrd, String.valueOf((int) chosenTimeCategory), currentActivity.getActivityType(), this);
  		     
  		      step_wrd.setRangeBounds(durationBounds[0], durationBounds[1]);
			      
			      // make selection
  		      step_wrd.doStep();
			      int chosenTime = (int) step_wrd.getchosenDistributionElement();

			      if(debugloggers!= null && debugloggers.existsLogger(id_wrd))
	    	    {
	    	    	debugloggers.getLogger(id_wrd).put(currentActivity, String.valueOf(chosenTime));
	    	    }
			     
			      currentActivity.setDuration(chosenTime);
  		      
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
    	 // create attribute lookup
  		AttributeLookup lookup = new AttributeLookup(person);   	
    	
	    // create step object
	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
	    step.doStep();

	    if(debugloggers!= null && debugloggers.existsLogger(id))
	    {
	    	debugloggers.getLogger(id).put(person, String.valueOf(step.getDecision()));
	    }

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
	      HTour currentTour = currentDay.getFirstTourOfDay();
	    	ActivityType tourtype = currentTour.getActivity(0).getActivityType();
	      if (tourtype == ActivityType.WORK || tourtype == ActivityType.EDUCATION)
	      {
	      	// create attribute lookup
	    		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
	      	
	  	    // create step object
	  	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
	  	    step.doStep();
	  	    
	  	    if(debugloggers!= null && debugloggers.existsLogger(id))
	  	    {
	  	    	debugloggers.getLogger(id).put(currentTour, String.valueOf(step.getAlternativeChosen()));
	  	    }

	  	    currentTour.addAttributetoMap("default_start_cat_yes",(step.getAlternativeChosen().equals("yes") ? 1.0d : 0.0d));
	      }
	    }
	  }
	}



	/**
	 * determine tour start times for tours where start time is known because of joint activities originating from other household members
	 * 
	 * @throws InvalidPatternException 
	 * 
	 */
	private void createTourStartTimesDueToScheduledActivities() throws InvalidPatternException
	{
		for (HDay currentDay : pattern.getDays())
	  {
	    if (currentDay.isHomeDay())
	    {
	    	continue;
	    }
	  	
	    for (HTour currentTour : currentDay.getTours())
	    {
		  	if (!currentTour.isScheduled())
		    {
		  		int startTimeDueToScheduledActivities=99999;
		  		
	  			int tripdurations=0;
	  			int activitydurations=0;
	  			
		  		HActivity.sortActivityListbyIndices(currentTour.getActivities());
		  		for (HActivity tmpact : currentTour.getActivities())
		  		{
		  			/*
		  			 * if start time of an activity is determined, set this as fixed element and subtract all activity and trip durations until then
		  			 */		  			
		  			if (tmpact.startTimeisScheduled())
		  			{
		  				startTimeDueToScheduledActivities= tmpact.getTripStartTimeBeforeActivity() - tripdurations - activitydurations;
		  				break;
		  			}
		  			/*
		  			 * otherwise add activity and trip times
		  			 */
		  			else
		  			{
		  				tripdurations += tmpact.getEstimatedTripTimeBeforeActivity();
		  				activitydurations += tmpact.getDuration();
		  			}
		  		}
		  		
		  		/*
		  		 * there may be negative tour start times because of other determined activities in rare cases
		  		 * example: the activity is generated by another person and close to midnight. if the actual person has another commuting duration
		  		 * the start time (leaving the house) for this tour may be negative!
		  		 */
		  		if (startTimeDueToScheduledActivities<0)
		  		{
		  			throw new InvalidPatternException("Person", pattern, "TourStartTimes <0 " + currentTour);
		  		}
		  		
		  		if (startTimeDueToScheduledActivities!=99999)
		  		{
		  			currentTour.setStartTime(startTimeDueToScheduledActivities);   
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
	    if (currentDay.isHomeDay()|| currentDay.getAmountOfTours()<tournrdestages)
	    {
	    	continue;
	    }
	  	
	    HTour currentTour = currentDay.getTour(currentDay.getLowestTourIndex()+(tournrdestages-1));
	  	
	  	if (!currentTour.isScheduled())
	    {
			
				/*
				 * 
				 * DC-step
				 * 
				 */
			
	  		// create attribute lookup
	  		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
	    	
		    // create step object
		    DCDefaultModelStep step_dc = new DCDefaultModelStep(id_dc, this, lookup);
		     		
	      // limit alternatives
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
		    
		    // make selection
		    step_dc.doStep();
		    
  	    if(debugloggers!= null && debugloggers.existsLogger(id_dc))
  	    {
  	    	debugloggers.getLogger(id_dc).put(currentTour, String.valueOf(step_dc.getDecision()));
  	    }

		    currentTour.addAttributetoMap("tourStartCat_index",(double) step_dc.getDecision());
		    
		    
		    /*
		     * 
		     * WRD-step
		     * 
		     */

	      double chosenStartCategory = (double) currentTour.getAttributesMap().get("tourStartCat_index");
	      WRDDefaultModelStep step_wrd = new WRDDefaultModelStep(id_wrd, String.valueOf((int)chosenStartCategory), currentTour.getActivity(0).getActivityType(), this);
	      
	      int[] bounds_mc = calculateStartingBoundsForTours(currentTour, false);
	      step_wrd.setRangeBounds(bounds_mc[0], bounds_mc[1]);
	      
	      // make selection
	      step_wrd.doStep();
	      int chosenStartTime = step_wrd.getchosenDistributionElement();
	      
  	    if(debugloggers!= null && debugloggers.existsLogger(id_wrd))
  	    {
  	    	debugloggers.getLogger(id_wrd).put(currentTour, String.valueOf(chosenStartTime));
  	    }
	      
	      currentTour.setStartTime(chosenStartTime);   	  	
	      currentTour.createStartTimesforActivities();
	      
//TODO previousTour needs to be scheduled if modeling is chronological 
	      
	      // ensure that there are no temporal overlaps
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
	    	// determine home time for all non-scheduled tours
	      if (!currentTour.isScheduled())
	      {
	      	// 10S
	      	      	
        	// create attribute lookup
      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour);   	
        	
    	    // create step object
    	    DCDefaultModelStep dcstep = new DCDefaultModelStep("10S", this, lookup);
    	     		
          // limit alternatives
          int dcbounds[] = calculateBoundsForHomeTime(currentTour, true);
    	    int lowerbound = dcbounds[0];
    	    int upperbound = dcbounds[1];
    	    dcstep.limitUpperandLowerBound(lowerbound, upperbound);
    	    
    	    // make selection
    	    dcstep.doStep();

    	    if(debugloggers!= null && debugloggers.existsLogger("10S"))
    	    {
    	    	debugloggers.getLogger("10S").put(currentTour, String.valueOf(dcstep.getDecision()));
    	    }

    	    int chosenHomeTimeCategory = dcstep.getDecision();
      	
    	    // 10T

    	    WRDDefaultModelStep step_wrd = new WRDDefaultModelStep("10T", String.valueOf((int)chosenHomeTimeCategory), currentTour.getActivity(0).getActivityType(), this);
    	    int[] wrdbounds = calculateBoundsForHomeTime(currentTour, false);
          step_wrd.setRangeBounds(wrdbounds[0], wrdbounds[1]);
          
          // make selection
          step_wrd.doStep();
          int chosenTime = step_wrd.getchosenDistributionElement();
          
    	    if(debugloggers!= null && debugloggers.existsLogger("10T"))
    	    {
    	    	debugloggers.getLogger("10T").put(currentTour, String.valueOf(chosenTime));
    	    }

          int starttimetour = currentDay.getTour(currentTour.getIndex()-1).getEndTime() + chosenTime;
          currentTour.setStartTime(starttimetour);
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
	  	// skip day if person is at home
	  	if (currentDay.isHomeDay())
	    {
	    	continue;
	    }
	    
	    for (HTour currentTour : currentDay.getTours())
	    {
	      for (HActivity currentActivity : currentTour.getActivities())
	      {
	      	/* 
	      	 * skip the activity if activity was generated by another household member
	      	 */
	      	if (currentActivity.getCreatorPersonIndex() != person.getPersIndex())
	      	{
	      		continue;
	      	}
	      	
	      	/*
	    		 * if person is the last one modeled in the household, no other members are available to join activities. Thus, skip decision then.
	    		 */
	    		if ((int) person.getAttributefromMap("numbermodeledinhh") != person.getHousehold().getNumberofPersonsinHousehold())
	    		{
	        	// create attribute lookup
	      		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, currentActivity);   	
	        	
	    	    // create step object
	    	    DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
	    	    step.doStep();
	    	    
      	    if(debugloggers!= null && debugloggers.existsLogger(id))
      	    {
      	    	debugloggers.getLogger(id).put(currentActivity, String.valueOf(step.getAlternativeChosen()));
      	    }
	
	    	    currentActivity.setJointStatus(JointStatus.getTypeFromInt(Integer.parseInt(step.getAlternativeChosen())));
	    		}
	  	    else
	  	    {
	    	    currentActivity.setJointStatus(JointStatus.NOJOINTELEMENT);
	  	    }
	      }
	    }
	  }
	}



	/**
	 * 
	 * detailed modeling of activity purposes
	 * 
	 * @param activity
	 * @param id
	 */
	private void executeStep98(HActivity activity, String id)
	{ 
		HDay currentDay = activity.getDay();
		HTour currentTour = activity.getTour();
	
		// create attribute lookup
		AttributeLookup lookup = new AttributeLookup(person, currentDay, currentTour, activity);   	
		
	  // create step object
	  DCDefaultModelStep step = new DCDefaultModelStep(id, this, lookup);
	  step.doStep();

    if(debugloggers!= null && debugloggers.existsLogger(id))
    {
    	debugloggers.getLogger(id).put(activity, String.valueOf(step.getAlternativeChosen()));
    }
	
	  int chosenActivityType = Integer.parseInt(step.getAlternativeChosen());
	  activity.setMobiToppActType((byte) chosenActivityType);          
	}

   

	
	/**
	 * determines lower and upper bound for activity durations due to other planned activities
	 * 
	 * @param act
	 * @return [0] = lower bound [1] = upper bound
	 * @throws InvalidPatternException
	 */
	private int[] calculateDurationBoundsDueToOtherActivities(HActivity act) throws InvalidPatternException
	{
		HDay dayofact = act.getDay();
		
		/*
		 * main idea of lower bound calculation
		 * 
		 * 1. starting point (descending priority)
		 * - There is another activity earlier this day having a determined starting time
		 * - The last activity of the previous day ends after midnight
		 * - begin of the day (1 minute past midnight to allow buffer for home time)
		 * 
		 * 2. Calculate all activity durations between actual activity and starting point
		 * 3. Calculate all trip durations between actual activity and starting point
		 * 4. Calculate home time buffers for all tours between between actual activity and starting point 
		 * 
		 * 
		 * main idea of upper bound calculation
		 * 
		 * 1. starting point (descending priority)
		 * - There is another activity later this day having a determined starting time
		 * - end of the day
		 * 
		 * step 2-4 identical
		 */
		
		
		/*
		 * 1.
		 * 
		 * starting points
		 * 
		 */
		HActivity last_act_scheduled = null;
		HActivity next_act_scheduled = null;

		for (HActivity tmpact : dayofact.getAllActivitiesoftheDay())
		{
			
			// Search for earlier activity with determined starting time
			if(act.compareTo(tmpact)==-1)	
			{
				//System.out.println(tmpact.getTour().getIndex() + "/" + tmpact.getIndex());
				if(tmpact.startTimeisScheduled() && (last_act_scheduled==null || tmpact.getStartTime()>last_act_scheduled.getStartTime())) last_act_scheduled = tmpact;
			}	
			
			// Search for later activity with determined starting time
			if(act.compareTo(tmpact)==+1)
			{
				//System.out.println(tmpact.getTour().getIndex() + "/" + tmpact.getIndex());
				if(tmpact.startTimeisScheduled() && (next_act_scheduled==null || tmpact.getStartTime()<next_act_scheduled.getStartTime())) next_act_scheduled = tmpact;
			}	
		}
		
		/*
		 * starting point for lower bound
		 */
		
		int startingpointlowerbound=1;
		if (last_act_scheduled!=null)
		{
			startingpointlowerbound = last_act_scheduled.getStartTime() + (last_act_scheduled.durationisScheduled() ?  last_act_scheduled.getDuration() : last_act_scheduled.getDefaultActivityTime()); 
		}
		else
		{
			// check if last activity of the previous day ends after midnight
			HDay previousDay = dayofact.getPreviousDay();
			if (previousDay!=null && !previousDay.isHomeDay())
			{
				HActivity lastactpreviousday = previousDay.getLastTourOfDay().getLastActivityInTour();
				if (lastactpreviousday.startTimeisScheduled())
				{
					int endlastactpreviousday = lastactpreviousday.getStartTime() +
							(lastactpreviousday.durationisScheduled() ? lastactpreviousday.getDuration() : 0) + 
							(lastactpreviousday.tripAfterActivityisScheduled() ? lastactpreviousday.getEstimatedTripTimeAfterActivity() : 0);
					if (endlastactpreviousday>1440) 
					{
						// +1 to allow at least one minute home time
						startingpointlowerbound = endlastactpreviousday-1440+1;
					}
				}
			}
		}
	
		/*
		 * starting point for upper bound
		 */	

		int startingpointupperbound=0;
		if (next_act_scheduled!=null)
		{
			startingpointupperbound = next_act_scheduled.getStartTime(); 
		}
		/*
		 * Otherwise, the upper bound starts at 3am or with the first planned activity the next day until 3am
		 */
		else
		{
			startingpointupperbound = 1620;
			
			HDay nextday = dayofact.getNextDay();
			if (nextday!=null && !nextday.isHomeDay())
			{
				HActivity firstactnextday = nextday.getFirstTourOfDay().getFirstActivityInTour();
				if (firstactnextday.startTimeisScheduled())
				{
					int startingtimefirstactnextday = firstactnextday.getTripStartTimeBeforeActivity();
					if (startingtimefirstactnextday<180) 
					{
						// -1  to allow at least one minute home time
						startingpointupperbound = 1440 + startingtimefirstactnextday -1;
					}
				}
			}
	
		}
	
		
		/*
		 * 2.
		 * 
		 * calculate activity durations
		 * 
		 */
		
		int activitydurationsincelastscheduled = countActivityDurationsbetweenActivitiesofOneDay(last_act_scheduled, act);
		int activitydurationuntilnextscheduled = countActivityDurationsbetweenActivitiesofOneDay(act, next_act_scheduled);
		
		/*
		 * 3.
		 * 
		 * calculate trip durations
		 * 
		 */
		
		int tripdurationssincelastscheduled = countTripDurationsbetweenActivitiesofOneDay(last_act_scheduled, act);
		int tripdurationsuntilnextscheduled = countTripDurationsbetweenActivitiesofOneDay(act, next_act_scheduled);
	
		/*
		 * 4.
		 * 
		 * calculate home time buffers (1 minute for each tour)
		 * 
		 */

		/*
		 * Before
		 */
		int timeforhomeactsincelastscheduled=0;		
	  if (last_act_scheduled==null)
		{
	  	timeforhomeactsincelastscheduled += (act.getTour().getIndex() - act.getDay().getLowestTourIndex());
		}
		else
		{
			timeforhomeactsincelastscheduled += (act.getTour().getIndex() - last_act_scheduled.getTour().getIndex());
		}
	  
	  /*
	   * Afters
	   */
		int timeforhomeactuntilnextscheduled=0;
	  if (next_act_scheduled==null)
		{
	  	timeforhomeactuntilnextscheduled += (act.getDay().getHighestTourIndex() - act.getTour().getIndex());
		}
		else
		{
			timeforhomeactuntilnextscheduled += (next_act_scheduled.getTour().getIndex() - act.getTour().getIndex());
		}
		
		/*
		 * 5.
		 * 
		 * calculate bound and maximum durations
		 * 
		 */	
		
		int lowerbound = startingpointlowerbound + activitydurationsincelastscheduled + tripdurationssincelastscheduled + timeforhomeactsincelastscheduled;
		int upperbound = startingpointupperbound - activitydurationuntilnextscheduled - tripdurationsuntilnextscheduled - timeforhomeactuntilnextscheduled;
		
		/*
		 * if activity already has a determined starting time, this is the lower bound
		 */
		if (act.startTimeisScheduled()) lowerbound = act.getStartTime();
		
		int maxduration = upperbound - lowerbound;
		int minduration = 1;
		
    // set maximum to one day if upper bound exceeds one day
    maxduration = Math.min(maxduration,1440);
       
    // error handling if upper bound <= lower bound 
    if (upperbound<=lowerbound)
    {
    	// household exception as conflict results from joint act of other household members
    	if (next_act_scheduled!= null && next_act_scheduled.getCreatorPersonIndex()!=person.getPersIndex() && 
    			last_act_scheduled!=null && last_act_scheduled.getCreatorPersonIndex()!=person.getPersIndex())
    	{
    		String errorMsg = "Duration Bounds incompatible Act" + act.getDayIndex() + "/" + act.getTour().getIndex() + "/" + act.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
    		throw new InvalidPatternException("Household",pattern, errorMsg);
    	}
    	// household exception as conflict results from the person itself
    	else
    	{
    		String errorMsg = "Duration Bounds incompatible Act " + act.getDayIndex() + "/" + act.getTour().getIndex() + "/" + act.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
    		throw new InvalidPatternException("Person",pattern, errorMsg);
    	}
    }
    
    /*
     * check if previous and following activity in this tour are already determined.
     * If so, duration and thus bounds are fixed.
     */
    if(!act.isActivityFirstinTour() && !act.isActivityLastinTour())
    {
    	HActivity lastact = act.getPreviousActivityinTour();
    	HActivity nextact = act.getNextActivityinTour();
    	
    	if (lastact.startTimeisScheduled() && lastact.durationisScheduled() && nextact.startTimeisScheduled())
    	{
    		minduration = maxduration;
    	}
    }
    /*
     * check if activity itself and following activity in this tour have a determined starting time.
     * If so, duration and thus bounds are fixed.
     */
    if (act.startTimeisScheduled() && !act.isActivityLastinTour())
    {
    	if (act.getNextActivityinTour().startTimeisScheduled()) minduration=maxduration;
    }
    
    
    /*
     * return bounds
     */
		
    int[] durationBounds = new int[2];
    durationBounds[0] = minduration;
    durationBounds[1] = maxduration;
    
		return durationBounds;
	}
	
	
	/**
	 * 
	 * reverse determination of a time class based on an exact value
	 * 
	 * @param maxduration
	 * @return
	 */
	private int getDurationTimeClassforExactDuration (int maxduration)
	{
    int timeClass=-1;
    for (int i = 0; i < Configuration.NUMBER_OF_ACT_DURATION_CLASSES; i++)
    {
        if (maxduration >= Configuration.ACT_TIME_TIMECLASSES_LB[i] && maxduration <= Configuration.ACT_TIME_TIMECLASSES_UB[i])
        {
        	timeClass = i;
        }
    }  
    assert timeClass!=-1 : "could not determine timeclass!";
    return timeClass;
	}
	
	/**
	 * calculates activity durations between two activities
	 * 
	 * @param actfrom
	 * @param actto
	 * @return
	 */
	private int countActivityDurationsbetweenActivitiesofOneDay(HActivity actfrom, HActivity actto) 
	{
		int result=0;
		List<HActivity> listofdayactivities;
		if (actfrom==null)
		{
			listofdayactivities = actto.getDay().getAllActivitiesoftheDay();
		}
		else 
		{
			listofdayactivities = actfrom.getDay().getAllActivitiesoftheDay();
		}
		
		for (HActivity tmpact : listofdayactivities)
		{
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
	 * calculates trip durations between two activities
	 * 
	 * @param actfrom
	 * @param actto
	 * @return
	 */
	private int countTripDurationsbetweenActivitiesofOneDay(HActivity actfrom, HActivity actto) 
	{
		int result=0;		
		List<HActivity> listofdayactivities;
		if (actfrom==null)
		{
			listofdayactivities = actto.getDay().getAllActivitiesoftheDay();
		}
		else 
		{
			listofdayactivities = actfrom.getDay().getAllActivitiesoftheDay();
		}
		
		for (HActivity tmpact : listofdayactivities)
		{
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
	 * calculates lower and upper bound for tour starting times based on other determined starting times and durations
	 * 
	 * @param categories decides returning categories or exact values
	 * @param tour
	 * @return
	 * @throws InvalidPatternException
	 */
	private int[] calculateStartingBoundsForTours(HTour tour, boolean categories) throws InvalidPatternException
	{
		
		/*
		 * main idea of lower bound calculation
		 * 
		 * 1. starting point (descending priority)
		 * - The tour is not the last tour of the day -> ending time of the previous tour
		 * - The last activity of the previous day ends after midnight
		 * - begin of the day (1 minute past midnight to allow buffer for home time)
		 * 
		 * 
		 * main idea of upper bound calculation
		 * 
		 * 1. starting point (descending priority)
		 * - There is another tour later this day having a determined starting time
		 * - There is another tour scheduled until 3am the next day
		 * - "end of the day" (3am next day)
		 * 
		 * 2. subtract all planned tours including activity and trip durations until upper bound starting point
		 * 3. buffer for home times
		 */
					  
	  HDay tourday = tour.getDay();
	 
	  int lowercat = -1;
	  int uppercat = -1; 
	  
		/*
		 * 
		 * lower bound
		 * 
		 */

	  int startingpointlowerbound = 1;
	 
	  
	  // if this is not the first tour, starting point is the end of the previous tour
	  if (tour.getIndex() != tourday.getLowestTourIndex())
	  {
	  	startingpointlowerbound = tourday.getTour(tour.getIndex()-1).getEndTime() + 1;
	  }
	  else
	  {
			HDay previousDay = tourday.getPreviousDay();
			if (previousDay!=null && !previousDay.isHomeDay())
			{
				HActivity lastactpreviousday = previousDay.getLastTourOfDay().getLastActivityInTour();
				if (lastactpreviousday.startTimeisScheduled())
				{
					int endlastactpreviousday = lastactpreviousday.getStartTime() +
							(lastactpreviousday.durationisScheduled() ? lastactpreviousday.getDuration() : 0) + 
							(lastactpreviousday.tripAfterActivityisScheduled() ? lastactpreviousday.getEstimatedTripTimeAfterActivity() : 0);
					if (endlastactpreviousday>1440) 
					{
						// +1 to allow at least one minute home time
						startingpointlowerbound = endlastactpreviousday-1440+1;
					}
				}
			}
	  }
	  

		/*
		 * 
		 * upper bound
		 * 
		 */

	  /*
	   * 1. starting point
	   */
	  int startingpointupperbound = 1440;
	  HTour nexttourscheduled=null;

	  for (int i = tour.getIndex()+1; i <= tourday.getHighestTourIndex(); i++)
	  {
	  	HTour tmptour = tourday.getTour(i);
	  	if (tmptour.isScheduled())
	  	{
	  		nexttourscheduled=tmptour;
	  		startingpointupperbound = tmptour.getStartTime();
	  		break;
	  	}
	  }
	  if (nexttourscheduled==null)
	  {
	  	HDay nextday = tourday.getNextDay();
	  	if (nextday!=null && !nextday.isHomeDay())
			{
				HActivity firstactnextday = nextday.getFirstTourOfDay().getFirstActivityInTour();
				if (firstactnextday.startTimeisScheduled())
				{
					int startfirstactnextday = firstactnextday.getStartTime() -
							(firstactnextday.tripBeforeActivityisScheduled() ? firstactnextday.getEstimatedTripTimeBeforeActivity() : 0);
					if (startfirstactnextday<(startingpointupperbound-1440)) 
					{
						startingpointupperbound = 1439 + startfirstactnextday;
					}
				}
			}	  						
	  }
	  
	  /*
	   * 2. activity and trip durations until upper bound starting point 
	   * 3. home time buffers
	   */
	  int tmptourdurations = 0;
	  int hometimebuffer = 0;
	  int tourindexforsearch;

	  if(nexttourscheduled!=null)
	  {
	  	tourindexforsearch = nexttourscheduled.getIndex()-1;
	  }
	  else
	  {
	  	tourindexforsearch = tourday.getHighestTourIndex();
	  }
	  for (int i = tour.getIndex(); i <= tourindexforsearch; i++)
	  {
	  	HTour tmptour = tourday.getTour(i);
	  	tmptourdurations += tmptour.getTourDuration();
	  	
	  	hometimebuffer += 1;
	  }
	  
	  
	  /*
	   * 
	   * calculate bound and get categories if needed
	   * 
	   */
	  
	  int lowerbound = startingpointlowerbound;
	  int upperbound = startingpointupperbound - tmptourdurations - hometimebuffer;
	  
	  // limit upper bound to 1439 as no starting times after midnight are possible
	  if (upperbound>1439) upperbound=1439;
	  
	        
	  // error handling
	  if (upperbound<lowerbound)
	  {
	  	String errorMsg = "TourStartTimes Tour " + tourday.getIndex() + "/" + tour.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	  	throw new InvalidPatternException("Person",pattern, errorMsg);
	  }
	
	  
	  // time classes for first tours of the day
	  if(categories && tour.getIndex()== tourday.getLowestTourIndex())
	  {
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
	
	  // time classes for all other tours of the day
	  if(categories && tour.getIndex()!= tourday.getLowestTourIndex())
	  {
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
	          
	  // error handling for non existing categories
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
	 * calculate bounds for home time
	 * 
	 * @param day
	 * @param tour
	 * @param categories decides returning categories or exact values
	 * @return
	 * @throws InvalidPatternException
	 */
	private int[] calculateBoundsForHomeTime(HTour tour, boolean categories) throws InvalidPatternException
	{
		HDay tourday = tour.getDay();
		
	  int lowerbound = 1;
	  int upperbound = -1;
	  
	  int lowercat = -1;
	  int uppercat = -1;   
	  
	  int starttime_nexttourscheduled = 1620;
	  	  
	  // get upper bound based on starting times later this day
	  int tmptourdurations = 0;
	  for (int i = tour.getIndex(); i <= tourday.getHighestTourIndex(); i++)
	  {
	  	HTour tmptour = tourday.getTour(i);
	  	
	  	// use the first scheduled tour as starting point to calculate
	  	if (tmptour.isScheduled())
	  	{
	  		starttime_nexttourscheduled = tmptour.getStartTime();
	  		break;
	  	}
	  	else
	  	{
	  		// +1 to allow at least one minute home time
	  		tmptourdurations += tmptour.getTourDuration() + 1;
	  	}
	  }
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
	  
	  upperbound = starttime_nexttourscheduled - tmptourdurations - tourday.getTour(tour.getIndex()-1).getEndTime();
	  if (upperbound>1439) upperbound=1439;
	
	  // error handling
	  if (upperbound<lowerbound)
	  {
	  	String errorMsg = "HomeTime Tour " + tour.getIndex() + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")";
	  	throw new InvalidPatternException("Person", pattern, errorMsg);
	  }
	
	  if(categories)
	  {
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
	          
	  // error handling
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
   * select possible other household members as participants for a joint activity 
   */
	private void selectWithWhomforJointActions() 
	{

		for (HActivity tmpactivity : pattern.getAllOutofHomeActivities()) 
		{
			/*
			 * consider activity if activity has a joint status and was created by the person itself  
			 */
			if (tmpactivity.getJointStatus()!=JointStatus.NOJOINTELEMENT && tmpactivity.getCreatorPersonIndex()==person.getPersIndex()) 
			{
				
				Map<Integer,ActitoppPerson> otherunmodeledpersinhh = new HashMap<Integer, ActitoppPerson>();
				// first add all other household members
				otherunmodeledpersinhh.putAll(person.getHousehold().getHouseholdmembers());				
				// delete all members that are already modeled or the person itself
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
					// decide number of household members joining the activity
					int numberofadditionalmembers=99;
					double randomvalue = randomgenerator.getRandomValue();
					int hhgro = person.getHousehold().getNumberofPersonsinHousehold();
					
					/*
					 * probabilities are calculated using MOP data
					 */
					if (hhgro==2)
					{
						numberofadditionalmembers=1;
					}
					if (hhgro==3)
					{
						if (randomvalue <  0.75) numberofadditionalmembers=1;
						if (randomvalue >= 0.75) numberofadditionalmembers=2;
					}
					if (hhgro==4)
					{
						if (0 	 <= randomvalue && randomvalue < 0.73) 	numberofadditionalmembers=1;
						if (0.73 <= randomvalue && randomvalue < 0.89) 	numberofadditionalmembers=2;
						if (0.89 <= randomvalue && randomvalue <= 1) 		numberofadditionalmembers=3;
					}
					if (hhgro>=5)
					{
						if (0 	 <= randomvalue && randomvalue < 0.79) 	numberofadditionalmembers=1;
						if (0.79 <= randomvalue && randomvalue < 0.92) 	numberofadditionalmembers=2;
						if (0.92 <= randomvalue && randomvalue < 0.95) 	numberofadditionalmembers=3;
						if (0.95 <= randomvalue && randomvalue <= 1) 		numberofadditionalmembers=4;
					}
					
					//TODO improvement: make the choice sensitive of the context (e.g., two pensioners is more likely than a pensioner and a student, ...)
								
					int anzahlweiterepers = Math.min(numberofadditionalmembers, otherunmodeledpersinhh.size());
					for (int i=1 ; i<= anzahlweiterepers; i++)
					{
						// choose randomly one unmodeled member
						List<Integer> keys = new ArrayList<Integer>(otherunmodeledpersinhh.keySet());
						Integer randomkey = keys.get(randomgenerator.getRandomPersonKey(keys.size()));
						
						// add activity to the other member's schedule
						ActitoppPerson otherperson = otherunmodeledpersinhh.get(randomkey);
						otherperson.addJointActivityforConsideration(tmpactivity);
						
						// add other person as participant for the actual person
						tmpactivity.addJointParticipant(otherperson);
						
						// remove person from map and proceed
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
					executeStep98(act, "98C");
					break;
				case EDUCATION:
					if (person.getAge()<10) {
						act.setMobiToppActType((byte) 31);
					} 
					else if ((person.getAge()<19)) {
						act.setMobiToppActType((byte) 32);						
					} 
					else {
						act.setMobiToppActType((byte) 33);
					}
					break;
				case SHOPPING:
					executeStep98(act, "98A");
					break;
				case LEISURE:
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
