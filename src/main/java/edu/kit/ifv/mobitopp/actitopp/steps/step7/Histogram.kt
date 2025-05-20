package edu.kit.ifv.mobitopp.actitopp.steps.step7

import edu.kit.ifv.mobitopp.actitopp.IO.loadDistributionInformationFromFile
import edu.kit.ifv.mobitopp.actitopp.WRDDiscreteDistribution
import edu.kit.ifv.mobitopp.actitopp.WRDModelDistributionInformation
import java.nio.file.Path
import java.util.Comparator
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Original actitopp modified the distribution, it could do so because for every person the histogram was reconstructed
 * from file. Since we would like to perform the parsing process only once, we need some form of modifcation protection
 * so that the read content remain the same.
 */
class ModifiableArrayHistogram (offset: Int = 0, probabilities: DoubleArray, categoryIndex: Int) : ArrayHistogram(
    offset,
    probabilities,
    categoryIndex
) {
    fun modify(position: Int) {
        require(position in this) {
            "Cannot update a position that is not present in the histogram. $position  ${offset}..${offset + size - 1}"
        }
        val index = position - offset
        val original = probabilities[index]
        val complementProbability = 1 - original
        val updatedProbability = original + complementProbability / 3
        val updatedComplement = 1 - updatedProbability
        val scalingFactor = updatedComplement / complementProbability
        probabilities.withIndex().forEach { (i, probability) ->
            probabilities[i] = probability * scalingFactor
        }
        probabilities[index] = updatedProbability

        cumulate()

        require(probabilities.sum() == 1.0) {
            "Somehow the sum of probabilities is not 1.0 ${probabilities.sum()}"
        }
    }
}

/**
 * Using an array rather than a map allows quicker access and calculation. In particular because all histogram entries
 * in actiTopp are formulated over a range.
 */
