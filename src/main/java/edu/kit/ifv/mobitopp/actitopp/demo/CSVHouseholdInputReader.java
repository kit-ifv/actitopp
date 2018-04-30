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
public class CSVHouseholdInputReader
{
	
	BufferedReader inRead;
	
	/**
	 *  
	 * Konstruktor
	 *
	 * @param filename
	 */
	public CSVHouseholdInputReader (String filename) throws FileNotFoundException
	{
		inRead = new BufferedReader(new FileReader(filename));
	}
	
	/**
	 * 
	 * Konstruktor
	 *
	 * @param input
	 */
	public CSVHouseholdInputReader (InputStream input)
	{
		inRead = new BufferedReader(new InputStreamReader(input));
	}

	/**
	 * 
	 * Methode zum Einlesen von Haushaltsdaten
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public HashMap<Number, ActiToppHousehold> loadInput() throws FileNotFoundException, IOException
	{
		      
    // Initialisierungen

		HashMap<Number, ActiToppHousehold> householdmap = new HashMap<Number, ActiToppHousehold>();

		
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
        
        ActiToppHousehold tmphousehold = new ActiToppHousehold(
        		Integer.parseInt(splitted[0]), 		// HouseholdIndex
        		Integer.parseInt(splitted[1]), 		// Kinder 0-10
        		Integer.parseInt(splitted[2]), 		// Kinder unter 18
        		Integer.parseInt(splitted[3]), 		// Raumtyp
        		Integer.parseInt(splitted[4])			// Pkw im HH
    				);		
        
        householdmap.put(Integer.parseInt(splitted[0]), tmphousehold);
        
      }
      catch (NumberFormatException e)
      {
      	// e.printStackTrace();
      	System.err.println("Ungültige Eingabedaten - NumberFormatException");
      	System.err.println("Zeile " + zeilenzaehler + " wird verworfen!");
      }
    }
	     
	  return householdmap;
	}
}