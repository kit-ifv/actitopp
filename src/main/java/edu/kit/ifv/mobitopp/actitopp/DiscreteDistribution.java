package edu.kit.ifv.mobitopp.actitopp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


//TODO Tim: Klasse untersuchen und checken

public class DiscreteDistribution
{
    private int startValue;
    private int endValue;

    // arraypos: n = x; (n+1) => x+y, y >=0 and y = relative share
    //
    private double[] distributionAsSum;

    private int[] occurences; // TODO: check if int is ok, or float should be
                              // used

    private int occurenceSum;
    
    //this is the last used temporary DTD, which is based upon this object. 
    //only used for debug purposes (printing of the decision process...)
    private DiscreteDistribution lastTempDTD;

    // the time distribution may have several values mapped to the same relative
    // share. we there save the range of this values as a KV-Pair
    // K: the first occurence of a specific value in the distributionAsSum List.
    // V: how large the range is. Minimum is 1
    private Map<Integer, Integer> sameValues;

    public DiscreteDistribution(int startValue, int endValue, double[] distributionAsSum, Map<Integer, Integer> sameValueMap, int[] shares)
    {
        super();
        this.startValue = startValue;
        this.endValue = endValue;
        this.distributionAsSum = distributionAsSum;
        this.sameValues = sameValueMap;
        this.occurences = shares;
        setOccurenceSum();
    }

    public DiscreteDistribution(DiscreteDistribution source)
    {
        super();
        this.startValue = source.startValue;
        this.endValue = source.endValue;
        this.distributionAsSum = Arrays.copyOf(source.distributionAsSum, source.distributionAsSum.length);
        this.occurences = Arrays.copyOf(source.occurences, source.occurences.length);
        //TODO:CHECK if deep copy ok...
        this.sameValues = copySameValues(sameValues);
        setOccurenceSum();
    }
    
    private Map<Integer, Integer> copySameValues(Map<Integer, Integer> originalMap)
    {
        if (originalMap == null) return new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> newMap = new HashMap<Integer, Integer>();
        for(Entry<Integer, Integer> entry : originalMap.entrySet())
        {
            Integer k = entry.getKey();
            Integer v = entry.getValue();
            
            Integer nk = new Integer(k.intValue());
            Integer nv = new Integer(v.intValue());
            
            newMap.put(nk, nv);
            
            
        }
        
        return newMap;
    }
    
    public DiscreteDistribution(DiscreteDistribution source, int lowerBound, int upperBound)
    {
        super();
        
        this.startValue = (lowerBound >= source.startValue) ? lowerBound : source.startValue;
        this.endValue = (upperBound <= source.endValue) ? upperBound : source.endValue;
        //this.distributionAsSum = Arrays.copyOf(source.distributionAsSum, source.distributionAsSum.length);
        //this.occurences = Arrays.copyOf(source.occurences, source.occurences.length);
        this.sameValues = copySameValues(sameValues);
        int startOffset = 0 + (lowerBound) - source.startValue;
        int endOffset = (source.endValue - (source.endValue - upperBound) - source.startValue) +1;
        int distributionSize = (endOffset - startOffset);
        this.distributionAsSum = new double[distributionSize];
        this.occurences = new int[distributionSize];
        
        for(int i = 0; i < occurences.length;i++)
        {
            this.occurences[i] = source.occurences[i+startOffset];
        }
        this.setOccurenceSum();
        this.reevaluateDistribution();

        
    }

    public void reevaluateDistribution()
    {

        double runningShareSum = 0.0d;
        //corner case: sometimes a dtd occurence sum is zero (in cases when a given dtd has been limited to a small range)
        //in that case, we override the occurences (even distribution assumed)
        if(occurenceSum == 0)
        {
            for(int i = 0; i < occurences.length;i++)
            {
                occurences[i] = 1;
            }
        }
        
        // Berechne die Summe neu, da sich durch die Modifizierung auch die Summe geändert hat
        this.setOccurenceSum();

        for (int i = 0; i < occurences.length; i++)
        {

            double shareOfOccurence = ((double) occurences[i]) /  occurenceSum;
            runningShareSum += shareOfOccurence;
            distributionAsSum[i] = runningShareSum;

        }
        
        // we must also re-evaluate the same value map
        double lastValue = distributionAsSum[0];
        double currValue = 0.0d;
        int sameValueRange = 0;
        sameValues.clear();
        for (int i = 1; i < distributionAsSum.length; i++)
        {
            currValue = distributionAsSum[i];

            if ((Math.abs(currValue - lastValue) < 0.000000001))
            {
                sameValueRange++;
            }
            else
            {
                if (sameValueRange >= 1)
                {
                    sameValues.put((i - 1) - sameValueRange, sameValueRange);
                }
                sameValueRange = 0;
            }

        }



    }

