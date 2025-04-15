package edu.kit.ifv.mobitopp.actitopp

import kotlin.math.exp

class LogitFunction : ChoiceFunction {
    override fun calculateProbabilities(alternatives: List<DCAlternative>) {
        var utilitySum = 0.0
        var probabilitySum = 0.0

        // Calculate utilitysum of all alternatives
        for (ma in alternatives) {
            if (ma.isEnabled) utilitySum += exp(ma.utility)
        }

        // Calculate probability of each alternative based on utilitySum
        for (ma in alternatives) {
            if (ma.isEnabled) {
                val probability = exp(ma.utility) / utilitySum
                ma.probability = probability
                probabilitySum += probability
            }
        }
        assert((Math.round(probabilitySum * 100) / 100).toDouble() == 1.0) { "wrong probability sum! (!=1.0d)" }
    }


    override fun chooseAlternative(alternatives: List<DCAlternative>, random: Double): Int {
        var choiceindex = -1
        var movingSum = 0.0
        for (i in alternatives.indices) {
            val ma = alternatives[i]
            if (ma.isEnabled) {
                val movingsumnew = movingSum + ma.probability
                if (random in movingSum..movingsumnew) {
                    choiceindex = i
                    break
                }
                movingSum = movingsumnew
            }
        }
        assert(choiceindex != -1) { "could not make a choice!" }
        return choiceindex
    }
}
