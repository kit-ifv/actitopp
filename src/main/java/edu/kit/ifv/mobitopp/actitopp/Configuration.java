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
 * 		Steuerung der Input-Parametersets	
 * 
 * 		erlaubte Konfigurationen:
 * 
 * 		mopv10					=	Nutzung der Parameterschätzungen des MOP 2004-2013 (nur für actiTopp Version 1.0)
 * 		mopv11					= Nutzung der Parameterschätzungen des MOP 2004-2013 (ab actiTopp Version 1.1)
 * 		mopv13					= Nutzung der Parameterschätzungen des MOP 2004-2013 (inkl. gemeinsame Aktivitäten - ab actiTopp Version 1.3)
 * 		mopv14					= Nutzung der Parameterschätzungen des MOP 2004-2013 (ab actiTopp Version 1.4)
 * 		stuttgart				=	Nutzung der Parameterschätzungen auf Basis des MOP 2004-2013 kalibriert auf die Stuttgart-Erhebung	
 */
	
	public static final String parameterset = "mopv14";
	
	
	
/*
 * 	Zu Vergleichszwecken und zur Demonstration der notwendigen Koordinierung bei der Modellierung einer Woche
 * 	Zeigen der Qualität von actiTopp kann die Modellierung auch unkoordiniert durchgeführt werden.
 * 	Das bedeutet, dass Schritt 8A nicht angewendet wird (standarddauer ist immer 0) und damit in Schritt 8B immer das volle Alternativenset zur Verfügung steht.
 * 	
 */
	public static boolean coordinated_modelling = true;	
	
/*
 * Berücksichtigung von gemeinsamen Wegen und Aktivitäten auf Haushaltsbasis
 * 	
 */
	public static boolean model_joint_actions = true;
	
	
	
	//Angabe der Stufen, die Logit-Modellierungen verwenden
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
  	/* Stufe 1K,1L ist erst ab actiTopp 1.4 verfügbar*/
  	
  	dcsteps.add("2A");
  	
  	dcsteps.add("3A");
  	dcsteps.add("3B");
  	
  	dcsteps.add("4A");
  	
  	dcsteps.add("5A");
  	dcsteps.add("5B");
  	
  	dcsteps.add("6A");
  	
  	dcsteps.add("7A");
  	dcsteps.add("7B");
  	dcsteps.add("7C");
  	dcsteps.add("7D");
  	dcsteps.add("7E");
  	
  	dcsteps.add("8A");
  	dcsteps.add("8B");
  	dcsteps.add("8D");
  	dcsteps.add("8J");
  	
  	dcsteps.add("9A");
  	
  	dcsteps.add("10A");
  	dcsteps.add("10M");
  	dcsteps.add("10O");
  	dcsteps.add("10Q");
  	dcsteps.add("10S");
  	/*"10B", "10D", "10G", "10J",*/ /*Stufe 10B-J sind ab actiTopp 1.4 nicht mehr enthalten */
  	
  	dcsteps.add("11");
  	/* Stufe 11 ist erst ab actiTopp 1.3 verfügbar*/
  	
  	dcsteps.add("98A");
  	dcsteps.add("98B");
  	dcsteps.add("98C");
  }  
  
  //Angabe der Stufen, die WRD-Modellierungen verwenden
  public static final HashMap<String, Integer> wrdsteps;
  static
  {
  	wrdsteps = new HashMap<String, Integer>();
  	wrdsteps.put("7K", 8);
  	wrdsteps.put("7L", 5);
  	wrdsteps.put("7M", 5);
  	wrdsteps.put("7N", 4);
  	wrdsteps.put("7O", 3);
  	
  	wrdsteps.put("8C", 14);
  	wrdsteps.put("8E", 14);
  	wrdsteps.put("8K", 14);
  	
  	wrdsteps.put("10N", 15);
  	wrdsteps.put("10P", 10);
  	wrdsteps.put("10R", 10);
  	wrdsteps.put("10T", 9);
  }
  
  //Angaben der Dateinamen, die Parameter Estimated für einfache Regressionen enthalten
  public static final HashSet<String> linregsteps_filenames;
  static
  {
  	linregsteps_filenames = new HashSet<String>();
  	linregsteps_filenames.add("97estimates");	
  }
  

  
	
/*
 * 		Aktitätenzwecke
 */	
	
	// genutzte Aktivitätstypen im Modell
	public static final ArrayList<Character> ACTIVITY_TYPES = new ArrayList<Character>(Arrays.asList('W', 'E', 'L', 'S', 'T'));
	// genutzte Aktivitätstypen (mobiTopp Aktivitäten)
	public static final ArrayList<Byte> ACTIVITY_TYPES_mobiTopp = new ArrayList<Byte>(Arrays.asList((byte)1,(byte)2,(byte)3,(byte)6,(byte)7,(byte)11,(byte)12,(byte)41,(byte)42,(byte)51,(byte)52,(byte)53,(byte)77));
	// Anzahl möglicher Aktivitäten
	public static final int NUMBER_OF_ACTIVITY_TYPES = ACTIVITY_TYPES.size();

	
