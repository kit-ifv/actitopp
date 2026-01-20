package edu.kit.ifv.discretechoice.extensions

import edu.kit.ifv.mobitopp.discretechoice.structure.EnumeratedStructureBuilder
import edu.kit.ifv.mobitopp.discretechoice.structure.loadFromList
import edu.kit.ifv.mobitopp.discretechoice.structure.loadFromMap

fun <T, A, C, P: List<T>> EnumeratedStructureBuilder<A, C, P>.optionsIndexed(vararg options: A, utilityFunction: T.(A, C) -> Double) {
    loadFromList(options.toList(), utilityFunction)
}

fun <T, A, C, P: List<T>> EnumeratedStructureBuilder<A, C, P>.optionsIndexed(options: Iterable<A>, utilityFunction: T.(A, C) -> Double) {
    loadFromList(options.toList(), utilityFunction)
}

fun <T, A, C, P: Map<A, T>> EnumeratedStructureBuilder<A, C, P>.loadOptionsMap(options: Iterable<A>, utilityFunction: T.(A, C) -> Double) {
    loadFromMap(options.toList(), utilityFunction)
}