package edu.kit.ifv.mobitopp.actitopp;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class CSVDCModelInformationLoader 
{

	public void loadModelFlowData(InputStream input, DCModelSteplnformation modelstep) throws FileNotFoundException, IOException
	{
		
		// Map for parameter Names and Context	      
		HashMap<String, String> paramNamesContexts = new HashMap<String, String>();
		// List for alternative names
    ArrayList<String> alternativesList = new ArrayList<String>();

    String line = null;
    boolean header = true;

    // read input data
    try (BufferedReader inRead = new BufferedReader(new InputStreamReader(input)))
    {
      while ((line = inRead.readLine()) != null)
      {
        String paramName = "";
        boolean in = false;
        boolean alt = false;

        // skip header section
        if (header)
        {
            line = inRead.readLine();
            header = false;
        }
        String[] splitted = line.split(";");
        paramName = splitted[0];
        
        if (splitted[1].equals("yes"))	in = true;
        /*
         * splitted[2] contains outParamInformation - not used anymore
         * Tim (29.08.2018)
         */
        // if (splitted[2].equals("yes"))	out = true;
        if (splitted[3].equals("yes"))	alt = true;

        if (in)
        {
            assert (splitted[4].equals("default") || splitted[4].equals("person") || splitted[4].equals("day") || splitted[4].equals("tour") || splitted[4].equals("activity")) : "wrong Reference Value for InputParamMap - " + splitted[1] + " - " + splitted[4] + " - SourceLocation: " + input;
            paramNamesContexts.put(paramName,splitted[4]);
        }

        if (alt)	alternativesList.add(paramName);
      }
    }
    
    // Add the resulting information from file to the model information
    modelstep.setParameterNamesContexts(paramNamesContexts);
    modelstep.setAlternativesList(alternativesList);

	}
}
