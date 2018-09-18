package edu.kit.ifv.mobitopp.actitopp;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
/**
 * 
 * @author Tim Hilgert
 *
 */
public class CSVWRDDistributionLoader
{

  public WRDModelDistributionInformation loadDistributionInformation(InputStream input)
  {
  	WRDModelDistributionInformation minfo = new WRDModelDistributionInformation();
  	   
    try (BufferedReader inRead = new BufferedReader(new InputStreamReader(input)))
    {
      boolean header = true;
      String line = null;

      while ((line = inRead.readLine()) != null)
      {
        if (header)
        {
          line = inRead.readLine();
          header = false;
        }
        String[] splitted = line.split(";");
        
        int slot = Integer.parseInt(splitted[0]);
        int amount = Integer.parseInt(splitted[1]);
        /*
         * Tim (18.09.2018): only first two columns are used. all other information is redundant and is now created dynamically at runtime
         * 
        int amount_sum = Integer.parseInt(splitted[2]);
        double share = 0d;
        double share_sum = 0d;
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        try
        {
        	share = nf.parse(splitted[3]).doubleValue();
        	share_sum = nf.parse(splitted[4]).doubleValue();
        }
        catch (ParseException e)
        {
          e.printStackTrace();
        }
        */
        
        minfo.addDistributionElement(slot, amount);
        
      }      
    } 
    catch (IOException e) 
    {
			e.printStackTrace();
		}
    return minfo;
  }
}
