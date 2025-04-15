package edu.kit.ifv.mobitopp.actitopp

interface ChoiceFunction {
    /**
     * Calculates the probabilitites for the alternatives
     *
     * @param alternatives
     */
    fun calculateProbabilities(alternatives: List<DCAlternative>)

    /**
     * Returns the index of the choice alternative that has been chosen
     *
     * @param alternatives
     * @param random
     * @return
     */
    fun chooseAlternative(alternatives: List<DCAlternative>, random: Double): Int
}
