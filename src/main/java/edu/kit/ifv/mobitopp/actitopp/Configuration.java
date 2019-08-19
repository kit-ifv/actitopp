package edu.kit.ifv.mobitopp.actitopp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class Configuration {

	
/*
 * coordination of input parameter set
 * 
 * possible configurations:
 * 
 * mopv10					=	use parameter estimation of MOP 2004-2013 (actiTopp version 1.0)
 * mopv11					= use parameter estimation of MOP 2004-2013 (actiTopp version 1.1 & 1.2)
 * mopv13					= use parameter estimation of MOP 2004-2013 (actiTopp version 1.3 incl. joint activities)
 * mopv14					= use parameter estimation of MOP 2004-2013 (actiTopp version 1.4+ incl. joint activities)
 * stuttgart			=	use parameter estimation of MOP 2004-2013 calibrated for stuttgart area (only actitopp version 1.0)	
 */
	
	public static final String parameterset = "mopv14";

/*
 * input parameter set for detailed modeling of activity purposes used by mobitopp (modeling step 98)
 * 
 * purposes_stuttgart			= use parameter sets for stuttgart area 
 * purposes_regiomove			= use parameter sets for regiomove project
 * 
 */
	public static final String parameterset_purposes = "purposes_regiomove";
	
/*
 * when modeling a whole week, we need to consider stability aspects in behavior (e.g. leaving the house at the same time every morning).
 * To demonstrate the siginficance of this aspects, the coordination of the modeling can enabled and disabled.
 * 
 * i.e: when disabled, different steps (e.g. 8A) will be skipped
 * 	
 */
	public static boolean coordinated_modelling = true;	
	
/*
 * modeling joint actions within household 	
 */
	public static boolean model_joint_actions = true;
	
	
	
	/*
	 * steps that use discrete choice modeling
	 */
  public static final HashSet<String> dcsteps;
  static
  {
  	dcsteps = new HashSet<String>();
  	dcsteps.add("1A");
  	dcsteps.add("1B");
  	dcsteps.add("1C");
  	dcsteps.add("1D");
  	dcsteps.add("1E");
  	dcsteps.add("1F");
  	dcsteps.add("1K");
  	dcsteps.add("1L");
  	/* step 1K,1L is available since actiTopp version 1.4 */
  	
  	dcsteps.add("2A");
  	
  	dcsteps.add("3A");
  	dcsteps.add("3B");
  	
  	dcsteps.add("4A");
  	
  	dcsteps.add("5A");
  	dcsteps.add("5B");
  	
  	dcsteps.add("6A");
  	
  	dcsteps.add("7A");
  	dcsteps.add("7C");
  	dcsteps.add("7E");
  	dcsteps.add("7G");
  	dcsteps.add("7I");
  	
  	dcsteps.add("8A");
  	dcsteps.add("8B");
  	dcsteps.add("8D");
  	dcsteps.add("8J");
  	
  	dcsteps.add("9A");
  	
  	dcsteps.add("10A");
  	dcsteps.add("10M");
  	dcsteps.add("10O");
  	dcsteps.add("10S");
  	/*"10B", "10D", "10G", "10J",*/ /*Stufe 10B-J are no longer included since actiTopp version 1.4 */
  	
  	dcsteps.add("11");
  	/* step 11 is available since actiTopp version 1.3 */
  }  
  
  
	/*
	 * steps that use discrete choice modeling
	 */
  public static final HashSet<String> dcsteps_purposes;
  static
  {
  	dcsteps_purposes = new HashSet<String>(); 	
  	dcsteps_purposes.add("98A");
  	dcsteps_purposes.add("98B");
  	dcsteps_purposes.add("98C");
  }  
  
  /*
   * steps that use weighted random draw (wrd) modeling
   */
  public static final HashMap<String, Integer> wrdsteps;
  static
  {
  	wrdsteps = new HashMap<String, Integer>();
  	wrdsteps.put("7B", 8);
  	wrdsteps.put("7D", 5);
  	wrdsteps.put("7F", 5);
  	wrdsteps.put("7H", 4);
  	wrdsteps.put("7J", 3);
  	
  	wrdsteps.put("8C", 14);
  	wrdsteps.put("8E", 14);
  	wrdsteps.put("8K", 14);
  	
  	wrdsteps.put("10N", 15);
  	wrdsteps.put("10P", 10);
  	wrdsteps.put("10T", 9);
  }
  
  /*
   * steps that use lineare regression modeling
   */
  public static final HashSet<String> linregsteps_filenames;
  static
  {
  	linregsteps_filenames = new HashSet<String>();
  	linregsteps_filenames.add("97estimates");	
  }
  

  
	
/*
 * activity types
 */	
	public static final ArrayList<Byte> ACTIVITY_TYPES_mobiTopp = new ArrayList<Byte>(Arrays.asList((byte)1,(byte)2,(byte)3,(byte)6,(byte)7,(byte)11,(byte)12,(byte)41,(byte)42,(byte)51,(byte)52,(byte)53,(byte)77));

	
/*
 * activity durations - categories
 */
		
	// activity durations - categories - Lower Bounds
	public static final int[] ACT_TIME_TIMECLASSES_LB = { 1, 15, 30, 60, 120, 180, 240, 300, 360, 420, 480, 540, 600, 660, 720 };
	// activity durations - categories - Upper Bounds
	public static final int[] ACT_TIME_TIMECLASSES_UB = { 14, 29, 59, 119, 179, 239, 299, 359, 419, 479, 539, 599, 659, 719, 1440 };
	// activity durations - number of categories
	public static final int NUMBER_OF_ACT_DURATION_CLASSES = ACT_TIME_TIMECLASSES_LB.length;
	
	
/*
 * activity durations for home activity - categories
 */
				
	// activity durations for home activity - categories - Lower Bounds
	public static final int[] HOME_TIME_TIMECLASSES_LB = { 0, 15, 30, 60, 120, 180, 240, 300, 360, 420 };
	// activity durations for home activity - categories - Upper Bounds
	public static final int[] HOME_TIME_TIMECLASSES_UB = { 14, 29, 59, 119, 179, 239, 299, 359, 419, 1440 };
	// activity durations for home activity - number of categories
	public static final int NUMBER_OF_HOME_DURATION_CLASSES = HOME_TIME_TIMECLASSES_LB.length;

	
/*
 * start time for first tour of the day - categories
 */	
	
	// start time for first tour of the day  - Lower Bounds 
	public static final int[] FIRST_TOUR_START_TIMECLASSES_LB = { 0, 120, 240, 360, 420, 480, 540, 600, 660, 780, 900, 960, 1020, 1080, 1200, 1320 };
	// start time for first tour of the day  - Upper Bounds 
	public static final int[] FIRST_TOUR_START_TIMECLASSES_UB = { 119, 239, 359, 419, 479, 539, 599, 659, 779, 899, 959, 1019, 1079, 1199, 1319, 1439 };
	// start time for first tour of the day - number of categories
	public static final int NUMBER_OF_FIRST_START_TIME_CLASSES = FIRST_TOUR_START_TIMECLASSES_LB.length;

	
	
/*
 * start time for second and third tour of the day
 */		
	
	// start time for second and third tour of the day - Lower Bounds 
	public static final int[] SECTHR_TOUR_START_TIMECLASSES_LB = { 0, 540, 660, 780, 840, 900, 960, 1020, 1080, 1140, 1200};
	// start time for second and third tour of the day - Upper Bounds 
	public static final int[] SECTHR_TOUR_START_TIMECLASSES_UB = { 539, 659, 779, 839, 899, 959, 1019, 1079, 1139, 1199, 1439};
	// start time for second and third tour of the day - number of categories
	public static final int NUMBER_OF_SECTHR_START_TIME_CLASSES = SECTHR_TOUR_START_TIMECLASSES_LB.length;

	
/*
 * trip time constant - default time occupation of a trip
 */		
	public static final int FIXED_TRIP_TIME_ESTIMATOR = 15;
	
	
/*
 * ONLY FOR DEBUG ON CONSOLE
 */
	public static boolean debugenabled = false;

}