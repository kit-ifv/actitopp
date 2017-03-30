package edu.kit.ifv.mobitopp.actitopp;

public class LogitFunction implements ChoiceFunction
{

  @Override
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

  @Override
  public int chooseAlternative(double[] probabilities, RNGHelper randomgenerator)
  {
    double random = randomgenerator.getRandomValue();

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

}
