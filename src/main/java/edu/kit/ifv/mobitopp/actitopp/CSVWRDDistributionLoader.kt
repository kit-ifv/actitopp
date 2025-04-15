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
