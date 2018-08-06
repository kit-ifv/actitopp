package edu.kit.ifv.mobitopp.actitopp.demo;

import java.io.IOException;
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
		
		createAndModelOnePerson_Example1();

		createAndModelOnePerson_Example2();
		
	  createAndModelOnePerson_Example3();
		
		createAndModelMultiplePersons_Example1();
		
		createAndModelMultiplePersons_Example2();
		
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
      catch (InvalidPersonPatternException | InvalidHouseholdPatternException e)
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
			 catch (InvalidPersonPatternException | InvalidHouseholdPatternException e)
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
	 * Erzeugung einer Person inkl. Haushaltskontext und Aktivitätenplan - Beispiel 3
	 * 
	 */
	public static void createAndModelOnePerson_Example3()
	{
		
		ActiToppHousehold testhousehold = new ActiToppHousehold(
				1,		// Haushaltsindex
				0, 		// Kinder 0-10
				1, 		// Kinder unter 18
				2, 		// Raumtyp
				2			// Pkw im HH
				);
		
		ActitoppPerson testperson = new ActitoppPerson(
				testhousehold,  // Haushalt
				1,							// PersNr im Haushalt
				10, 						// PersIndex
				55, 						// Alter
				1, 							// Beruf
				1 							// Geschlecht
				);		
		
		// Person zum Haushalt hinzufügen
		testhousehold.addHouseholdmember(testperson, testperson.getPersIndex());
		
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
      catch (InvalidPersonPatternException | InvalidHouseholdPatternException e)
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
	public static void createAndModelMultiplePersons_Example1()
	{
		try
		{			
			CSVPersonInputReader loader = new CSVPersonInputReader(ModelFileBase.class.getResourceAsStream("demo/Demopersonen.csv"));
			HashMap<Number, ActitoppPerson> personmap = loader.loadInput();
			
			for (Number key : personmap.keySet())
			{
				ActitoppPerson actperson = personmap.get(key);
				System.out.println(actperson);
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
	        catch (InvalidPersonPatternException | InvalidHouseholdPatternException e)
	        {
	          System.err.println(e.getReason());
	          System.err.println("person involved: " + actperson.getPersIndex());
	        }
		    }
				
				actperson.getWeekPattern().printAllActivitiesList();
				 
			}
				
			// Output als CSV-Datei
			CSVExportWriter tripwriter = new CSVExportWriter("D:/DemoTripList.csv");
			tripwriter.exportTripData(personmap);
			
			CSVExportWriter activitywriter = new CSVExportWriter("D:/DemoActivityList.csv");
			activitywriter.exportActivityData(personmap);
			
			System.out.println("all persons processed!");	
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	/**
	 * 
	 * Erzeugung mehrerer Personen inkl. Haushaltskontext & Aktivitätenplan
	 * 
	 */
	public static void createAndModelMultiplePersons_Example2()
	{
				
		/*
		 * 
		 * Inputpersonen aus SAS-Export-Datei einlesen
		 * 
		 */
		HashMap<Number, ActiToppHousehold> householdmap = null;
		HashMap<Number, ActitoppPerson> personmap = null;;
				
		try
		{
			CSVHouseholdInputReader hhloader = new CSVHouseholdInputReader(ModelFileBase.class.getResourceAsStream("demo/Demo_HHInfo.csv"));
			CSVPersonInputReader personloader = new CSVPersonInputReader(ModelFileBase.class.getResourceAsStream("demo/Demo_Personen_mitHHIndex.csv"));
			householdmap = hhloader.loadInput();			
			personmap = personloader.loadInput_withHHIndex(householdmap);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		assert householdmap!=null : "HouseholdMap konnte nicht eingelesen werden.";
		assert personmap !=null : "PersonMap konnte nicht eingelesen werden.";
		
		
		for (Number key : householdmap.keySet())
		{
			ActiToppHousehold acthousehold = householdmap.get(key);
			System.out.println("HH: " + acthousehold.getHouseholdIndex() + " HHGRO: " + acthousehold.getNumberofPersonsinHousehold());
			
							
		  /* 
		   * Erzeuge Schedules für die Person bis der Schedule keine Fehler mehr hat.
		   * 
		   * Reihenfolge der Aktivitätenplanmodellierung orientiert sich an der Wahrscheinlichkeit des Anteils gemeinsamer Aktivitäten
		   * 
		   * In einigen Fällen kommt es aufgrund ungünstiger Zufallszahlen zu Überlappungen
			 * in den Aktivitätenplänen (bspw. nicht genug Zeit für alle Aktivitäten).
			 * In diesen seltenen Fällen wird die Planerstellung mit einer neuen Zufallszahl wiederholt.
			 */
			
			boolean householdscheduleOK = false;
			while (!householdscheduleOK)
			{	
				try
				{
					
					// Pläne für den gesamten Haushalt generieren
					acthousehold.generateSchedules(fileBase, randomgenerator);

					//System.out.println("HHdone: " + key);
					householdscheduleOK = true;
					
				}
				catch (InvalidHouseholdPatternException e)
				{
					System.err.println(e.getReason());
					
	        // Setze die Modellierungsergebnisse dieses Haushalts zurück für neuen Versuch
	        acthousehold.resetHouseholdModelingResults();
				}
			}
		}
				
		
		try
		{
			// Output der Wege als CSV-Datei
			CSVExportWriter tripwriter = new CSVExportWriter("D:/DemoTripList.csv");
			tripwriter.exportTripData(personmap);
			
			// Output der Aktivitäten als CSV-Datei
			CSVExportWriter activitywriter = new CSVExportWriter("D:/DemoActivityList.csv");
			activitywriter.exportActivityData(personmap);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
			
		System.out.println("all persons processed!");	

	}

}
