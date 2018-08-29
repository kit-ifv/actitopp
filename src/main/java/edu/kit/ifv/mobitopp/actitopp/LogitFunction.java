package edu.kit.ifv.mobitopp.actitopp;

import java.util.List;

public class LogitFunction implements ChoiceFunction
{


  public void calculateProbabilities(List<ModelAlternative> alternatives)
  {
  	double utilitySum = 0.0d;
  	double probabilitySum = 0.0d;
  	
  	// Calculate utilitysum of all alternatives
    for (ModelAlternative ma : alternatives)
    {
      if (ma.isEnabled()) utilitySum += Math.exp(ma.getUtility());
    }
    
    // Calculate probability of each alternative based on utilitySum
    for (ModelAlternative ma : alternatives)
    {
    	if (ma.isEnabled()) 
    	{
    		double probability = Math.exp(ma.getUtility()) / utilitySum;
    		ma.setProbability(probability);
    		probabilitySum += probability;
    	}
    }
    //assert Math.round(probabilitySum*100)/100 == 1.0d:"wrong probability sum! (!=1.0d)";
  }
  
  
  @Override
  public int chooseAlternative(List<ModelAlternative> alternatives, double random)
  {
  	int choiceindex=-1;
    double movingSum = 0;
    for (int i=0; i<alternatives.size();i++)
    {
    	ModelAlternative ma = alternatives.get(i);
    	if (ma.isEnabled())
    	{
    		double movingsumnew = movingSum + ma.getProbability();
    		// Bedingung, dass random-Wert im Bereich zwischen letzter und aktueller Grenze liegt
        if (random>= movingSum && random <= movingsumnew)
        {
        	choiceindex=i;
        	break;
        }
        // Falls nicht, wird Schleife weiter durchgeführt
        movingSum = movingsumnew;
    	}
    }
    assert choiceindex!=-1 : "Konnte keine Wahl treffen!";
    return choiceindex;
  }

}
