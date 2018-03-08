package edu.kit.ifv.mobitopp.actitopp.demo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
	
	

	public HashMap<Number, ActitoppPerson> loadInput(InputStream input) throws FileNotFoundException, IOException
	{
		      
    // Initialisierungen

		HashMap<Number, ActitoppPerson> personmap = new HashMap<Number, ActitoppPerson>();

		
    String line = null;
    boolean header = true;

    try (BufferedReader inRead = new BufferedReader(new InputStreamReader(input)))
    {
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
    }
	     
	  return personmap;
	}
}