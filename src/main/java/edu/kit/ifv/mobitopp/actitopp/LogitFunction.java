package edu.kit.ifv.mobitopp.actitopp;

import java.util.List;

public class LogitFunction implements ChoiceFunction
{


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
