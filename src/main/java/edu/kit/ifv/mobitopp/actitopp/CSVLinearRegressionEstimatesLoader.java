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
 * @author Tim Hilgert
 *
 */
public class CSVLinearRegressionEstimatesLoader
{

	/**
	 * 
	 * Methode liest Estimates Werte aus Inputdatei ein und speichert diese in einer HashMap
	 * 
	 * @param input
	 * @return
	 */
  public Map<String, LinearRegressionEstimate> getEstimates(InputStream input)
  {

    Map<String, LinearRegressionEstimate> estimatesMap = new HashMap<String, LinearRegressionEstimate>();

      
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
        String variable = splitted[0];
        String contextIdentifier = splitted[2];
 
        double estimatevalue = 0f;
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);

        try
        {
        	estimatevalue = nf.parse(splitted[1]).doubleValue();
        }
        catch (ParseException e)
        {
          e.printStackTrace();
        }
        
        assert (contextIdentifier.equals("default") || contextIdentifier.equals("person") || contextIdentifier.equals("day") || contextIdentifier.equals("tour") || contextIdentifier.equals("activity")) : "wrong Reference Value for InputParamMap - " + variable + " - " + contextIdentifier + " - SourceLocation: " + input;
        estimatesMap.put(variable, new LinearRegressionEstimate(variable, estimatevalue, contextIdentifier));          
      }
    }
    catch (IOException e)
    {
        e.printStackTrace();
    }

    return estimatesMap;
  }
}
