package edu.kit.ifv.mobitopp.actitoppNG.timebudgets

import edu.kit.ifv.mobitopp.actitoppNG.enums.Category
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.Identifier
import edu.kit.ifv.mobitopp.actitoppNG.utils.affineTransform
import edu.kit.ifv.mobitopp.actitoppNG.utils.ceilWholeMinutes
import edu.kit.ifv.mobitopp.actitoppNG.utils.indexBinarySearch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.useLines
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Original actitopp modified the distribution, it could do so because for every person the histogram was reconstructed
 * from file. Since we would like to perform the parsing process only once, we need some form of modifcation protection
 * so that the read content remain the same.
 */
class ModifiableArrayHistogram(offset: Int = 0, probabilities: DoubleArray, categoryIndex: Category) : ArrayHistogram(
    offset,
    probabilities,
    categoryIndex
) {
    /**
     * Takes an absolute position
     */
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

        require(probabilities.sum() in 0.99999..1.00001) {
            "Somehow the sum of probabilities is not 1.0 ${probabilities.sum()}"
        }
    }
}

/**
 * Using an array rather than a map allows quicker access and calculation. In particular because all histogram entries
 * in actiTopp are formulated over a range.
 */
@Serializable
open class ArrayHistogram protected constructor(
    protected val offset: Int = 0,
    protected val probabilities: DoubleArray = doubleArrayOf(0.0),
    val categoryIndex: Category,
) : Comparable<Int> {
    constructor(offset: Int, values: Collection<Number>, categoryIndex: Category) : this(
        offset,
        values.map { it.toDouble() / values.sumOf { it.toDouble() } }.toDoubleArray(),
        categoryIndex,
    )


    @Transient
    protected val size = probabilities.size

    @Transient
    private val _cumulativeSum: DoubleArray = DoubleArray(probabilities.size)
    val start = offset.minutes
    val end = (probabilities.size + offset - 1).minutes

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

    fun probabilities() = probabilities.toList()


    operator fun contains(position: Int): Boolean {
        return position in offset..<offset + size
    }

    operator fun contains(duration: Duration): Boolean {
        return duration.inWholeMinutes.toInt() in this
    }

    fun intersects(bounds: ClosedRange<Duration>): Boolean {
        if (bounds.start > this.end) return false
        if (bounds.endInclusive < this.start) return false
        return true
    }

    /**
     * Once copied, you may start modifying to your hearts content, but until then the histogram stays readonly.
     */
    fun copy(): ModifiableArrayHistogram = ModifiableArrayHistogram(offset, probabilities.clone(), categoryIndex)
    fun trim(): ArrayHistogram {
        val trimmedStartIndex =
            probabilities.withIndex().takeWhile { it.value == 0.0 }.lastOrNull()?.let { it.index + 1 } ?: 0
        val trimmedEndIndex =
            probabilities.withIndex().reversed().takeWhile { it.value == 0.0 }.lastOrNull()?.index ?: size
        return ArrayHistogram(
            offset = offset + trimmedStartIndex,
            probabilities = probabilities.copyOfRange(trimmedStartIndex, trimmedEndIndex),
            categoryIndex = categoryIndex,

            )
    }

    operator fun get(index: Int): Double {
        val relativeIndex = index - offset
        return if (relativeIndex in 0..<size) probabilities[relativeIndex] else 0.0.also {
            println("Cannot access probability for $index, since this histogram has only values for ${offset}..${offset + size - 1}")
        }
    }

    /**
     * Pick a value from the histogram using a random number between 0.0 and 1.0 as input, the random number is then
     * transformed using an affine translation to match the probability range of the cumulative sum of the elements within
     * the
     */
    private fun selectInt(
        randomNumber: Double,
        lowerBoundInclusive: Int? = null,
        upperBoundInclusive: Int? = null,
    ): Duration {
        require(randomNumber in 0.0..1.0) {
            "Input is not a probability as random Number $randomNumber"
        }

        val lb = lowerBoundInclusive?.let {
            val index = it - offset
            max(index, 0)
        } ?: 0
        val ub = upperBoundInclusive?.let { min(it - offset, size - 1) } ?: (size - 1)

        // We require the cumulative sum up to the current lower bound, which is stored in the field before the current index
        // If this field does not exist(because the index is 0) the cumulative sum before that element must be .0
        val lowerCumulativeProbability = _cumulativeSum.getOrNull(lb - 1) ?: 0.0

        val upperCumulativeProbability = _cumulativeSum[ub]
        val selectionIndex = if (lowerCumulativeProbability == upperCumulativeProbability) {
            /* This is the emergency situation. All elements in the targeted selection have a selection probability of
                0.0. In this scenario a number is picked uniformly between the lower bound and upper bound. Since
                the random number is known, we can use an affine transformation for the result.
            */
            val emergency = randomNumber.affineTransform(lb.toDouble(), ub.toDouble()).roundToInt()
            emergency
        } else {
            val affineRandomNumber =
                randomNumber.affineTransform(lowerCumulativeProbability, upperCumulativeProbability)
            _cumulativeSum.indexBinarySearch(affineRandomNumber, lb, ub)
        }



        return (selectionIndex + offset).minutes
    }
    context(rng: Random)
    fun select(bounds: ClosedRange<Duration>) = select(rng.nextDouble(), bounds)
    fun select(randomNumber: Double, bounds: ClosedRange<Duration>) =
        select(randomNumber, bounds.start, bounds.endInclusive)
    context(rng: Random)
    fun select(
        lowerBoundInclusive: Duration? = null,
        upperBoundInclusive: Duration? = null,
    ) = select(rng.nextDouble(), lowerBoundInclusive, upperBoundInclusive)

    fun select(
        randomNumber: Double,
        lowerBoundInclusive: Duration? = null,
        upperBoundInclusive: Duration? = null,
    ): Duration {
        val lb = lowerBoundInclusive?.ceilWholeMinutes
        val ub = upperBoundInclusive?.inWholeMinutes?.toInt()
        return selectInt(randomNumber, lb, ub)
    }


    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: Int): Int {
        if (end < other.minutes) return -1
        if (start > other.minutes) return 1
        return 0
    }

    override fun toString(): String {
        return "Histogram($categoryIndex)[$offset, ${offset + size - 1}]"
    }

    companion object {
        // The +1 is correct, the histogram path names do not match the reference numbers in the choice models... for some reason....
        fun fromPath(path: Path) = fromWRDDistribution(
            loadDistributionInformationFromFile(path), path.name.split('_').last().split('.').first().toInt() + 1
        ).trim()

        fun fromWRDDistribution(
            modelDistribution: Map<Int, Int>,
            categoryIndex: Int,
        ): ArrayHistogram {
            require(modelDistribution.keys.size == modelDistribution.keys.max() - modelDistribution.keys.min() + 1) {
                "Mismatch in the construction, some entries maybe missing?"
            }
            val sum = modelDistribution.values.sum()
            return ArrayHistogram(
                modelDistribution.keys.min(),
                modelDistribution.values.map { it.toDouble() / sum }.toDoubleArray(),
                Category(categoryIndex),

                )
        }

        fun fromFolder(
            path: Path = Path("src/main/resources/edu/kit/ifv/mobitopp/actitoppNG/mopv14_withpkwhh"),
            identifier: Identifier,
        ): List<ArrayHistogram> {
            return path.listDirectoryEntries("*${identifier.id}*").map { fromPath(it) }.sortedBy { it.categoryIndex }
        }

        fun fromResource(resourcePath: String): List<ArrayHistogram> {
            val inputStream = ArrayHistogram::class.java.getResourceAsStream(resourcePath)
                ?: throw NoSuchElementException("Resource of $resourcePath does not exist")

            return inputStream.reader().use {
                Json.decodeFromString(it.readText())
            }
        }

        fun fromResource(identifier: Identifier, prefix: String = "mop14_withpkwhh"): List<ArrayHistogram> {
            return fromResource("/$prefix/${identifier.resourcePath()}.json")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ArrayHistogram) return false
        return categoryIndex == other.categoryIndex &&
                offset == other.offset &&
                probabilities.contentEquals(other.probabilities)
    }

    override fun hashCode(): Int {
        var result = offset
        result = 31 * result + probabilities.contentHashCode()
        result = 31 * result + categoryIndex.hashCode()
        return result
    }

}


private fun loadDistributionInformationFromFile(path: Path): Map<Int, Int> {
    require(path.exists()) {
        "The path $path does not exist."
    }
    return path.useLines { lines ->
        val map = lines.drop(1).associate { line ->
            val split = line.split(";")
            val slot = split[0].toInt()
            val amount = split[1].toInt()
            slot to amount
        }
        map
    }


}