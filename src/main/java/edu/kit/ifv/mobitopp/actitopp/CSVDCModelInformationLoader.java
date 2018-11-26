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
        String paramvalue = "";
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
        if (splitted[2].equals("yes"))	alt = true;

        if (in)
        {
        	paramvalue = splitted[3];
          assert (paramvalue.equals("default") || paramvalue.equals("person") || paramvalue.equals("day") || paramvalue.equals("tour") || paramvalue.equals("activity")) : "wrong Reference Value for InputParamMap - " + paramName + " - " + paramvalue + " - SourceLocation: " + input;
          paramNamesContexts.put(paramName,paramvalue);
        }

        if (alt)	alternativesList.add(paramName);
      }
    }
    
    // Add the resulting information from file to the model information
    modelstep.setParameterNamesContexts(paramNamesContexts);
    modelstep.setAlternativesList(alternativesList);

	}
}
