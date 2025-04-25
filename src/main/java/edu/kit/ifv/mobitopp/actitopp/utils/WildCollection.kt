package edu.kit.ifv.mobitopp.actitopp.utils

fun <T> Iterable<T>.zipWithPrevious(): List<Pair<T?, T>> {
    val result = mutableListOf<Pair<T?, T>>()
    var previous: T? = null
    for (current in this) {
        result.add(previous to current)
        previous = current
    }
    return result
}