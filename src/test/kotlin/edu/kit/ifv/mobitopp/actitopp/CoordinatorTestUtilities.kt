package edu.kit.ifv.mobitopp.actitopp

import kotlin.math.abs

abstract class CoordinatorTestUtilities {
    protected  val fileBase: ModelFileBase = ModelFileBase()
    protected val randomgenerator: RNGHelper = RNGHelper(1234)
    protected fun generateIntUtilities(id: String, person: ActitoppPerson): Map<Int, Double> {


        val step = DCDefaultModelStep(id, fileBase, AttributeLookup(person), randomgenerator)
        step.doStep()
        return step.utilities(String::toInt)
    }
    protected fun <T> generateUtilities(id: String, person: ActitoppPerson, day: HDay, converter: (String) -> T): Map<T, Double> {
        val step = DCDefaultModelStep(id, fileBase, AttributeLookup(person, day), randomgenerator)
        step.doStep()
        return step.utilities(converter)
    }
    protected fun <K> coerceMap(map: Map<K, Double>): String {
        return map.entries.joinToString(", ") { "${it.key}=${it.value.toString().take(5).padEnd(5, '0')}"  }
    }

    protected fun <K> Map<K, Double>.minus(other: Map<K, Double>): Map<K, Double> {
        return entries.associate { it.key to it.value - (other[it.key] ?: 0.0 )}
    }
    protected fun DCDefaultModelStep.probabilities() = probabilities {it.toInt()}
    protected fun <T> DCDefaultModelStep.probabilities(converter: (String) -> T): Map<T, Double> {
        return alternatives.associate { converter(it.name) to it.probability }
    }



    fun <K> testDoubleMapEquality(
        expected: Map<K, Double>,
        actual: Map<K, Double>,
        delta: Double = 1e-8
    ): Boolean {
        require(expected.keys == actual.keys) {
            "Key mismatch: expected keys ${expected.keys}, got ${actual.keys}"
        }
        return expected.keys.all {
            val expectedValue = expected.getValue(it)
            val actualValue = actual.getValue(it)
            abs(expectedValue - actualValue) < delta
        }

    }
}
fun <T> DCDefaultModelStep.utilities(converter: (String) ->T ): Map<T, Double> {
    return alternatives.associate { converter(it.name) to it.utility }
}