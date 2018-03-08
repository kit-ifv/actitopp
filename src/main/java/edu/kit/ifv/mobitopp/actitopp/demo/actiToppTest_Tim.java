package edu.kit.ifv.mobitopp.actitopp.demo;

import java.io.InputStream;
import java.util.HashMap;

import edu.kit.ifv.mobitopp.actitopp.*;

public class actiToppTest_Tim {
	
	private static ModelFileBase fileBase = new ModelFileBase();
	private static RNGHelper randomgenerator = new RNGHelper(1234);
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		CSVPersonInputReader loader = new CSVPersonInputReader();
		try (InputStream input = ModelFileBase.class.getResourceAsStream("demo/Demopersonen.csv"))
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
				* in den Aktivitätenplänen (bspw. nicht genug Zeit für alle Aktivitäten.
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
			System.out.println("all persons processed!");
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	


}
