package edu.kit.ifv.mobitopp.actitopp

class WeightedDistributionRegistry {
    private val distributionMap: MutableMap<String, MutableMap<String, WRDModelDistributionInformation>> = mutableMapOf()

    operator fun get(stepID: String): Map<String, WRDModelDistributionInformation> {
        return distributionMap[stepID]?: throw NoSuchElementException("No Map Defined for step $stepID")
    }
    operator fun get(stepID: String, category: String): WRDModelDistributionInformation {
        return get(stepID)[category] ?: throw NoSuchElementException("No Distribution found for category $category in step $stepID")
    }

    operator fun set(stepID: String, category: String, distribution: WRDModelDistributionInformation) {
        distributionMap.getOrPut(stepID) { mutableMapOf() }[category] = distribution
    }
}