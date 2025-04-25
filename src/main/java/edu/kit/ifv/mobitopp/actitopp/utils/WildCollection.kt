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

fun <A, B, C> Iterable<A>.zip(

    second: Iterable<B>,
    third: Iterable<C>
): List<Triple<A, B, C>> {
    val iterator1 = iterator()
    val iterator2 = second.iterator()
    val iterator3 = third.iterator()

    val result = mutableListOf<Triple<A, B, C>>()
    while (iterator1.hasNext() && iterator2.hasNext() && iterator3.hasNext()) {
        result.add(Triple(iterator1.next(), iterator2.next(), iterator3.next()))
    }
    return result
}