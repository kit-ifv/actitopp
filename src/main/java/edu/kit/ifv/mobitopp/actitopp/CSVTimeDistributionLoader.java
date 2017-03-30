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
public class CSVTimeDistributionLoader
{
	
//TODO Kommentierung fehlt!	
  public DiscreteTimeDistribution loadDistribution(InputStream input)
  {
    //TODO: performance optimization: find out how many lines there are and do use a fixed array instead of an list (and lateron toArray copy)
    List<Double> relSumList = new ArrayList<Double>();
    List<Integer> sharesList = new ArrayList<Integer>();
    
    int sameValueRange = 0;
    double lastValue = -1.0;       
    Map<Integer, Integer> sameValueMap = new HashMap<Integer, Integer>();
    
    try (BufferedReader inRead = new BufferedReader(new InputStreamReader(input)))
    {
      boolean header = true;
      String line = null;
      int startPoint = -1;
      int endPoint = -1;
      int runningIndex =-1;
      while ((line = inRead.readLine()) != null)
      {
        if (header)
        {
          line = inRead.readLine();
          header = false;
        }
        String[] splitted = line.split(";");
        int slot = Integer.parseInt(splitted[0]);
        double relSum = 0d;
        int share = Integer.parseInt(splitted[1]);
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        try
        {
          relSum = nf.parse(splitted[4]).doubleValue();
            
        }
        catch (ParseException e)
        {
          e.printStackTrace();
        }
         
        relSumList.add(relSum);
        sharesList.add(share);

        if (startPoint == -1)
        {
          startPoint = slot;
        }
        
        //TODO: check the precision and how many 0's we need
        if( (Math.abs(relSum - lastValue) < 0.000000001))
        {
        	sameValueRange++;
        }
        else
        {
          if(sameValueRange >=1)
          {
            sameValueMap.put((runningIndex)-sameValueRange, sameValueRange);
          }
          sameValueRange = 0;
        }
        
        lastValue = relSum;
        runningIndex++;

      }

      endPoint = runningIndex + startPoint;
      
      double[] dArr = new double[relSumList.size()];
      for (int i = 0; i < dArr.length; i++) {
      	dArr[i] = relSumList.get(i);  
      }
      
      int[] sArr = new int[sharesList.size()];
      for(int i = 0; i < sArr.length;i++)
      {
        sArr[i] = sharesList.get(i);
      }
      
      //finish map
      if(sameValueRange >= 1)
      {
        sameValueMap.put((runningIndex+1)-sameValueRange, sameValueRange);
      }
      
      DiscreteTimeDistribution distribution = new DiscreteTimeDistribution(startPoint, endPoint,dArr,sameValueMap, sArr );
      return distribution;

    }
    catch (NumberFormatException | IOException e)
    {
        e.printStackTrace();
    }
    return null;
  }
}
