package edu.kit.ifv.mobitopp.actitopp;

import java.util.List;

public class LogitFunction implements ChoiceFunction
{


  public void calculateProbabilities(List<DCAlternative> alternatives)
  {
  	double utilitySum = 0.0d;
  	double probabilitySum = 0.0d;
  	
  	// Calculate utilitysum of all alternatives
    for (DCAlternative ma : alternatives)
    {
      if (ma.isEnabled()) utilitySum += Math.exp(ma.getUtility());
    }
    
    // Calculate probability of each alternative based on utilitySum
    for (DCAlternative ma : alternatives)
    {
    	if (ma.isEnabled()) 
    	{
    		double probability = Math.exp(ma.getUtility()) / utilitySum;
    		ma.setProbability(probability);
    		probabilitySum += probability;
    	}
    }
    assert Math.round(probabilitySum*100)/100 == 1.0d:"wrong probability sum! (!=1.0d)";
  }
  
  
  @Override
  public int chooseAlternative(List<DCAlternative> alternatives, double random)
  {
  	int choiceindex=-1;
    double movingSum = 0;
    for (int i=0; i<alternatives.size();i++)
    {
    	DCAlternative ma = alternatives.get(i);
    	if (ma.isEnabled())
    	{
    		double movingsumnew = movingSum + ma.getProbability();
        if (random>= movingSum && random <= movingsumnew)
        {
        	choiceindex=i;
        	break;
        }
        movingSum = movingsumnew;
    	}
    }
    assert choiceindex!=-1 : "could not make a choice!";
    return choiceindex;
  }

}
