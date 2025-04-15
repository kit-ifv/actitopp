package edu.kit.ifv.mobitopp.actitopp

/**
 * @author Tim Hilgert
 *
 *
 * object stores all distribution information of different categories (e.g. time classes) for a specific model step
 * needed to make a weighted random draw (wrd) decision
 */
class WRDModelSteplnformation {
    // contains all distribution information for different categories of the step
    // Robin: This does not need to be an explicit hashmap
    private val distributionInformation = mutableMapOf<String, WRDModelDistributionInformation>()

    /**
     * method to add distribution information for a step loaded from the file system
     *
     * @param category
     * @param distribution
     */
    fun addDistributionInformation(category: String, distribution: WRDModelDistributionInformation) {
        distributionInformation[category] = distribution
    }

    /**
     * Reduce visual clutter via operators: TODO kill the original function once behaviour tested
     */
    operator fun set(category: String, distribution: WRDModelDistributionInformation) = addDistributionInformation(category, distribution)
    /**
     * Robin: This method is only used in one place, and if the return value would be null the program would
     * run into a null pointer exception. In that case we can throw an exception, which actually provides meaningful
     * input to find the error instead of running into nullability issues.
     */
    fun getWRDDistribution(category: String): WRDModelDistributionInformation {
        return distributionInformation[category] ?: throw NoSuchElementException("No distribution information for category $category")
    }

    /**
     * Reduce visual clutter via operators: TODO kill the original function once behaviour tested
     */
    operator fun get(category: String) = getWRDDistribution(category)
}