open class ArrayHistogram protected constructor(
    protected val offset: Int = 0,
    protected val probabilities: DoubleArray = doubleArrayOf(0.0),
    val categoryIndex: Int, // TODO the category index from legacy code should maybe be added somewhere else
): Comparable<Int> {
    constructor(offset: Int, values: Collection<Number>, categoryIndex: Int) : this(
        offset,
        values.map { it.toDouble() / values.sumOf { it.toDouble() } }.toDoubleArray(),
        categoryIndex
    )

    protected val size = probabilities.size
    private val _cumulativeSum: DoubleArray = DoubleArray(probabilities.size)

    val start = offset
    val end = probabilities.size + offset - 1
    val cumulativeSum get() = _cumulativeSum.asList()

    init {
        cumulate()
    }


    protected fun cumulate() {
        var counter = 0.0
        probabilities.withIndex().forEach { (index, probability) ->
            _cumulativeSum[index] = probability + counter
            counter += probability
        }
    }

    operator fun contains(position: Int): Boolean {
        return position in offset..offset + size
    }

    /**
     * Once copied, you may start modifying to your hearts content, but until then the histogram stays readonly.
     */
    fun copy(): ModifiableArrayHistogram = ModifiableArrayHistogram(offset, probabilities, categoryIndex)
    fun trim(): ArrayHistogram {
        val trimmedStartIndex =
            probabilities.withIndex().takeWhile { it.value == 0.0 }.lastOrNull()?.let { it.index + 1 } ?: 0
        val trimmedEndIndex =
            probabilities.withIndex().reversed().takeWhile { it.value == 0.0 }.lastOrNull()?.index ?: size
        return ArrayHistogram(
            offset = offset + trimmedStartIndex,
            probabilities = probabilities.copyOfRange(trimmedStartIndex, trimmedEndIndex),
            categoryIndex = categoryIndex
        )
    }

    operator fun get(index: Int): Double {
        val relativeIndex = index - offset
        return if (relativeIndex in 0..<size) probabilities[relativeIndex] else 0.0.also {
            println("Cannot access probability for $index, since this histogram has only values for ${offset}..${offset + size - 1}")
        }
    }

    /**
     * Instead of passing absolute bounds, you can also specify relative bounds. The result will however still be absolute
     */
    fun selectRelative(randomNumber: Double, lowerBoundRelative: Int? = null, upperBoundRelative: Int? = null): Duration {
        return select(randomNumber, lowerBoundRelative?.let { it + offset }, upperBoundRelative?.let { it + offset })
    }

    /**
     * Pick a value from the histogram using a random number between 0.0 and 1.0 as input, the random number is then
     * transformed using an affine translation to match the probabiliy range of the cumulative sum of the elements within
     * the
     */
    fun select(randomNumber: Double, lowerBoundInclusive: Int? = null, upperBoundInclusive: Int? = null): Duration {
        val lb = lowerBoundInclusive?.let {
            val index = it - offset
            if (index > 0) index - 1 else null
        }
        val ub = upperBoundInclusive?.let { it - offset } ?: (size - 1)

        require(randomNumber in 0.0..1.0) {
            "Input is not a probability as random Number $randomNumber"
        }
        val lowerCumulativeProbability = lb?.let { _cumulativeSum[it] } ?: 0.0
        val upperCumulativeProbability = _cumulativeSum[ub]
        val affineRandomNumber = randomNumber.affineTransform(lowerCumulativeProbability, upperCumulativeProbability)

        return (_cumulativeSum.indexBinarySearch(affineRandomNumber, lb ?: 0, ub) + offset).minutes
    }

    /**
     * Affine transform the target number so that the interval of 0..1 is mapped to lower..upper
     */
    private fun Double.affineTransform(lower: Double, upper: Double): Double {
        return (upper - lower) * this + lower
    }

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: Int): Int {
        if(end < other) return -1
        if(start > other) return 1
        return 0
    }

    override fun toString(): String {
        return "Histogram[$offset, ${offset + size - 1}]"
    }

    companion object {

        fun fromPath(path: Path) = fromWRDDistribution(loadDistributionInformationFromFile(path)
        ,path.name.split('_').last().split('.').first().toInt() + 1).trim()
        fun fromWRDDistribution(modelDistribution: WRDModelDistributionInformation, categoryIndex: Int): ArrayHistogram {
            require(modelDistribution.keys.size == modelDistribution.keys.max() - modelDistribution.keys.min() + 1) {
                "Mismatch in the construction, some entries maybe missing?"
            }
            val sum = modelDistribution.values.sum()
            return ArrayHistogram(
                modelDistribution.keys.min(),
                modelDistribution.values.map { it.toDouble() / sum }.toDoubleArray(),
                categoryIndex

            )
        }
    }
}

/**
 * In case we don't need the insertion, but are just interested in the position, we can invert the index of binary search
 */
fun DoubleArray.indexBinarySearch(element: Double, fromIndex: Int = 0, toIndex: Int = size): Int {
    val binarySearch = binarySearch(element, fromIndex, toIndex)
    return binarySearch.indexOfSearch()
}

fun Int.indexOfSearch(): Int {
    return if (this < 0) -this - 1 else this
}
fun main() {

    val wrdDistribution =
        loadDistributionInformationFromFile(Path("src/main/resources/edu/kit/ifv/mobitopp/actitopp/mopv14_withpkwhh/7B_KAT_0.csv"))
    val distribution = WRDDiscreteDistribution(wrdDistribution)
    val arrayHistogram = ArrayHistogram.fromWRDDistribution(wrdDistribution, 1)
    val rng = Random(1)
    for (i in 0..1000) {
        val range = arrayHistogram.start..arrayHistogram.end
        val first = range.random()
        val second = range.random()
        val bounds = min(first, second)..max(first, second)
        println("Running i$i $bounds")
        for (j in 0..10000) {
            val randomNumber = rng.nextDouble()
            val b = arrayHistogram.select(randomNumber, bounds.first, bounds.last).inWholeMinutes.toInt()
            val a = distribution.getRandomPickFromDistribution(bounds, randomNumber)
            if (a != b) {
                println("Big error i$i j$j")
            }
        }
    }

}