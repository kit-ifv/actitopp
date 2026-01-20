package edu.kit.ifv.mobitopp.actitoppNG

import edu.kit.ifv.mobitopp.actitoppNG.enums.Category
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.choicemodels.LEAD
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.choicemodels.MAJOR
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.choicemodels.MINOR
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.ArrayHistogram
import edu.kit.ifv.mobitopp.actitoppNG.utils.affineTransform
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.provider.CsvSource
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.time.Duration.Companion.minutes

class ArrayHistogramTest {
    private val histogram = ArrayHistogram(offset = 0, listOf(1, 1, 1, 1, 1), Category(1))
    @Test
    fun boundedSelectionTakesProperElements() {
        val histogram = ArrayHistogram(offset = 0, listOf(1, 1, 1, 1, 1), Category(1))
        assertEquals(histogram[0], 0.2)
        assertEquals(histogram[2], 0.2)

        assertEquals(histogram.select(0.5), 2.minutes)

        assertEquals(histogram.select(0.45, lowerBoundInclusive = 0.minutes, upperBoundInclusive = 1.minutes), 0.minutes)
    }

    @Test
    fun properContainsCheck() {
        val histogram = ArrayHistogram.fromPath(Path("src/main/resources/edu/kit/ifv/mobitopp/actitoppNG/mopv14_withpkwhh/8C_KAT_8.csv"))
        val earlierHistogram = ArrayHistogram.fromPath(Path("src/main/resources/edu/kit/ifv/mobitopp/actitoppNG/mopv14_withpkwhh/8C_KAT_7.csv"))
        assertFalse(earlierHistogram.contains(360))
        assertTrue(histogram.contains(360))
        assertTrue(histogram.contains(361))
    }
    @Test
    fun taintingWorks() {
        val histogram = ArrayHistogram.fromPath(Path("src/main/resources/edu/kit/ifv/mobitopp/actitoppNG/mopv14_withpkwhh/8C_KAT_5.csv"))
        val originalProbabilities = histogram.probabilities()
        val taint = histogram.copy()
        assertContentEquals(originalProbabilities, histogram.probabilities())
        taint.modify(181)
        assertContentEquals(originalProbabilities, histogram.probabilities())

    }
    @ParameterizedTest
    @CsvSource(
        "0.0, 0, 5, 0",
        "0.33, 1, 3, 1",
        "0.00, 1, 3, 1",
        "0.00, 3, 4, 3",
        "0.01, 3, 4, 3",
        "0.00, 2, 4, 2",
        "0.33, 2, 4, 2",
        "0.1, 2, 4, 2",
        "0.99, 2, 4, 4",


    )
    fun uniformBoundChecks(randomNumber: Double, lowerBound: Double, upperBound: Double, expected: Int) {
        val lb = lowerBound.minutes
        val ub = upperBound.minutes
        val result = expected.minutes
        assertEquals(result, histogram.select(randomNumber, lb, ub))
    }
    @Test
    fun affineTransformation() {
        assertEquals(0.5.affineTransform(0.8, 1.0), 0.9)
        assertEquals(0.2.affineTransform(0.0, 10.0), 2.0)
    }
    @Test
    fun emergencyBehaviour() {
        val histogram = ArrayHistogram(
            offset = 0,
            values = listOf(0, 0, 0, 0, 1),
            categoryIndex = Category.NONE_CHOSEN
        )
        val selection = histogram.select(0.2, 2.minutes, 3.minutes)
        assertEquals(selection, 2.minutes)
    }
    @Test
    fun assertCategoryEquality() {
        val lead = LEAD
        val major = MAJOR
        val minor = MINOR
        lead.histograms.withIndex().forEach { (i, it) ->
            assertEquals(it.categoryIndex, major.histograms[i].categoryIndex)
            assertEquals(it.categoryIndex, minor.histograms[i].categoryIndex)
            assertEquals(it.start, major.histograms[i].start)
            assertEquals(it.start, minor.histograms[i].start)
        }
    }

}