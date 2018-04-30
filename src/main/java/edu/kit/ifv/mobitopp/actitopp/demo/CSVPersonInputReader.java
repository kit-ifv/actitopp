package edu.kit.ifv.mobitopp.actitopp.demo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import edu.kit.ifv.mobitopp.actitopp.*;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class CSVPersonInputReader
{
	
	BufferedReader inRead;
	
	/**
	 *  
	 * Konstruktor
	 *
	 * @param filename
	 */
	public CSVPersonInputReader (String filename) throws FileNotFoundException
	{
		inRead = new BufferedReader(new FileReader(filename));
	}
	
	/**
	 * 
	 * Konstruktor
	 *
	 * @param input
	 */
	public CSVPersonInputReader (InputStream input)
	{
		inRead = new BufferedReader(new InputStreamReader(input));
	}

	/**
	 * 
	 * Methode zum Einlesen von Personendaten
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public HashMap<Number, ActitoppPerson> loadInput() throws FileNotFoundException, IOException
	{
		      
    // Initialisierungen

		HashMap<Number, ActitoppPerson> personmap = new HashMap<Number, ActitoppPerson>();

		
    String line = null;
    boolean header = true;

  	int zeilenzaehler=0;
  	
    while ((line = inRead.readLine()) != null)
    {
    	zeilenzaehler++;
      
      // skip header section
      if (header)
      {
          line = inRead.readLine();
          header = false;
      }
      String[] splitted = line.split(";");
      
      try
      {
        
        ActitoppPerson tmpperson = new ActitoppPerson(
        		Integer.parseInt(splitted[0]), 		// PersIndex
        		Integer.parseInt(splitted[1]), 		// Kinder 0-10
        		Integer.parseInt(splitted[2]), 		// Kinder unter 18
        		Integer.parseInt(splitted[3]), 		// Alter
        		Integer.parseInt(splitted[4]), 		// Beruf
        		Integer.parseInt(splitted[5]), 		// Geschlecht
        		Integer.parseInt(splitted[6]), 		// Raumtyp
        		Integer.parseInt(splitted[7]),		// Pkw im HH
        		Double.parseDouble(splitted[8]),	// Pendeldistanz zur Arbeit in Kilometern	(0 falls kein Pendeln)
        		Double.parseDouble(splitted[9])		// Pendeldistanz zu Bildungszwecken in Kilometern (0 falls kein Pendeln)
    				);		
        
        personmap.put(Integer.parseInt(splitted[0]), tmpperson);
        
      }
      catch (NumberFormatException e)
      {
      	// e.printStackTrace();
      	System.err.println("Ungültige Eingabedaten - NumberFormatException");
      	System.err.println("Zeile " + zeilenzaehler + " wird verworfen!");
      }
    }
	     
	  return personmap;
	}

	/**
	 * 
	 * Methode zum Einlesen von Personendaten inkl. Haushaltskontext
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public HashMap<Number, ActitoppPerson> loadInput_withHHIndex(HashMap<Number, ActiToppHousehold> householdmap) throws FileNotFoundException, IOException
	{
		      
	  // Initialisierungen
	
		HashMap<Number, ActitoppPerson> personmap = new HashMap<Number, ActitoppPerson>();
	
		
	  String line = null;
	  boolean header = true;
	
		int zeilenzaehler=0;
		
	  while ((line = inRead.readLine()) != null)
	  {
	  	zeilenzaehler++;
	    
	    // skip header section
	    if (header)
	    {
	        line = inRead.readLine();
	        header = false;
	    }
	    String[] splitted = line.split(";");
	    
	    try
	    {
	    	
	      // Haushalt ermitteln, damit Person mit Haushaltsinfos erzeugt werden kann
	      ActiToppHousehold tmphousehold = householdmap.get(Integer.parseInt(splitted[0]));
	      assert tmphousehold!=null:"household does not exists - hhindex: " + Integer.parseInt(splitted[0]);
	    		 
	      // Person erzeugen
	      ActitoppPerson tmpperson = new ActitoppPerson(
	      		tmphousehold,
	      		Integer.parseInt(splitted[2]), 		// PersIndex
	      		Integer.parseInt(splitted[3]), 		// Alter
	      		Integer.parseInt(splitted[4]), 		// Beruf
	      		Integer.parseInt(splitted[5]), 		// Geschlecht
	      		Double.parseDouble(splitted[6]),	// Pendeldistanz zur Arbeit in Kilometern	(0 falls kein Pendeln)
	      		Double.parseDouble(splitted[7])		// Pendeldistanz zu Bildungszwecken in Kilometern (0 falls kein Pendeln)
	  				);		
	      
	      // Person dem Haushalt hinzufügen
	      tmphousehold.addHouseholdmember(tmpperson, Integer.parseInt(splitted[1]));
	      
	      // Person der Map hinzufügen
	      personmap.put(Integer.parseInt(splitted[2]), tmpperson);
	      
	    }
	    catch (NumberFormatException e)
	    {
	    	// e.printStackTrace();
	    	System.err.println("Ungültige Eingabedaten - NumberFormatException");
	    	System.err.println("Zeile " + zeilenzaehler + " wird verworfen!");
	    }
	  }
	     
	  return personmap;
	}
}