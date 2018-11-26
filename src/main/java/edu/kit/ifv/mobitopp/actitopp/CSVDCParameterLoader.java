package edu.kit.ifv.mobitopp.actitopp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 
 * class to read parameter values for different dc model steps from file system
 * 
 * @author Tim Hilgert
 *
 */
public class CSVDCParameterLoader
{
	  
  /**
   * 
   * method to load parameter values for a specified model step
   * 
   * @param input
   * @param modelstep
   */
  public void loadParameterValues(InputStream input, DCModelSteplnformation modelstep)
  {   
    /*
     * initialization of ModelAlternativeParameterValues objects
     * an object exists for each alternative of this modelstep
     */
    Map<String, DCModelAlternativeParameterValues> alternativesParameters = new HashMap<String, DCModelAlternativeParameterValues>();
    for (String s :  modelstep.getAlternativesList())
    {
    	alternativesParameters.put(s, new DCModelAlternativeParameterValues());
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
        
        String parameterName = splitted[0];
        String alternativeName = splitted[1];
        double parameterValue = 0d;
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        try
        {
          parameterValue = nf.parse(splitted[2]).doubleValue();
        }
        catch (ParseException e)
        {
          e.printStackTrace();
        }
	        
        if(!parameterName.equals("0"))
        {
        	alternativesParameters.get(alternativeName).addParameterValue(parameterName, parameterValue);
        }           
      }
    }
    catch (IOException e)
    {
        e.printStackTrace();
    }
		modelstep.setAlternativesParameters(alternativesParameters);
  }
}
