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
    private val distributionInformation =
        HashMap<String, WRDModelDistributionInformation>()

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
     * method to get distribution information of a specified category
     *
     * @param category
     * @return
     */
    fun getWRDDistribution(category: String): WRDModelDistributionInformation? {
        return distributionInformation[category]
    }
}
