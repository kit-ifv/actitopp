package edu.kit.ifv.mobitopp.actitopp;

public class ExampleActiTopp {
	
	private static ModelFileBase fileBase = new ModelFileBase();
	private static RNGHelper randomgenerator = new RNGHelper(1234);
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		ActitoppPerson testperson = new ActitoppPerson(
				99, 	// PersIndex
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

	}

}
