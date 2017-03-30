package edu.kit.ifv.mobitopp.actitopp;


public interface ChoiceFunction
{
    /**
     * Calculates the probabilitites for the alternatives
     * @param utilityNumbers
     * @return
     */
    public double[] calculateProbabilities(double[] utilityNumbers);
    
    /**
     * Returns the index of the choice alternative that has been chosen
     * @param probabilities
     * @return
     */
    public int chooseAlternative(double[] probabilities, RNGHelper randomgenerator);
}
