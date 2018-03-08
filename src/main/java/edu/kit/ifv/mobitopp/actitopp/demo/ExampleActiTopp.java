package edu.kit.ifv.mobitopp.actitopp.demo;

import java.io.InputStream;
import java.util.HashMap;

import edu.kit.ifv.mobitopp.actitopp.*;

public class ExampleActiTopp {
	
	private static ModelFileBase fileBase = new ModelFileBase();
	private static RNGHelper randomgenerator = new RNGHelper(1234);
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) 
	{
		
		// createAndModelOnePerson_Example1();

		// createAndModelOnePerson_Example2();
		
		createAndModelMultiplePersons_Example();
		
	}
	
	
	/**
	 * 
	 * Erzeugung einer Person inkl. Aktivitätenplan - Beispiel 1
	 * 
	 */
	public static void createAndModelOnePerson_Example1()
	{
		ActitoppPerson testperson = new ActitoppPerson(
				10, 	// PersIndex
				0, 		// Kinder 0-10
				1, 		// Kinder unter 18
				55, 	// Alter
				1, 		// Beruf
				1, 		// Geschlecht
				2, 		// Raumtyp
				2			// Pkw im HH
				);		
		System.out.println(testperson);
					
	 /* 
	  * Erzeuge Schedules für die Person bis der Schedule keine Fehler mehr hat.
	  *  
		* In einigen Fällen kommt es aufgrund ungünstiger Zufallszahlen zu Überlappungen
		* in den Aktivitätenplänen (bspw. nicht genug Zeit für alle Aktivitäten).
		* In diesen seltenen Fällen wird die Planerstellung mit einer neuen Zufallszahl wiederholt.
		*/
		boolean scheduleOK = false;
    while (!scheduleOK)
    {
      try
      {
    		// Erzeuge Wochenaktivitätenplan
     	 testperson.generateSchedule(fileBase, randomgenerator);
    		
        scheduleOK = true;                
      }
      catch (InvalidPatternException e)
      {
        System.err.println(e.getReason());
        System.err.println("person involved: " + testperson.getPersIndex());
      }
	  }
		
		//testperson.getweekPattern().printOutofHomeActivitiesList();
		testperson.getWeekPattern().printAllActivitiesList();
	}
	
	
	/**
	 * 
	 * Erzeugung einer Person inkl. Aktivitätenplan - Beispiel 2
	 * 
	 */
	public static void createAndModelOnePerson_Example2()
	{
		ActitoppPerson testperson = new ActitoppPerson(
				20, 	// PersIndex
				0, 		// Kinder 0-10
				1, 		// Kinder unter 18
				55, 	// Alter
				1, 		// Beruf
				1, 		// Geschlecht
				2, 		// Raumtyp
				2,		// Pkw im HH
				3.0,	// Pendeldistanz zur Arbeit in Kilometern	(0 falls kein Pendeln)
				0.0		// Pendeldistanz zu Bildungszwecken in Kilometern (0 falls kein Pendeln)
				);		
		System.out.println(testperson);
		
	 /* 
	  * Erzeuge Schedules für die Person bis der Schedule keine Fehler mehr hat.
	  *  
		* In einigen Fällen kommt es aufgrund ungünstiger Zufallszahlen zu Überlappungen
		* in den Aktivitätenplänen (bspw. nicht genug Zeit für alle Aktivitäten).
		* In diesen seltenen Fällen wird die Planerstellung mit einer neuen Zufallszahl wiederholt.
		*/
		boolean scheduleOK = false;
		while (!scheduleOK)
		{
			 try
			 {
				// Erzeuge Wochenaktivitätenplan
				 testperson.generateSchedule(fileBase, randomgenerator);
				
			   scheduleOK = true;                
			 }
			 catch (InvalidPatternException e)
			 {
			   System.err.println(e.getReason());
			   System.err.println("person involved: " + testperson.getPersIndex());
			 }
		}
		
		//testperson.getweekPattern().printOutofHomeActivitiesList();
		testperson.getWeekPattern().printAllActivitiesList();
	}
	
	/**
	 * 
	 * Erzeugung mehrerer Personen inkl. Aktivitätenplan
	 * 
	 */
	public static void createAndModelMultiplePersons_Example()
	{
		CSVPersonInputReader loader = new CSVPersonInputReader();
		try (InputStream input = ModelFileBase.class.getResourceAsStream("demo/Demopersonen2.csv"))
		{
			HashMap<Number, ActitoppPerson> personmap = loader.loadInput(input);
			
			for (Number key : personmap.keySet())
			{
				ActitoppPerson actperson = personmap.get(key);
				// System.out.println(actperson);
				// System.out.println(actperson.getPersIndex());
				
				
			 /* 
			  * Erzeuge Schedules für die Person bis der Schedule keine Fehler mehr hat.
			  *  
				* In einigen Fällen kommt es aufgrund ungünstiger Zufallszahlen zu Überlappungen
				* in den Aktivitätenplänen (bspw. nicht genug Zeit für alle Aktivitäten).
				* In diesen seltenen Fällen wird die Planerstellung mit einer neuen Zufallszahl wiederholt.
				*/
				boolean scheduleOK = false;
		    while (!scheduleOK)
		    {
	        try
	        {
	      		// Erzeuge Wochenaktivitätenplan
	        	actperson.generateSchedule(fileBase, randomgenerator);
	      		
	          scheduleOK = true;                
	        }
	        catch (InvalidPatternException e)
	        {
	          System.err.println(e.getReason());
	          System.err.println("person involved: " + actperson.getPersIndex());
	        }
		    }
				
				//actperson.getWeekPattern().printAllActivitiesList();
				 
			}
				
			// Output als CSV-Datei
			CSVActivityExportWriter exportwriter = new CSVActivityExportWriter();
			exportwriter.exportActivityData(personmap, "D:/DemoActivityList.csv");
			
			System.out.println("all persons processed!");	
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
