package edu.kit.ifv.mobitopp.actitopp.demo;

import edu.kit.ifv.mobitopp.actitopp.*;

public class ExampleActiTopp {
	
	private static ModelFileBase fileBase = new ModelFileBase();
	private static RNGHelper randomgenerator = new RNGHelper(1234);
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {



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
					
		try 
		{
			testperson.generateSchedule(fileBase, randomgenerator);
		} 
		catch (InvalidPatternException e) 
		{
			e.printStackTrace();
		}
		
		//testperson.getweekPattern().printOutofHomeActivitiesList();
		testperson.getWeekPattern().printAllActivitiesList();
	
		
		
		
		ActitoppPerson testperson2 = new ActitoppPerson(
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
		System.out.println(testperson2);
					
		try 
		{
			testperson2.generateSchedule(fileBase, randomgenerator);
		} 
		catch (InvalidPatternException e) 
		{
			e.printStackTrace();
		}
		
		//testperson.getweekPattern().printOutofHomeActivitiesList();
		testperson2.getWeekPattern().printAllActivitiesList();
		
		
		
		
		
		
		
		
	}

}
