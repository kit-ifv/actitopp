package edu.kit.ifv.mobitopp.actitopp;

import java.util.List;

public class LogitFunction implements ChoiceFunction
{

  @Override
  @Deprecated
  public double[] calculateProbabilities(double[] utilityNumbers)
  {
    double[] probabilities = new double[utilityNumbers.length];
    double utilitySum = 0.0d;
    
    for (int i = 0; i < utilityNumbers.length; i++)
    {
      utilitySum +=Math.exp(utilityNumbers[i]);
    }
    
    for (int i = 0; i < utilityNumbers.length; i++)
    {
      probabilities[i] = (Math.exp(utilityNumbers[i]) / utilitySum);
    }
    return probabilities;
  }
  

  public void calculateProbabilities(List<ModelAlternative> alternatives)
  {
  	double utilitySum = 0.0d;
    for (ModelAlternative ma : alternatives)
    {
      if (ma.isEnabled()) utilitySum += Math.exp(ma.getUtility());
    }
    
    for (ModelAlternative ma : alternatives)
    {
    	if (ma.isEnabled()) ma.setProbability(Math.exp(ma.getUtility()) / utilitySum);
    }
  }
  

  @Override
  public int chooseAlternative(double[] probabilities, double random)
  {
    int ptr = 0;
    double movingSum = probabilities[0];
    while (ptr < probabilities.length)
    {
      if (random <= movingSum)
      {
        break;
      }
      else
      {
        movingSum += probabilities[ptr + 1];
        ptr++;
      }
    }
    return ptr;
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
    		// Bedingung, dass random-Wert im Bereich zwischen letzter und aktueller Grenze liegt
        if (random>= movingSum && random <= movingSum + ma.getProbability())
        {
        	choiceindex=i;
        	break;
        }
        // Falls nicht, wird Schleife weiter durchgeführt
        movingSum += ma.getProbability();
    	}
    }
    assert choiceindex!=-1 : "Konnte keine Wahl treffen!";
    return choiceindex;
  }

}