    public void setOccurenceSum()
    {
    	occurenceSum = 0;
        for (int i = 0; i < occurences.length; i++)
        {
            occurenceSum += occurences[i];
        }
    }

    public int getRandomPickFromDistribution(int lowerbound, int upperbound, RNGHelper randomgenerator)
    {
        //Phase1: check and apply bounds
        int usedLowerBound = startValue;
        int usedUpperBound = endValue;
                
        if(lowerbound != -1 && upperbound != -1)
        {
        	// Stelle sicher, dass Grenzen aus Vorbedingungen zu denen der gezogenen Zeitklasse passen!
        	assert lowerbound<=usedUpperBound : "Inconsistent Boundaries! lowerBound aus Vorbedingungen: " + lowerbound + " vs. upperBound der Kategorie: " + usedUpperBound;
          assert upperbound>=usedLowerBound : "Inconsistent Boundaries! upperBound aus Vorbedingungen: " + upperbound + " vs. lowerBound der Kategorie: " + usedLowerBound;

          if(lowerbound >= startValue && lowerbound <= endValue)
          {
              usedLowerBound = lowerbound;
          }
          
          if(upperbound >= startValue && upperbound <=endValue)
          {
              usedUpperBound = upperbound;
          }
        }
        
        if(lowerbound > upperbound)
        {
            System.err.println("time distribution bounds faulty: lower > upper... equalize");
            usedLowerBound = usedUpperBound;
        }
//        
//        int start = 0 + (usedLowerBound) - startValue;
//        int end = (endValue - (endValue - usedUpperBound) - startValue) +1;
        
        //Phase2: create temporary distribution table with limited range
        DiscreteDistribution tmpDTD = new DiscreteDistribution(this, usedLowerBound, usedUpperBound);
        lastTempDTD = tmpDTD;
        //Phase3: pick randomly
        double rand = randomgenerator.getRandomValue();
        for (int i = 0; i < tmpDTD.distributionAsSum.length; i++)
        {
            // TODO: check corner cases because of floating IEEE
            if (rand <= tmpDTD.distributionAsSum[i] && tmpDTD.sameValues.get(i) == null)
            {
                return tmpDTD.startValue + i;
            }
            else if (rand <= tmpDTD.distributionAsSum[i] && tmpDTD.sameValues.get(i) != null)
            { //new random pick for the chosen sameValue-Zone
                return tmpDTD.startValue + randomgenerator.getRandomValueBetween(i, i + tmpDTD.sameValues.get(i), 1);
            }

        }

        // should never occur
        System.err.print("time dist error...");
        return tmpDTD.endValue;
    }

    public int getStartPoint()
    {
        return startValue;
    }

    public void setStartPoint(int startPoint)
    {
        this.startValue = startPoint;
    }

    public int getEndPoint()
    {
        return endValue;
    }

    public void setEndPoint(int endPoint)
    {
        this.endValue = endPoint;
    }

    public double[] getDistributionAsSum()
    {
        return distributionAsSum;
    }

    public void setDistributionAsSum(double[] distributionAsSum)
    {
        this.distributionAsSum = distributionAsSum;
    }

    public int getShareSum()
    {
        return occurenceSum;
    }

    public void setShareSum(int shareSum)
    {
        this.occurenceSum = shareSum;
    }

    public int[] getShares()
    {
        return occurences;
    }

    public void setShares(int[] shares)
    {
        this.occurences = shares;
    }

    public DiscreteDistribution getLastTempDTD()
    {
        return lastTempDTD;
    }

    public void setLastTempDTD(DiscreteDistribution lastTempDTD)
    {
        this.lastTempDTD = lastTempDTD;
    }

}
