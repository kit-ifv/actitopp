package edu.kit.ifv.mobitopp.actitopp

import java.util.TreeMap

/**
 * represents the information of a distribution loaded from the file system
 *
 * @author Tim Hilgert
 */
class WRDModelDistributionInformation {
    /**
     * @return the distributionElements
     */
    /*
          * main information about the distribution
          * contains all elements with an identifier (e.g. duration in minutes) and their amount based on empirical data
          */
    val distributionElements: TreeMap<Int, Int> = TreeMap()

    /**
     * @param slot
     * @param amount
     */
    fun addDistributionElement(slot: Int, amount: Int) {
        distributionElements[slot] = amount
    }
}
