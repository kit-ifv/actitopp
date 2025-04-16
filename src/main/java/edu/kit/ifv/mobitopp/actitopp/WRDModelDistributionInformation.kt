package edu.kit.ifv.mobitopp.actitopp

import java.util.TreeMap

/**
 * represents the information of a distribution loaded from the file system
 *
 * @author Tim Hilgert
 */
class WRDModelDistributionInformation(val distributionElements: MutableMap<Int, Int> = mutableMapOf()): MutableMap<Int, Int> by distributionElements {


    /**
     * @param slot
     * @param amount
     */
    fun addDistributionElement(slot: Int, amount: Int) {
        distributionElements[slot] = amount
    }
}
