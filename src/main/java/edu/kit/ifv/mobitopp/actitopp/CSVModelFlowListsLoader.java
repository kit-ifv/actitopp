package edu.kit.ifv.mobitopp.actitopp;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class CSVModelFlowListsLoader 
{

	public ModelFlowLists loadLists(InputStream input) throws FileNotFoundException, IOException
	{
		
		ModelFlowLists mf = new ModelFlowLists();
	      
    // Initialisierungen

		Map<String, String> inParamMap = mf.getInParamMap();
    List<String> outParamList = mf.getOutParamList();
    List<String> alternativesList = mf.getAlternativesList();

    String line = null;
    boolean header = true;

    try (BufferedReader inRead = new BufferedReader(new InputStreamReader(input)))
    {
      while ((line = inRead.readLine()) != null)
      {
        String paramName = "";
        boolean in = false;
        boolean out = false;
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
        if (splitted[2].equals("yes"))	out = true;
        if (splitted[3].equals("yes"))	alt = true;

        if (in)
        {
            assert (splitted[4].equals("default") || splitted[4].equals("person") || splitted[4].equals("day") || splitted[4].equals("tour") || splitted[4].equals("activity")) : "wrong Reference Value for InputParamMap - " + splitted[1] + " - " + splitted[4] + " - SourceLocation: " + input;
            inParamMap.put(paramName,splitted[4]);
        }

        if (out)	outParamList.add(paramName);
        if (alt)	alternativesList.add(paramName);
      }
    }
	      
	      return mf;
	}
}
