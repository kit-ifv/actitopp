package edu.kit.ifv.mobitopp.actitopp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 
 * @author Tim Hilgert
 *
 */
public class CSVParameterWeightLoader
{
	
//TODO Kommentierung fehlt!	
	
  public Map<String, List<ModelParameterWeight>> getWeightValues(InputStream input, List<String> propertyNames, List<String> alternativeNames)
  {

    int altSize = alternativeNames.size();
    int alternativePtr = 0;

    List<List<ModelParameterWeight>> utilityLists = new ArrayList<List<ModelParameterWeight>>(altSize);

    for (int j = 0; j < altSize; j++)
    {
        utilityLists.add(new ArrayList<ModelParameterWeight>());
    }
      
			try(BufferedReader inRead = new BufferedReader(new InputStreamReader(input)))
			{
		    boolean header = true;
		    String line = null;

		    while((line  = inRead.readLine())!= null)
		    {
	        if(header)
	        {
            line = inRead.readLine();
            header = false;
	        }
	        
	        String[] splitted = line.split(";");
	        //String propertyNr =splitted[0];
	        String readPropertyName =splitted[1];
	        String altNr = splitted[2];
	        //String altName = splitted[3];
	        double parameterWeightValue = 0f;
	        //Tim: 17.03 NumberFormat von German auf Englisch umgestellt, da besser konform zu Standard-SAS-Export
	        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
	        //NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
	        try
	        {
            parameterWeightValue = nf.parse(splitted[4]).doubleValue();
	        }
	        catch (ParseException e)
	        {
            e.printStackTrace();
	        }
		        
	        if(!readPropertyName.equals("0"))
	        {
            //set property to "default 1", real property will get set in later steps.
            ModelParameterWeight uPair = new ModelParameterWeight(readPropertyName, parameterWeightValue, -99999);
            //alternatives start with 1 in the data files, must start with 0 here...
            utilityLists.get(Integer.parseInt(altNr)-1).add(uPair); 
            alternativePtr = (alternativePtr +1) % altSize;
	        }           
        }
      }
      catch (IOException e)
      {
          e.printStackTrace();
      }

      Map<String, List<ModelParameterWeight>> utilityMap = new HashMap<String, List<ModelParameterWeight>>();
      for (int i = 0; i < altSize; i++)
      {
          utilityMap.put(alternativeNames.get(i), utilityLists.get(i));
      }

      return utilityMap;
  }
}
