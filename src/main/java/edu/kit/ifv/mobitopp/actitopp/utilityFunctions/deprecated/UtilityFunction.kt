package edu.kit.ifv.mobitopp.actitopp.utilityFunctions.deprecated

/**
 * @author Tim Hilgert
 */
class UtilityFunction {
    private var baseWeight = 0.0
    private val parameterattributeCombinations =
        ArrayList<UtilityParameterAttributeCombination>()

    fun setBaseWeight(baseWeight: Double) {
        this.baseWeight = baseWeight
    }

    fun addParameterAttributeCombination(combination: UtilityParameterAttributeCombination) {
        parameterattributeCombinations.add(combination)
    }

    val utility: Double
        get() {
            var utility = 0.0 + baseWeight
            for (pair in parameterattributeCombinations) {
                utility += (pair.getattributeValue() * pair.getparameterValue())
            }

            return utility
        }

    fun printUtilityDetails() {
        print("Base utility: $baseWeight")
        for (pair in parameterattributeCombinations) {
            print(pair.name + ":" + pair.getattributeValue() + "*" + pair.getparameterValue())
            print(" __ ")
        }
        println(
            """
                
                TOTAL UTILITY: ${utility}
                """.trimIndent()
        )
    }
}
