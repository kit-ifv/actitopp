package edu.kit.ifv.mobitopp.actitopp.utilityFunctions
import kotlin.math.sign

/**
 * Picks the key from the map which corresponds to the representative value of the random number.
 */
fun <K> Map<K, Double>.select(random: Double): K {
    require(isNotEmpty()) {
        "Cannot pick a value from an empty map"
    }
    require(random in 0.0..1.0) {
        "Need a random value between 0 and 1"
    }
    val target = normalize().cumulativeSum()
    val ins = target.binarySearch { it.first.compareTo(random) }
    val choice = target[ins.toIndex()].second
    return choice
}

fun <K> Map<K, Double>.normalize(): Map<K, Double> {
    val sum = values.sum()
    val copy = this.toMutableMap()
    copy.forEach {
        copy[it.key] = it.value / sum
    }
    return copy
}

fun <K> Map<K, Double>.cumulativeSum(): List<Pair<Double, K>> {
    val cumSum = values.cumulativeSum()
    return cumSum.zip(keys)
}

fun <K, V> Map<K, V>.sortByValues(comparator: Comparator<V>): Map<K, V> {
    return this.toList()
        .sortedWith(compareBy(comparator) { it.second })
        .toMap()
}

fun <K, V> Map<K, V>.invertMap(): Map<V, List<K>> {
    return this.entries
        .groupBy({ it.value }, { it.key })
}

/**
 * Converts the return value of [binarySearch] to the index position
 */
fun Int.toIndex(): Int {
    return if (sign == -1) {
        -this - 1
    } else {
        this
    }
}

fun <M, K, V> M.append(key: K, value: V) where M : MutableMap<K, MutableList<V>> {
    if (key !in this) {
        this[key] = mutableListOf(value)
    } else {
        this[key]!!.add(value)
    }
}

fun Collection<Number>.cumulativeSum(): List<Double> {
    val result = mutableListOf<Double>()
    var sum = 0.0

    for (number in this) {
        sum += number.toDouble()
        result.add(sum)
    }

    return result
}

fun <K, V> MutableMap<K, V>.replaceOrRemoveAll(mapping: (K, V) -> V?) {
    val iterator = this.entries.iterator()
    while (iterator.hasNext()) {
        val entry = iterator.next()
        val newValue = mapping(entry.key, entry.value)

        // Remove the entry if the new value is null, update otherwise
        if (newValue == null) {
            iterator.remove()
        } else {
            entry.setValue(newValue)
        }
    }
}
