package edu.kit.ifv.mobitopp.actitopp

data class UtilityDebug<T>(
    val options: Collection<T>,
    val utilities: Map<T, Double>,
    val probabilities: Map<T, Double>,
    val randomNumber: Double,
    val selection: T
)