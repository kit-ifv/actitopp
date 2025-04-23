package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.IO.ModelFileBase

class LinRegDefaultCalculation(
    private val regressionname: String,
    private val fileBase: ModelFileBase,
    private val attributeLookup: AttributeLookup
) {
    var estimatesMap: MutableMap<String, LinRegEstimate> =
        HashMap()

    fun initializeEstimates() {
        // copy the parameters loaded frome file base to the decision of this modeling step

        for (key in fileBase.getLinearRegressionEstimates(regressionname).keys) {
            val fromFileBase = fileBase.getLinearRegressionEstimates(regressionname)[key] ?: throw NoSuchElementException("$regressionname not found")
            val estimate =
                LinRegEstimate(fromFileBase.name, fromFileBase.estimateValue, fromFileBase.contextIdentifier)
            estimatesMap[key] = estimate
        }

        // read attribute values for estimates
        for (key in estimatesMap.keys) {
            val estimate = estimatesMap[key] ?: throw Exception("estimate not found")

            if (key == "Grundnutzen" || key == "Intercept") {
                estimate.attributeValue = 1.0
            } else {
                var attributeValue = 0.0
                attributeValue = attributeLookup.getAttributeValue(estimate.contextIdentifier, estimate.name)
                estimate.attributeValue = attributeValue
            }
        }
    }

    /**
     * calculate the linear combination of the estimates and the according attributes
     *
     * @return
     */
    fun calculateRegression(): Double {
        var result = 0.0

        // Estimates auslesen und Linearkombination bilden
        for (key in estimatesMap.keys) {
            val estimate = estimatesMap[key]
            result += (estimate!!.estimateValue * estimate.attributeValue)
        }

        return result
    }
}
