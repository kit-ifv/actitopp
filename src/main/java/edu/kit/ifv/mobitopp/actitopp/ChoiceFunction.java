package edu.kit.ifv.mobitopp.actitopp;

import java.util.List;

public interface ChoiceFunction
{

    /**
     * Calculates the probabilitites for the alternatives
     * @param alternatives
     * @return
     */
    public void calculateProbabilities(List<ModelAlternative> alternatives);
    
    /**
     * Returns the index of the choice alternative that has been chosen
     * @param alternatives
     * @param random
     * @return
     */
    public int chooseAlternative(List<ModelAlternative> alternatives, double random);
}
