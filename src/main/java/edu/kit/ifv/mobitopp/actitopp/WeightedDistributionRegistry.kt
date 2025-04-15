package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.changes.Category

/**
 * All references to a weighted distribution seem to run over a step id and a category. We can simplify the original
 * weird map of map process in this class.
 */
class WeightedDistributionRegistry {
    private val distributionMap: MutableMap<String, MutableMap<Category, WRDModelDistributionInformation>> = mutableMapOf()

    operator fun get(stepID: String): Map<Category, WRDModelDistributionInformation> {
        return distributionMap[stepID]?: throw NoSuchElementException("No Map Defined for step $stepID")
    }
    operator fun get(stepID: String, category: Category): WRDModelDistributionInformation {
        return get(stepID)[category] ?: throw NoSuchElementException("No Distribution found for category $category in step $stepID")
    }

    operator fun set(stepID: String, category: Category, distribution: WRDModelDistributionInformation) {
        distributionMap.getOrPut(stepID) { mutableMapOf() }[category] = distribution
    }
}