/*
 * 		Aktivitätendauern - Zeitklassen	
 */
		
	// Zeitklassen für Aktivitätendauern - Lower Bounds
	public static final int[] ACT_TIME_TIMECLASSES_LB = { 1, 15, 30, 60, 120, 180, 240, 300, 360, 420, 480, 540, 600, 660, 720 };
	// Zeitklassen für Aktivitätendauern - Upper Bounds
	public static final int[] ACT_TIME_TIMECLASSES_UB = { 14, 29, 59, 119, 179, 239, 299, 359, 419, 479, 539, 599, 659, 719, 1440 };
	// Anzahl an Zeitklassen für Aktivitätendauer
	public static final int NUMBER_OF_ACT_DURATION_CLASSES = ACT_TIME_TIMECLASSES_LB.length;
	
	
/*
 * 		Heimaktivitätendauern - Zeitklassen	
 */
				
	// Zeitklassen für HomeTime - Lower Bounds
	public static final int[] HOME_TIME_TIMECLASSES_LB = { 0, 15, 30, 60, 120, 180, 240, 300, 360, 420 };
	// Zeitklassen für HomeTime - Upper Bounds
	public static final int[] HOME_TIME_TIMECLASSES_UB = { 14, 29, 59, 119, 179, 239, 299, 359, 419, 1440 };
	// Anzahl an Zeitklassen für HomeTime
	public static final int NUMBER_OF_HOME_DURATION_CLASSES = HOME_TIME_TIMECLASSES_LB.length;

	
/*
 * 		Startzeiten Haupttouren - Zeitklassen	
 */	
	
	// Zeitklassen für Startzeiten von Haupttouren - Lower Bounds 
	public static final int[] MAIN_TOUR_START_TIMECLASSES_LB = { 0, 120, 240, 360, 420, 480, 540, 600, 660, 780, 900, 960, 1020, 1080, 1200, 1320 };
	// Zeitklassen für Startzeiten von Haupttouren - Upper Bounds 
	public static final int[] MAIN_TOUR_START_TIMECLASSES_UB = { 119, 239, 359, 419, 479, 539, 599, 659, 779, 899, 959, 1019, 1079, 1199, 1319, 1439 };
	// Anzahl an Zeitklassen für Haupttouren
	public static final int NUMBER_OF_MAIN_START_TIME_CLASSES = MAIN_TOUR_START_TIMECLASSES_LB.length;

	
/*
 * 		Startzeiten VOR-Touren - Zeitklassen	
 */		
	
	// Zeitklassen für Startzeiten von Vortouren - Lower Bounds 
	public static final int[] PRE_TOUR_START_TIMECLASSES_LB = { 0, 360, 420, 480, 540, 600, 660, 780, 900, 960, 1020 };
	// Zeitklassen für Startzeiten von Vortouren - Upper Bounds 
	public static final int[] PRE_TOUR_START_TIMECLASSES_UB = { 359, 419, 479, 539, 599, 659, 779, 899, 959, 1019, 1439 };
	// Anzahl an Zeitklassen für Vortouren
	public static final int NUMBER_OF_PRE_START_TIME_CLASSES = PRE_TOUR_START_TIMECLASSES_LB.length;

	
/*
 * 		Startzeiten NACH-Touren - Zeitklassen	
 */			
	
	// Zeitklassen für Startzeiten von Nachtouren - Lower Bounds 
	public static final int[] POST_TOUR_START_TIMECLASSES_LB = { 0, 600, 660, 780, 900, 960, 1020, 1080, 1140, 1200, 1320 };
	// Zeitklassen für Startzeiten von Nachtouren - Upper Bounds 
	public static final int[] POST_TOUR_START_TIMECLASSES_UB = { 599, 659, 779, 899, 959, 1019, 1079, 1139, 1199, 1319, 1439 };
	// Anzahl an Zeitklassen für Nachtouren
	public static final int NUMBER_OF_POST_START_TIME_CLASSES = POST_TOUR_START_TIMECLASSES_LB.length;

	
/*
 * 		Startzeiten 2.,3. Tour - Zeitklassen	
 */		
	
	// Zeitklassen für Startzeiten von zweiter und dritte Tour des Tages - Lower Bounds 
	public static final int[] SECTHR_TOUR_START_TIMECLASSES_LB = { 0, 540, 660, 780, 840, 900, 960, 1020, 1080, 1140, 1200};
	// Zeitklassen für Startzeiten von zweiter und dritte Tour des Tages - Upper Bounds 
	public static final int[] SECTHR_TOUR_START_TIMECLASSES_UB = { 539, 659, 779, 839, 899, 959, 1019, 1079, 1139, 1199, 1439};
	// Anzahl an Zeitklassen für zweiter und dritte Tour des Tages
	public static final int NUMBER_OF_SECTHR_START_TIME_CLASSES = SECTHR_TOUR_START_TIMECLASSES_LB.length;

/*
 * 		Wegzeitkonstante	
 */		
	
	// Konstante für die durchschnittliche Zeitbelegung eines Weges in Minuten
	public static final int FIXED_TRIP_TIME_ESTIMATOR = 15;
	
	
/*
 * 		Folgende Einstellungen sind nur zu Debugzwecken relevant
 */
	
	// steuert die Ausgabe von Log in Modellschritten
	public static boolean debugenabled = false;

}