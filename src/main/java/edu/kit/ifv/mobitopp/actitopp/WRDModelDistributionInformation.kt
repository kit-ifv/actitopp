package edu.kit.ifv.mobitopp.actitopp

import java.util.TreeMap

/**
 * represents the information of a distribution loaded from the file system
 *
 * @author Tim Hilgert
 */
class WRDModelDistributionInformation(private val distributionElements: Map<Int, Int> =mapOf()): Map<Int, Int> by distributionElements
