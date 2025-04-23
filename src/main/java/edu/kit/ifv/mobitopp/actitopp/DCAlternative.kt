package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.deprecated.UtilityFunction

/**
 * @author Tim Hilgert
 */
class DCAlternative( var name: String) {
    
    val utilityFunction: UtilityFunction = UtilityFunction()


    /**
     * @return the probability
     */
    /**
     * @param probability the probability to set
     */
    
    var probability: Double = -1.0
    /**
     * @return the enabled
     */
    /**
     * @param enabled the enabled to set
     */
    var isEnabled: Boolean = true

    /*
     * Faktor to weight utility.
     * by default, factor is equal to 1, i.e. no additional
     * weithing. Some model steps used weighting factor to ensure
     * stability aspects, i.e. setting it to 1.1 is equal to a 10%
     * raising of the utility.
     *
     * factor is inclued when getUtility
     */
    private var utilityweithingfactor = 1.0

    /**
     * @param utilityfactor the utilityfactor to set
     */
    fun setUtilityfactor(utilityfactor: Double) {
        this.utilityweithingfactor = utilityfactor
    }

    val utility: Double
        /**
         * @return the utility*utilityFactor
         */
        get() = utilityFunction.utility * utilityweithingfactor

    override fun toString(): String {
        return name
    }
}
